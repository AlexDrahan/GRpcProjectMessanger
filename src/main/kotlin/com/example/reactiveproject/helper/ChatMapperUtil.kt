package com.example.reactiveproject.helper

import com.example.reactiveproject.Services
import com.example.reactiveproject.id
import com.example.reactiveproject.model.Chat
import com.example.reactiveproject.model.FullChat
import com.example.reactiveproject.model.Message
import com.example.reactiveproject.model.User
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

fun grpcToChat(grpcChat: Mono<Services.ChatDescription>): Mono<Chat> {

    return grpcChat.map {
        Chat(
            name = it!!.name,
            messageIds = it.messageIdsList,
            userIds = it.userIdsList.toMutableSet()
        )
    }
}

fun grpcToChatUnMono(grpcChat: Services.ChatDescription): Chat {

    return grpcChat.let {
        Chat(
            name = it!!.name,
            messageIds = it.messageIdsList,
            userIds = it.userIdsList.toMutableSet(),
        )
    }
}

fun chatToGrpc(chat : Chat): Services.ChatResponse{
    return Services.ChatResponse
        .newBuilder()
        .apply {
            id = chat.id
            name = chat.name
            addAllMessageIds(chat.messageIds)
            addAllUserIds(chat.userIds)
        }
        .build()
}

fun chatToGrpcMono(it : Chat): Mono<Services.ChatResponse>{
    return Services.ChatResponse
            .newBuilder()
            .apply {
                id = it.id
                name = it.name
                addAllMessageIds(it.messageIds)
                addAllUserIds(it.userIds)
            }
            .build()
        .toMono()

}

fun chatToGrpcDescription(chat : Chat): Services.ChatDescription{
    return Services.ChatDescription
        .newBuilder()
        .apply {
            name = chat.name
            addAllMessageIds(chat.messageIds)
            addAllUserIds(chat.userIds)
        }
        .build()
}
fun chatToGrpcRequest(chat : Chat): Services.ChatDescription{


    return Services.ChatDescription
        .newBuilder()
        .apply {
            chat.id = chat.id
            name = chat.name
            addAllMessageIds(chat.messageIds)
            addAllUserIds(chat.userIds)
        }
        .build()
}

fun updateGrpcToChat(request: Mono<Services.ChatUpdateRequest>): Mono<Pair<String, String>> {
    return request.map {
        it!!.chatId.toString() to it.userId.toString()
    }
}


fun fullChatToGrpc(request: Mono<FullChat>): Mono<Services.FullChatResponse>{

    return request.map {
        Services.FullChatResponse
            .newBuilder()
            .apply {
                chat = chatToGrpcRequest(it.chat)
                addAllMessageList(it.messageList.map { messageToGrpc(it) })
                addAllUserList(it.userList.map { userToGrpcDescription(it) })
            }
            .build()
    }
}
fun fullChatToGrpcMono(it: FullChat): Services.FullChatResponse {

    return Services.FullChatResponse
            .newBuilder()
            .apply {
                chat = chatToGrpcRequest(it.chat)
                addAllMessageList(it.messageList.map { messageToGrpc(it) })
                addAllUserList(it.userList.map { userToGrpcDescription(it) })
            }
            .build()

}

