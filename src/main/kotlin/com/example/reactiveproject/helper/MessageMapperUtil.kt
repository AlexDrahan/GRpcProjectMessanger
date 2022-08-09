package com.example.reactiveproject.helper

import com.example.reactiveproject.Services
import com.example.reactiveproject.model.Chat
import com.example.reactiveproject.model.Message
import reactor.core.publisher.Mono
import java.time.LocalDateTime


fun textToGrpc(grpcText: Mono<Services.text>): String{
    return grpcText.block()!!.text.toString()
}

fun grpcToMessage(message: Mono<Services.MessageDescription>): Message {
    return message.block().let {
        Message(
            text = it!!.text,
            messageChatId = it.messageChatId,
            messageUserId = it.messageUserId
        )
    }
}

fun messageToGrpc(messages: Message): Services.MessageResponse{

    return Services.MessageResponse.newBuilder().apply {
        id = messages.id.toString()
        dateTime = timestampFromLocalDate(LocalDateTime.parse(messages.datetime))
        text = messages.text
        messageChatId = messages.messageUserId
        messageUserId = messages.messageChatId
        }
        .build()

}

fun updateGrpcToMessage(request: Mono<Services.MessageUpdateRequest>?): Pair<String, Message>{
    return request!!.block().let {
        it!!.messageId.toString() to Message(
            text = it.message.text
        )
    }
}
fun updateMessageToGrpc(message: Message): Services.MessageResponse{
    return Services.MessageResponse
        .newBuilder()
        .apply {
            text = message.text
            dateTime = timestampFromLocalDate(LocalDateTime.parse(message.datetime))
        }
        .build()
}