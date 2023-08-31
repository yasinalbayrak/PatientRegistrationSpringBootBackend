package com.internshipproject.patientregistration.util.appointment

import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Component
class AppointmentUtils {
    fun convertStringToLocalDateTime(dateTimeString: String): LocalDateTime {

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return LocalDateTime.parse(dateTimeString, formatter)
    }
}