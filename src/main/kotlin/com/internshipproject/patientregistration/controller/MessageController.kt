package com.internshipproject.patientregistration.controller

import com.internshipproject.patientregistration.dto.MessageModel
import com.internshipproject.patientregistration.dto.ResponseMessageModel
import com.internshipproject.patientregistration.repository.mongo.ChatMessageRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/messages")
class MessageController(private val chatMessageRepository: ChatMessageRepository) {

    @GetMapping("/{senderId}/{receiverId}")
    fun getMessagedPeople(@PathVariable senderId : String, @PathVariable receiverId: String): List<ResponseMessageModel> {
        val messages = chatMessageRepository.findMessagesBetweenUsers(senderId,receiverId)

        return messages.map {
            ResponseMessageModel(it.message,it.sender,it.recipient, readStatus = it.readStatus,  photoData = it.photoData)
        }
    }




}