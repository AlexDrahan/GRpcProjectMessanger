package com.example.reactiveproject.grpc
import com.example.reactiveproject.ReactorUserServiceGrpc
import com.example.reactiveproject.Services
import com.example.reactiveproject.UserServiceGrpcKt
import com.example.reactiveproject.helper.*
import com.example.reactiveproject.service.UserService
import com.google.protobuf.Empty
import com.google.protobuf.empty
import org.lognet.springboot.grpc.GRpcService
import org.springframework.beans.factory.annotation.Autowired
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono


@GRpcService
class GrpcUserService: ReactorUserServiceGrpc.UserServiceImplBase(){
    @Autowired
    lateinit var userService: UserService

    override fun findUserById(request: Mono<Services.id>?): Mono<Services.UserResponse> {
        return userService.findByUserId(idToGrpc(request!!))
            .flatMap {
                userToGrpcMono(it!!)
            }
    }

    override fun findUserByUserName(request: Mono<Services.name>?): Mono<Services.UserResponse> {
        return userService.findUserByUserName(userNameToGrpc(request!!))
            .flatMap{
                userToGrpcMono(it!!)
            }
    }


    override fun findUserByPhoneNumber(request: Mono<Services.phoneNumber>?): Mono<Services.UserResponse> {
        return userService.findUserByPhoneNumber(phoneNumberToGrpc(request!!))
            .flatMap{
                userToGrpcMono(it!!)
            }
    }

    override fun createUser(request: Mono<Services.UserDescription>?): Mono<Services.UserResponse> {
        return userService.createUser(grpcToUser(request!!))
            .flatMap{
                userToGrpcMono(it)
            }
    }

    override fun deleteUser(request: Mono<Services.id>?): Mono<Empty> {
        return Mono.just(empty { userService.deleteUser(idToGrpc(request!!)) })
    }

    override fun updateUser(request: Mono<Services.UserUpdateRequest>?): Mono<Services.UserResponse> {
        val pairReq = updateGrpcToUser(request)
        return userService.updateUser(pairReq.first, pairReq.second)
            .flatMap { userToGrpcMono(it) }
    }
}