package com.internshipproject.patientregistration.service

import com.internshipproject.patientregistration.entity.auth.TokenRepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.stereotype.Service


@Service
class LogoutService(
    val tokenRepository: TokenRepository
) : LogoutHandler {
    override fun logout(request: HttpServletRequest?, response: HttpServletResponse?, authentication: Authentication?) {
        val authHeader :String? = request?.getHeader("Authorization")
        var jwt :String

        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            return
        }

        //jwt= authHeader.substring(7)
        jwt = authHeader.split(" ")[1].trim();
        val storedToken = tokenRepository.findByToken(jwt).orElse(null)

        if(storedToken != null) {
            storedToken.expired = true
            storedToken.revoked =  true
            tokenRepository.save(storedToken)
        }
    }

}