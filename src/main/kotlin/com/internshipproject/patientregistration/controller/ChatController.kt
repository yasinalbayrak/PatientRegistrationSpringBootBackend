package com.internshipproject.patientregistration.controller

import com.internshipproject.patientregistration.dto.MessageModel
import com.internshipproject.patientregistration.dto.MessageStatus
import com.internshipproject.patientregistration.dto.ReadStatus
import com.internshipproject.patientregistration.dto._internal.UserDTO
import com.internshipproject.patientregistration.entity.chat.ChatMessage
import com.internshipproject.patientregistration.repository.mongo.ChatMessageRepository
import com.internshipproject.patientregistration.service.UserService
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.time.LocalTime
import java.util.*
import kotlin.jvm.optionals.getOrNull


@Controller
class ChatController(
    private val messagingTemplate: SimpMessagingTemplate,
    private val chatMessageRepository: ChatMessageRepository,
    private val userService: UserService
) {

    @MessageMapping("/message")
    @SendTo("/chatroom/public")
    fun sendPublicMessage(

        @Payload message: MessageModel,
        headerAccessor: SimpMessageHeaderAccessor
    ): MessageModel {
        message.user = userService.getUser(message.senderName.toInt()).getOrNull()?.let {
            UserDTO(
                it.id, it.firstName, it.lastName, it.email, it.passw, it.gender, it.age, it.roles.map { role -> role.name }[0]
            )
        }
        return message

    }

    @MessageMapping("/private-message")
    fun sendPrivateMessage(
        @Payload message: MessageModel,
        headerAccessor: SimpMessageHeaderAccessor
    ): MessageModel {
        // Convert Base64 encoded photo data to ByteArray

        if(message.status == MessageStatus.MESSAGE){
            val photoData: ByteArray? = message.photoData?.let {
                Base64.getDecoder().decode(it.split(",")[1])
            }


            val messageEntity = ChatMessage(
                sender = message.senderName,
                recipient = message.receiverName,
                message = message.message,
                photoData = photoData,
                readStatus = message.readStatus,
                timestamp= message.timestamp
            )

            chatMessageRepository.save(messageEntity)
        } else if (message.status == MessageStatus.PRIVATE_JOIN){
            message.user = userService.getUser(message.senderName.toInt()).getOrNull()?.let {
                UserDTO(
                    it.id, it.firstName, it.lastName, it.email, it.passw, it.gender, it.age, it.roles.map { role -> role.name }[0], activeInChat = message.receiverName
                )
            }

            val currentTime = System.currentTimeMillis()

            val allMessages: List<ChatMessage> = chatMessageRepository.findBySenderAndRecipient(
                message.receiverName,
                message.senderName
            ).reversed().map { originalMessage ->
                if (
                    //originalMessage.timestamp <= currentTime &&
                    originalMessage.readStatus != ReadStatus.SEEN) {
                    originalMessage.readStatus = ReadStatus.SEEN
                }
                originalMessage
            }
            chatMessageRepository.saveAll(allMessages)

        } else if ( message.status == MessageStatus.LEAVE){
            
        }

        messagingTemplate.convertAndSendToUser(message.receiverName, "/private", message)

        return message
    }



}