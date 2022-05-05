package com.eemf.chatservice.repository

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param

interface MessageRepository : CoroutineCrudRepository<Message, String> {

  @Query(
    """
        SELECT * FROM (
            SELECT * FROM messages
            ORDER BY "SENT" DESC
            LIMIT 10
        ) ORDER BY "SENT"
    """
  )
  fun findLatest(): Flow<Message>

  @Query(
    """
        SELECT * FROM (
            SELECT * FROM messages
            WHERE SENT > (SELECT SENT FROM messages WHERE ID = :id)
            ORDER BY "SENT" DESC
        ) ORDER BY "SENT"
    """
  )
  fun findLatest(@Param("id") id: String): Flow<Message>
}