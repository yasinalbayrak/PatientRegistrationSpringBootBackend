package com.internshipproject.patientregistration.dto


import com.internshipproject.patientregistration.dto._internal.UserDTO

data class ResponseMessageModel(
    val message: String = "",
    val senderName: String = "",
    val receiverName: String = "",
    val status: MessageStatus = MessageStatus.MESSAGE,
    val readStatus: ReadStatus = ReadStatus.UNSEEN,
    var user: UserDTO? = null,
    var photoData: ByteArray? = null,
    var timestamp: Long =  System.currentTimeMillis()

) {
}