package com.internshipproject.patientregistration.config.securityExceptions
import com.fasterxml.jackson.databind.ObjectMapper
import com.internshipproject.patientregistration.exception.YourCustomEmailAlreadyExistsException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KLogging
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerExceptionResolver
import java.io.IOException


@Component
class CustomAuthenticationEntryPoint(
    @Qualifier("handlerExceptionResolver") val resolver: HandlerExceptionResolver
) : AuthenticationEntryPoint {

    companion object {
        private val objectMapper = ObjectMapper()
        val logg = KLogging()

    }

    @Throws(IOException::class)
    override fun commence(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authException: AuthenticationException?
    ) {
        logg.logger.info ("Yasooo entry point")


        when (authException) {
            is YourCustomEmailAlreadyExistsException -> {
                response?.apply {
                    status = HttpStatus.CONFLICT.value()
                    contentType = "application/json"
                    val customErrorResponse = CustomErrorResponse(
                        error = authException.status?.toString() ?: "Auth Error",
                        message = authException.message ?: ""
                    )
                    writer.write(objectMapper.writeValueAsString(customErrorResponse))
                    writer.flush()
                }
            }
            else -> {
                response?.apply {
                    status = HttpStatus.UNAUTHORIZED.value()
                    contentType = "application/json"
                    writer.write("{\"error\": \"Unauthorized\", \"message\": \"${authException!!.message}\"}")
                    writer.flush()
                }
            }
        }
    }
}