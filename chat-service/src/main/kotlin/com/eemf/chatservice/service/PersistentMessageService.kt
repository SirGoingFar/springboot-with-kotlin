package com.eemf.chatservice.service

import com.eemf.chatservice.asDomainObject
import com.eemf.chatservice.mapToViewModel
import com.eemf.chatservice.repository.MessageRepository
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service
@Primary
class PersistentMessageService(val messageRepository: MessageRepository) : MessageService {

  override fun latest(): List<MessageVM> = messageRepository.findLatest().mapToViewModel()

  override fun after(messageId: String): List<MessageVM> = messageRepository.findLatest(messageId).mapToViewModel()

  override fun post(message: MessageVM) {
    messageRepository.save(message.asDomainObject())
  }

}