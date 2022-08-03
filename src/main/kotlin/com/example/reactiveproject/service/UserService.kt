package com.example.reactiveproject.service

import com.example.reactiveproject.model.User
import reactor.core.publisher.Mono


interface UserService {

    fun createUser(user: User)

    fun deleteUser(id: String)

    fun updateUser(id: String, user: User): Mono<User>

    fun findUserByPhoneNumber(phoneNumber: String): Mono<User?>

    fun findUserByUserName(name: String): Mono<User?>

    fun findByUserId(id: String): Mono<User?>
}