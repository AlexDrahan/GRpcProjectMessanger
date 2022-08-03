package com.example.reactiveproject.service.impl

import com.example.reactiveproject.model.Chat
import com.example.reactiveproject.model.FullChat
import com.example.reactiveproject.model.Message
import com.example.reactiveproject.model.User
import org.springframework.beans.factory.annotation.Autowired
import com.example.reactiveproject.repository.ChatRepository
import com.example.reactiveproject.repository.FullChatRepository
import com.example.reactiveproject.repository.MessageRepository
import com.example.reactiveproject.repository.UserRepository
import com.example.reactiveproject.service.ChatService
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Service
class ChatServiceImpl(
    val chatRepository: ChatRepository,
    val userRepository: UserRepository?,
    val fullChatRepository: FullChatRepository,
    val messageRepository: MessageRepository?
) : ChatService {

    override fun createChat(chat: Chat){
        chatRepository.save(chat)
            .subscribe()
    }

    override fun deleteChat(id: String) {
        chatRepository.deleteById(id).subscribe()
    }

    override fun addUserToTheChat(chatId: String, userId: String) {

        userRepository!!.findById(userId)
            .switchIfEmpty(
                Mono.error(NotFoundException())
            )
            .then(
                chatRepository.findChatById(chatId)
            )
            .map {
                it!!.userIds
                Chat(
                    id = chatId,
                    name = it.name,
                    messageIds = it.messageIds,
                    userIds = it.userIds!!.plus(userId)
                )
            }
            .flatMap {
                chatRepository.save(it)
            }
            .subscribe()
    }


    override fun deleteUserFromChat(chatId: String, userId: String) {

        userRepository!!.findById(userId)
            .switchIfEmpty(
                Mono.error(NotFoundException())
            )
            .then(
                chatRepository.findChatById(chatId)
            )
            .map {
                it!!.userIds
                Chat(
                    id = chatId,
                    name = it.name,
                    messageIds = it.messageIds,
                    userIds = it.userIds!!.minus(userId)
                )
            }
            .flatMap {
                chatRepository.save(it)
            }
            .subscribe()
    }


    override fun findAllChats(): Flux<Chat> {
        return chatRepository.findAll()
    }

    override fun getChatById(chatId: String): Mono<FullChat> {

        val userF = chatRepository.findChatById(chatId)
            .switchIfEmpty(
                Mono.error(NotFoundException())
            )
            .flatMapIterable {
                it!!.userIds!!
            }
            .flatMap {
                userRepository!!.findById(it)
            }
            .collectList()

        val messageF = chatRepository.findChatById(chatId)
            .switchIfEmpty(
                Mono.error(NotFoundException())
            )
            .flatMapIterable {
                it!!.messageIds!!
            }
            .flatMap {
                messageRepository!!.findById(it)
            }
            .collectList()

        val chat = chatRepository.findChatById(chatId)

        return Mono.zip(chat, userF, messageF)
            .flatMap {
                fullChatRepository.save(FullChat(
                    chat = it.t1,
                    userList = it.t2,
                    messageList = it.t3
                ))
            }



    }


}
