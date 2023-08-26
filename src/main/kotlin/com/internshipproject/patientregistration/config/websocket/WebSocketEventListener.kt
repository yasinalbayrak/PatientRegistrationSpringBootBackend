package com.internshipproject.patientregistration.config.websocket/*
package com.internshipproject.patientregistration.config.websocket

import com.internshipproject.patientregistration.entity.chat.ChatMessage
import com.internshipproject.patientregistration.entity.chat.MessageType
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.apache.logging.log4j.message.SimpleMessage
import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionDisconnectEvent
import kotlin.math.log

@Component
@RequiredArgsConstructor
@Slf4j
class WebSocketEventListener (
    val messageTemplate : SimpMessageSendingOperations
) {


    @EventListener
    fun handleWebSocketDisconnectListener(event: SessionDisconnectEvent) {
        val headerAccessor : StompHeaderAccessor = StompHeaderAccessor.wrap(event.message)
        val username : String? = headerAccessor.sessionAttributes?.get("username") as String?
        if (username != null){

            var chatMessage = ChatMessage.Builder().type(MessageType.LEAVE).sender(username).build()
            messageTemplate.convertAndSend("/topic/public",chatMessage)
        }
    }
}
*/
