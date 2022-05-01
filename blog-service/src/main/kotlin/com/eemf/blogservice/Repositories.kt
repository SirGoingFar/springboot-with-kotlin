package com.eemf.blogservice

import com.eemf.blogservice.data.Article
import com.eemf.blogservice.data.User
import org.springframework.data.repository.CrudRepository

interface ArticleRepository : CrudRepository<Article, Long> {
  fun findBySlug(slug: String): Article?
  fun findAllByOrderByAddedAtDesc(): Iterable<Article>
}

interface UserRepository : CrudRepository<User, Long> {
  fun findByLogin(login: String): User?
}