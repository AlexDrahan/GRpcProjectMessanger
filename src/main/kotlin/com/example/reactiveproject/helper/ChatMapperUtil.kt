package com.example.reactiveproject.helper

import com.example.reactiveproject.Services
import com.example.reactiveproject.id
import com.example.reactiveproject.model.Chat
import com.example.reactiveproject.model.FullChat
import com.example.reactiveproject.model.Message
import com.example.reactiveproject.model.User
import reactor.core.publisher.Mono

fun grpcToChat(grpcChat: Mono<Services.ChatDescription>): Chat {

    var userList = mutableSetOf<String>()
    for (i in grpcChat.block()!!.userIdsList){
        userList.add(i)
    }

    return grpcChat.block().let {
        Chat(
            name = it!!.name,
            messageIds = it.messageIdsList,
            userIds = userList,
        )
    }
}

fun grpcToChatUnMono(grpcChat: Services.ChatDescription): Chat {

    var userList = mutableSetOf<String>()
    for (i in grpcChat!!.userIdsList){
        userList.add(i)
    }

    return grpcChat.let {
        Chat(
            name = it!!.name,
            messageIds = it.messageIdsList,
            userIds = userList,
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

fun updateGrpcToChat(request: Mono<Services.ChatUpdateRequest>): Pair<String, String> {
    return request.block().let {
        it!!.chatId.toString() to it.userId.toString()
    }
}


fun fullChatToGrpc(request: FullChat): Services.FullChatResponse{
    var messageList = mutableListOf<Message>()
    for (i in request.messageList){
        messageList.add(i)
    }
    var userList = mutableListOf<User>()
    for (i in request.userList){
        userList.add(i)
    }
    return Services.FullChatResponse
        .newBuilder()
        .apply {
            chat = chatToGrpcRequest(request.chat)
            userList = userList
            messageList = messageList
        }
        .build()
}

