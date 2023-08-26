package com.internshipproject.patientregistration.entity.chat

import com.internshipproject.patientregistration.dto.ReadStatus
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document


@Document(collection = "chats")
data class ChatMessage(
    @Id
    val id: String? = null,
    val sender: String,
    val recipient: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    var photoData: ByteArray? = null,
    var readStatus: ReadStatus = ReadStatus.UNSEEN
) {
    data class Builder(
        var id: String? = null,
        var recipient: String = "",
        var sender: String = "",
        var message: String = "",
        var photoData: ByteArray? = null
    ) {

        fun recipient(recipient: String) = apply { this.recipient = recipient }
        fun sender(sender: String) = apply { this.sender = sender }
        fun message(message: String) = apply { this.message = message }

        fun photoData(photoData: ByteArray?) = apply { this.photoData = photoData }
        fun build() = ChatMessage(id, sender, recipient, message, photoData =  photoData )
    }
}

