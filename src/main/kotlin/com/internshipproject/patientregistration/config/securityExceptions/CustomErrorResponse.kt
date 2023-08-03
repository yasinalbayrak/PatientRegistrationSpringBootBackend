package com.internshipproject.patientregistration.config.securityExceptions

data class CustomErrorResponse(
    val error: String,
    val message: String
)