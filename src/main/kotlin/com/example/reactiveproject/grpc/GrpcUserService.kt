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
        return idToGrpc(request!!)
            .map { userService.findByUserId(it) }
            .flatMap { userToGrpc(it) }
    }

    override fun findUserByUserName(request: Mono<Services.name>?): Mono<Services.UserResponse> {
        return userNameToGrpc(request!!)
            .map {userService.findUserByUserName(it)}
            .flatMap { userToGrpc(it) }

    }

    override fun findUserByPhoneNumber(request: Mono<Services.phoneNumber>?): Mono<Services.UserResponse> {
        return phoneNumberToGrpc(request!!)
            .map {userService.findUserByPhoneNumber(it)}
            .flatMap { userToGrpc(it) }
    }

    override fun createUser(request: Mono<Services.UserDescription>?): Mono<Services.UserResponse> {
        return grpcToUser(request!!)
            .map { userService.createUser(it) }
            .flatMap { userToGrpcForCreate(it) }
    }

    override fun deleteUser(request: Mono<Services.id>?): Mono<Empty> {
        return idToGrpc(request!!)
            .map { userService.deleteUser(it) }
            .flatMap { Mono.empty() }
    }

    override fun updateUser(request: Mono<Services.UserUpdateRequest>?): Mono<Services.UserResponse> {
        return updateGrpcToUser(request)
            .map { userService.updateUser(it.first, it.second)}
            .flatMap { updateUserToGrpc(it) }

    }
}