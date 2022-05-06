package com.eemf.chatservice

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientException
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient
import java.net.URI
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.time.ZonedDateTime
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.function.Function

/**
 * Read more: https://www.baeldung.com/spring-5-webclient
 * */
@Component
class RestCallHandler {

  fun getClientOne(): WebClient = WebClient.create()

  fun getClientTwo(): WebClient = WebClient.create("baseUrl")

  fun getClientThree(): WebClient = WebClient.builder()
    .baseUrl("http://localhost:8080")
    .defaultCookie("cookieKey", "cookieValue")
    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
    .defaultUriVariables(Collections.singletonMap("url", "http://localhost:8080"))
    .build()

  fun getClientWithTimeoutConfig(): WebClient {
    val httpClient: HttpClient = HttpClient.create()
      .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
      .responseTimeout(Duration.ofMillis(5000))
      .doOnConnected { conn ->
        conn.addHandlerLast(ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS))
          .addHandlerLast(WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS))
      }

    return WebClient.builder().clientConnector(ReactorClientHttpConnector(httpClient)).build()
  }

  fun sendRequest() {

    /**
     * Prepare Method
     * */
    val reqBodySpec = getClientOne().method(HttpMethod.POST)
    reqBodySpec.uri("/path")

    //OR
    val _reqBodySpec = getClientOne().post().uri(Function { builder -> builder.pathSegment("/path").build() })

    //OR
    val __reqBodySpec = getClientOne().post().uri(URI.create("/path"))

    /**
     * Prepare Body
     * */
    reqBodySpec.bodyValue(Any())

    //OR
    _reqBodySpec.body(Mono.just(Data("name")), Data::class.java)

    //OR
    __reqBodySpec.body(BodyInserters.fromValue(Data("name")))

    //OR
    __reqBodySpec.body(BodyInserters.fromPublisher(Mono.just(Data("name")), Data::class.java), Data::class.java)

    //OR
    val map = LinkedMultiValueMap<String, String>().apply {
      add("key1", "value1")
      add("key2", "value2")
    }
    __reqBodySpec.body(BodyInserters.fromMultipartData(map), LinkedMultiValueMap::class.java)

      /**
       * Add other headers
       * */
      .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
      .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
      .acceptCharset(StandardCharsets.UTF_8)
      .ifNoneMatch("*")
      .ifModifiedSince(ZonedDateTime.now())

    /**
     * Get response
     * */
    var response: Mono<String?>? = __reqBodySpec.exchangeToMono { response -> //or exchangeToFlux
      if (response.statusCode().equals(HttpStatus.OK)) {
        return@exchangeToMono response.bodyToMono(String::class.java)
      } else if (response.statusCode().is4xxClientError()) {
        return@exchangeToMono Mono.just("Error response")
      } else {
        return@exchangeToMono response.createException().flatMap { Mono.error(it) }
      }
    }

    //OR
    try {
      response = __reqBodySpec.retrieve().bodyToMono(String::class.java)
    } catch (ex: WebClientException) {
      //Do something
    }

  }

  data class Data(val name: String)
}