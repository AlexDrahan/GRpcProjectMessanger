package com.example.reactiveproject.grpc

import com.example.reactiveproject.ReactorChatServiceGrpc
import com.example.reactiveproject.Services
import com.example.reactiveproject.helper.*
import com.example.reactiveproject.service.ChatService
import com.google.protobuf.Empty
import com.google.protobuf.empty
import org.lognet.springboot.grpc.GRpcService
import org.springframework.beans.factory.annotation.Autowired
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux

@GRpcService
class GrpcChatService: ReactorChatServiceGrpc.ChatServiceImplBase() {
    @Autowired
    lateinit var chatService: ChatService

    override fun findAllChats(request: Mono<Empty>?): Flux<Services.ChatResponse> {
        return chatService.findAllChats()
            .map {
                chatToGrpc(it)
            }
    }

    override fun createChat(request: Mono<Services.ChatDescription>?): Mono<Services.ChatResponse> {
        return  grpcToChat(request!!)
            .map { chatService.createChat(it) }
            .flatMap { chatToGrpcMono(it) }
    }

    override fun deleteChat(request: Mono<Services.id>?): Mono<Empty> {
        return idToGrpc(request!!)
            .map { chatService.deleteChat(it) }
            .then(Mono.empty())
    }

    override fun addUserToTheChat(request: Mono<Services.ChatUpdateRequest>?): Mono<Services.ChatResponse> {
        return updateGrpcToChat(request!!)
            .map { chatService.addUserToTheChat(it.first, it.second) }
            . flatMap { chatToGrpcMono(it) }
    }

    override fun deleteUserFromChat(request: Mono<Services.ChatUpdateRequest>?): Mono<Services.ChatResponse> {
        return updateGrpcToChat(request!!)
            .map { chatService.deleteUserFromChat(it.first, it.second) }
            . flatMap { chatToGrpcMono(it) }
    }

    override fun getChatById(request: Mono<Services.id>?): Mono<Services.FullChatResponse> {
        return idToGrpc(request!!)
            .map { chatService.getChatById(it) }
            .flatMap { fullChatToGrpc(it) }
    }
}