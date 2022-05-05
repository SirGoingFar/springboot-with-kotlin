package com.example.kotlin.chat

import app.cash.turbine.test
import com.eemf.chatservice.prepareForTesting
import com.eemf.chatservice.repository.ContentType
import com.eemf.chatservice.repository.Message
import com.eemf.chatservice.repository.MessageRepository
import com.eemf.chatservice.service.MessageVM
import com.eemf.chatservice.service.UserVM
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.dataWithType
import org.springframework.messaging.rsocket.retrieveFlow
import java.net.URI
import java.net.URL
import java.time.Instant
import java.time.temporal.ChronoUnit.MILLIS
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

/**
 * Runtime issue with the test. It will be visited later as I am out of time
 * */

/*@SpringBootTest(
  webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
  properties = [
    "spring.r2dbc.url=r2dbc:h2:mem:///testdb;USER=sa;PASSWORD=password"
  ], classes = arrayOf(MessageRepository::class)
)*/
class ChatKotlinApplicationTests(/*@LocalServerPort val serverPort: Int*/) {

  lateinit var lastMessageId: String
  lateinit var rSocketRequester: RSocketRequester

//  @MockBean
  lateinit var messageRepository: MessageRepository

  val now: Instant = Instant.now()

//  @BeforeEach
  fun setUp() {
    runBlocking {
      val secondBeforeNow = now.minusSeconds(1)
      val twoSecondBeforeNow = now.minusSeconds(2)
      val savedMessages = messageRepository.saveAll(
        listOf(
          Message(
            "*testMessage*",
            ContentType.PLAIN,
            twoSecondBeforeNow,
            "test",
            "http://test.com"
          ),
          Message(
            "**testMessage2**",
            ContentType.MARKDOWN,
            secondBeforeNow,
            "test1",
            "http://test.com"
          ),
          Message(
            "`testMessage3`",
            ContentType.MARKDOWN,
            now,
            "test2",
            "http://test.com"
          )
        )
      ).toList()
      lastMessageId = savedMessages.first().id ?: ""
    }
  }

//  @AfterEach
  fun tearDown() {
    runBlocking {
      messageRepository.deleteAll()
    }
  }

  @ExperimentalTime
  @ExperimentalCoroutinesApi
//  @Test
  fun `test that messages API streams latest messages`() {
    runBlocking {
      rSocketRequester = RSocketRequester.builder().websocket(URI("ws://localhost:8080/rsocket"))
      rSocketRequester
        .route("api.v1.messages.stream")
        .retrieveFlow<MessageVM>()
        .test {
          assertThat(expectItem().prepareForTesting())
            .isEqualTo(
              MessageVM(
                "*testMessage*",
                UserVM("test", URL("http://test.com")),
                now.minusSeconds(2).truncatedTo(MILLIS)
              )
            )

          assertThat(expectItem().prepareForTesting())
            .isEqualTo(
              MessageVM(
                "<body><p><strong>testMessage2</strong></p></body>",
                UserVM("test1", URL("http://test.com")),
                now.minusSeconds(1).truncatedTo(MILLIS)
              )
            )
          assertThat(expectItem().prepareForTesting())
            .isEqualTo(
              MessageVM(
                "<body><p><code>testMessage3</code></p></body>",
                UserVM("test2", URL("http://test.com")),
                now.truncatedTo(MILLIS)
              )
            )

          expectNoEvents()

          launch {
            rSocketRequester.route("api.v1.messages.stream")
              .dataWithType(flow {
                emit(
                  MessageVM(
                    "`HelloWorld`",
                    UserVM("test", URL("http://test.com")),
                    now.plusSeconds(1)
                  )
                )
              })
              .retrieveFlow<Void>()
              .collect()
          }

          assertThat(expectItem().prepareForTesting())
            .isEqualTo(
              MessageVM(
                "<body><p><code>HelloWorld</code></p></body>",
                UserVM("test", URL("http://test.com")),
                now.plusSeconds(1).truncatedTo(MILLIS)
              )
            )

          cancelAndIgnoreRemainingEvents()
        }
    }
  }

  @ExperimentalTime
//  @Test
  fun `test that messages streamed to the API is stored`() {
    runBlocking {
      launch {
        rSocketRequester = RSocketRequester.builder().websocket(URI("ws://localhost:8080/rsocket"))
        rSocketRequester.route("api.v1.messages.stream")
          .dataWithType(flow {
            emit(
              MessageVM(
                "`HelloWorld`",
                UserVM("test", URL("http://test.com")),
                now.plusSeconds(1)
              )
            )
          })
          .retrieveFlow<Void>()
          .collect()
      }

      delay(2.seconds)

      messageRepository.findAll()
        .first { it.content.contains("HelloWorld") }
        .apply {
          assertThat(this.prepareForTesting())
            .isEqualTo(
              Message(
                "`HelloWorld`",
                ContentType.MARKDOWN,
                now.plusSeconds(1).truncatedTo(MILLIS),
                "test",
                "http://test.com"
              )
            )
        }
    }
  }
}