package com.example.reactiveproject.grpc


import com.example.reactiveproject.ReactorUserServiceGrpc
import com.example.reactiveproject.model.User
import com.example.reactiveproject.repository.MessageRepository
import com.example.reactiveproject.repository.UserRepository
import com.example.reactiveproject.service.impl.UserServiceImpl
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.core.publisher.Mono

@SpringBootTest
@ExtendWith(SpringExtension::class)
@Import(UserServiceImpl::class)
internal class GrpcUserControllerTest{

    @MockBean
    lateinit var userRepository: UserRepository

    private val channel: ManagedChannel = ManagedChannelBuilder
        .forAddress("localhost", 6565)
        .usePlaintext()
        .build()

    private var serviceStub: ReactorUserServiceGrpc.ReactorUserServiceStub =
        ReactorUserServiceGrpc.newReactorStub(channel)


    fun randomName(): String = List(10) {
        (('a'..'z') + ('A'..'Z')).random()
    }.joinToString("")
    private fun randomPhone(): String = List(12) {
        (('0'..'9')).random()
    }.joinToString("")
    private fun randomBio(): String = List(80) {
        (('a'..'z')).random()
    }.joinToString("")


    fun prepareData(): User {
        val user = User(
            id = ObjectId.get().toString(),
            name = randomName(),
            phoneNumber = "+" + randomPhone(),
            bio = randomBio(),
            chat = emptyList(),
            message = emptyList()
        )
        return user
    }

    @Test
    fun `should return user by id`(){
        val user = prepareData()

        Mockito.`when`(userRepository.findById(user.id!!)).thenReturn(Mono.just(user))
    }
}