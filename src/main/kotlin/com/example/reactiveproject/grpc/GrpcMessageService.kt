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
        return messageService.findMessage(textToGrpc(request!!))
            .map {
                messageToGrpc(it!!)
            }
    }

    override fun sendMessage(request: Mono<Services.MessageDescription>?): Mono<Services.MessageResponse> {
        return messageService.sendMessage(grpcToMessage(request!!))
            .map {
                messageToGrpc(it)
            }
    }

    override fun deleteMessage(request: Mono<Services.id>?): Mono<Empty> {
        return Mono.just(empty { messageService.deleteMessage(idToGrpc(request!!)) })
    }

    override fun editMessage(request: Mono<Services.MessageUpdateRequest>?): Mono<Services.MessageResponse> {
        val pairReq = updateGrpcToMessage(request)
        return messageService.editMessage(pairReq.first, pairReq.second)
            .map {
                updateMessageToGrpc(it)
            }
    }
}