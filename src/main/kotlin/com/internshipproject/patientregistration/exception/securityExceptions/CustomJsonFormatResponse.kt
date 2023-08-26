package com.internshipproject.patientregistration.exception.securityExceptions

import com.internshipproject.patientregistration.dto._public.DoctorDTOPublic


data class CustomJsonFormatResponse(
    val error: String,
    val message: String,
    val properJsonFormat: Any
)