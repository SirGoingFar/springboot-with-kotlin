package com.eemf.chatservice.service

interface MessageService {

  fun latest(): List<MessageVM>

  fun after(messageId: String): List<MessageVM>

  fun post(message: MessageVM)
}