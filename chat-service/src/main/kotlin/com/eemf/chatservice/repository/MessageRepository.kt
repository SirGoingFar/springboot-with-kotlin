package com.eemf.chatservice.repository

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface MessageRepository : CrudRepository<Message, String> {

  // language=SQL
  @Query("""
        SELECT * FROM (
            SELECT * FROM messages
            ORDER BY "SENT" DESC
            LIMIT 10
        ) ORDER BY "SENT"
    """)
  fun findLatest(): List<Message>

  // language=SQL
  @Query("""
        SELECT * FROM (
            SELECT * FROM messages
            WHERE SENT > (SELECT SENT FROM messages WHERE ID = :id)
            ORDER BY "SENT" DESC
        ) ORDER BY "SENT"
    """)
  fun findLatest(@Param("id") id: String): List<Message>
}