package com.example.reactiveproject.helper

import com.example.reactiveproject.Services
import com.example.reactiveproject.Services.UserDescription
import com.example.reactiveproject.model.Chat
import com.example.reactiveproject.model.Message
import com.example.reactiveproject.model.User
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDateTime

//for method find by userName
fun userNameToGrpc(grpcId: Mono<Services.name>): String{
    return grpcId.block()!!.name.toString()
}
//for method find by phoneNumber
fun phoneNumberToGrpc(grpcPhoneNumber: Mono<Services.phoneNumber>): String{
    return grpcPhoneNumber.block()!!.phoneNumber.toString()
}

fun userToGrpcMono(user: User): Mono<Services.UserResponse>{
    var chatList = mutableListOf<Chat>()
    for (i in user.chat){
        chatList.add(i)
    }
    var messageList = mutableListOf<Message>()
    for (i in user.message){
        messageList.add(i)
    }

    val grpcUser = Services.UserResponse
        .newBuilder()
        .apply {
            id = user.id
            bio = user.bio
            name = user.name
            phoneNumber = user.phoneNumber
            messageList = messageList
            chatList = chatList
        }
        .build()
        .toMono()

    return grpcUser
}



fun grpcToUser(grpcUser: Mono<Services.UserDescription>): User {

    return grpcUser.block()
        .let {
            User(
                name = it!!.name,
                phoneNumber = it.name,
                bio = it.bio,
                chat = it.chatList.map {
                    grpcToChatUnMono(it)
                },
                message = it.messageList.map {
                    grpcToMessage(it.toMono())
                }
            )
        }
}

fun updateGrpcToUser(request: Mono<Services.UserUpdateRequest>?): Pair<String, User>{
    return request!!.block().let {
        it!!.userId.toString() to User(
            id = it.userId.toString(),
            it.user.name,
            it.user.phoneNumber,
            it.user.bio,
            it.user.chatList.map {
                grpcToChatUnMono(it)
            },
            it.user.messageList.map {
                grpcToMessage(it.toMono())
            }
        )
    }
}

//fun updateUserToGrpc(user: User): Services.UserResponse{
//    return Services.UserResponse
//        .newBuilder()
//        .apply {
//            id = user.id
//            bio = user.bio
//            name = user.name
//            phoneNumber = user.phoneNumber
//            messageList = messageList
//            chatList = chatList
//        }
//        .build()
//}