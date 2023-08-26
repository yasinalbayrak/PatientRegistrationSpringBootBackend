package com.internshipproject.patientregistration.repository.mongo

import com.internshipproject.patientregistration.entity.chat.ChatMessage
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface ChatMessageRepository : MongoRepository<ChatMessage, String> {
    fun findBySenderAndRecipient(sender: String, recipient: String): List<ChatMessage>

    @Query("{'\$or':[{'\$and':[{'sender': ?0}, {'recipient': ?1}]}, {'\$and':[{'sender': ?1}, {'recipient': ?0}]}]}")
    fun findMessagesBetweenUsers(user1Id: String, user2Id: String): List<ChatMessage>



}
