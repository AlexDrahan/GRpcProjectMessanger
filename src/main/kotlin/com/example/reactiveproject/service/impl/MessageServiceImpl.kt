package com.example.reactiveproject.service.impl

import com.example.reactiveproject.model.Message
import org.springframework.beans.factory.annotation.Autowired
import com.example.reactiveproject.repository.MessageRepository
import com.example.reactiveproject.service.MessageService
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Service
class MessageServiceImpl(
    @Autowired
    val messageRepository: MessageRepository
): MessageService {


    override fun sendMessage(message: Message) {
        val messageSend: Message = Message(
            id = message.id,
            text = message.text,
            messageChatId = message.messageChatId,
            messageUserId = message.messageUserId,
            datetime = LocalDateTime.now().toString())
        messageRepository.save(messageSend).subscribe()
    }

    override fun deleteMessage(messageId: String) {
        messageRepository.deleteById(messageId).subscribe()
    }

    override fun editMessage(id: String, message: Message): Mono<Message> {
        return messageRepository.findMessageById(id)
            .map {
                Message(
                    id = id,
                    text = message.text,
                    datetime = LocalDateTime.now().toString())
            }
            .flatMap(
                messageRepository::save
            )
    }

    override fun findMessage(text: String): Flux<Message?> {
        return messageRepository.findMessageByText(text)
            .switchIfEmpty(
                Mono.error(NotFoundException())
            )
    }


}