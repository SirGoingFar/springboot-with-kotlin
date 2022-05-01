package com.eemf.blogservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(BlogProperties::class)
class BlogServiceApplication

fun main(args: Array<String>) {
  runApplication<BlogServiceApplication>(*args)
}
