package com.eemf.chatservice

import com.eemf.chatservice.repository.Message
import com.eemf.chatservice.service.MessageVM
import java.time.temporal.ChronoUnit.MILLIS

fun MessageVM.prepareForTesting() = copy(id = null, sent = sent.truncatedTo(MILLIS))

fun Message.prepareForTesting() = copy(id = null, sent = sent.truncatedTo(MILLIS))