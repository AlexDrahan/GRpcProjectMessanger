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
        return  chatService.createChat(grpcToChat(request!!))
            .map {
                chatToGrpc(it)
            }
    }

    override fun deleteChat(request: Mono<Services.id>?): Mono<Empty> {
        return Mono.just(empty { chatService.deleteChat(idToGrpc(request!!)) })
    }

    override fun addUserToTheChat(request: Mono<Services.ChatUpdateRequest>?): Mono<Empty> {
        val pairReq = updateGrpcToChat(request!!)
        return Mono.just(empty { chatService.addUserToTheChat(pairReq.first, pairReq.second) })
    }

    override fun deleteUserFromChat(request: Mono<Services.ChatUpdateRequest>?): Mono<Empty> {
        val pairReq = updateGrpcToChat(request!!)
        return Mono.just(empty { chatService.deleteUserFromChat(pairReq.first, pairReq.second) })
    }

    override fun getChatById(request: Mono<Services.id>?): Mono<Services.FullChatResponse> {
        return chatService.getChatById(idToGrpc(request!!))
            .map {
                fullChatToGrpc(it)
            }
    }
}