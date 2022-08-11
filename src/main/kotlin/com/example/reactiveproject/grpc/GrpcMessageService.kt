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
            .map { messageService.sendMessage(it) }
            .flatMap { messageToGrpcMono(it) }

    }

    override fun deleteMessage(request: Mono<Services.id>?): Mono<Empty> {
        return idToGrpc(request!!)
            .map {messageService.deleteMessage(it)}
            .then(Mono.empty())

    }

    override fun editMessage(request: Mono<Services.MessageUpdateRequest>?): Mono<Services.MessageResponse> {
        return updateGrpcToMessage(request)
            .map { messageService.editMessage(it.first, it.second)}
            .flatMap { updateMessageToGrpcMono(it) }

    }
}