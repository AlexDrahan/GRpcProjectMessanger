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
import reactor.kotlin.core.publisher.toMono

@GRpcService
class GrpcChatService: ReactorChatServiceGrpc.ChatServiceImplBase() {
    @Autowired
    lateinit var chatService: ChatService

    override fun findAllChats(request: Mono<Empty>?): Flux<Services.ChatResponse> {
        return chatService.findAllChats()
            .flatMap { chatToGrpcMono(it) }
    }

    override fun createChat(request: Mono<Services.ChatDescription>?): Mono<Services.ChatResponse> {
        return  grpcToChat(request!!).log("1")
            .flatMap { chatService.createChat(it) }.log("2")
            .map { chatToGrpc(it) }.log("3")
    }

    override fun deleteChat(request: Mono<Services.id>?): Mono<Services.DeleteAnswer> {
        return idToGrpc(request!!)
            .flatMap { chatService.deleteChat(it) }
            .then(Services.DeleteAnswer.newBuilder().apply { text = "Chat is deleted" }.build().toMono())
    }

    override fun addUserToTheChat(request: Mono<Services.ChatUpdateRequest>?): Mono<Services.ChatResponse> {
        return updateGrpcToChat(request!!)
            .flatMap { chatService.addUserToTheChat(it.first, it.second) }
            . map { chatToGrpc(it) }
    }

    override fun deleteUserFromChat(request: Mono<Services.ChatUpdateRequest>?): Mono<Services.ChatResponse> {
        return updateGrpcToChat(request!!)
            .flatMap { chatService.deleteUserFromChat(it.first, it.second) }
            . map { chatToGrpc(it) }
    }

    override fun getChatById(request: Mono<Services.id>?): Mono<Services.FullChatResponse> {
        return idToGrpc(request!!)
            .flatMap { chatService.getChatById(it) }
            .map { fullChatToGrpcMono(it) }
    }
}