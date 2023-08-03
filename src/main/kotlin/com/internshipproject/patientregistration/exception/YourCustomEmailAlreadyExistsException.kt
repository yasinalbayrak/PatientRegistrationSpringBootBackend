package com.internshipproject.patientregistration.exception

import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException


class YourCustomEmailAlreadyExistsException(message: String, val status: HttpStatus?) : AuthenticationException(message)