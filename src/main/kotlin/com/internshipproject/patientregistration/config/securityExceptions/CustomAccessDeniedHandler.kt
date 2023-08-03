package com.internshipproject.patientregistration.config.securityExceptions

import com.fasterxml.jackson.databind.ObjectMapper
import com.internshipproject.patientregistration.exception.NoUserFoundException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KLogging
import org.springframework.http.HttpStatus
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import java.io.IOException


@Component
class CustomAccessDeniedHandler : AccessDeniedHandler {

    @Throws(IOException::class)
    override fun handle(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        accessDeniedException: org.springframework.security.access.AccessDeniedException?
    ) {


        response?.apply {
            status = HttpStatus.FORBIDDEN.value()
            contentType = "application/json"
            writer.write("{\"error2\": \"Forbidden\", \"yasin\": \"Access Denieds.\"}")
            writer.flush()
        }


    }
}