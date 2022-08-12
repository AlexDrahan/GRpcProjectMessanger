package com.example.reactiveproject.grpc

import com.example.reactiveproject.ReactorMessageServiceGrpc
import com.example.reactiveproject.Services
import com.example.reactiveproject.helper.*
//import com.example.reactiveproject.helper.messageToGrpc
import com.example.reactiveproject.service.MessageService
import com.google.protobuf.Empty
import com.google.protobuf.empty
import org.lognet.springboot.grpc.GRpcService
import org.springframework.beans.factory.annotation.Autowired
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@GRpcService
class GrpcMessageService: ReactorMessageServiceGrpc.MessageServiceImplBase(){
    @Autowired
    lateinit var messageService: MessageService

    override fun findMessage(request: Mono<Services.text>?): Flux<Services.MessageResponse> {
        return textToGrpc(request!!)
            .flatMapMany { messageToGrpc(messageService.findMessage(it)) }
    }

    override fun sendMessage(request: Mono<Services.MessageDescription>?): Mono<Services.MessageResponse> {
        return grpcToMessage(request!!)
            .flatMap { messageService.sendMessage(it) }
            .map { messageToGrpcUnMono(it) }

    }

    override fun deleteMessage(request: Mono<Services.id>?): Mono<Services.DeleteAnswer> {
        return idToGrpc(request!!)
            .flatMap { messageService.deleteMessage(it) }
            .then(Services.DeleteAnswer.newBuilder().apply { text = "Message is deleted" }.build().toMono())
    }

    override fun editMessage(request: Mono<Services.MessageUpdateRequest>?): Mono<Services.MessageResponse> {
        return updateGrpcToMessage(request)
            .flatMap { messageService.editMessage(it.first, it.second)}
            .map { updateMessageToGrpcUnMono(it) }

    }
}