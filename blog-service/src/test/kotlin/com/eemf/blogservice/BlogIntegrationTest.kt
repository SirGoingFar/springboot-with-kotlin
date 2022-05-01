package com.eemf.blogservice

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import java.net.URI

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BlogIntegrationTest(@Autowired val restTemplate: TestRestTemplate, @LocalServerPort val port: Int) {

  @BeforeAll
  fun setup() {
    println(">> Setup")
  }

  @Test
  fun `Assert blog page title, content and status code`() {
    val entity = restTemplate.getForEntity(URI.create("/"), String::class.java)
    assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
    assertThat(entity.body).contains("<h1>Blog</h1>")
  }

  @Test
  fun `Assert article page title, content and status code`() {
    println(">> Assert article page title, content and status code")
    val title = "Reactor Aluminium has landed"
    val entity = restTemplate.getForEntity(URI.create("/article/${title.toSlug()}"), String::class.java)
    assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
    assertThat(entity.body).contains(title, "Lorem ipsum", "dolor sit amet")
  }

  @AfterAll
  fun teardown() {
    println(">> Tear down")
  }

}