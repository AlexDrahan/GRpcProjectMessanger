package com.example.reactiveproject.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.FieldType
import org.springframework.data.mongodb.core.mapping.MongoId

@Document("messages")
data class Message(
    @MongoId(value = FieldType.OBJECT_ID)
    val id: String? = null,
    val datetime: String,
    val text: String,
    val messageChatId: String = "",
    val messageUserId: String = ""
)
