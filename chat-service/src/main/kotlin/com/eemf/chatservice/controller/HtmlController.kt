package com.eemf.chatservice.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HtmlController {

  @GetMapping("/")
  suspend fun index(model: Model): String {
    return "chatrs"
  }

}