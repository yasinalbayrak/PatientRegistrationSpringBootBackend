package com.internshipproject.patientregistration.config.auth

import io.jsonwebtoken.io.IOException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpSession
import lombok.RequiredArgsConstructor
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
class AuthenticationController(
    private val service: AuthenticationService

) {
    @PostMapping("/register")
    fun register(
        @RequestBody request:RegisterRequest
    ):ResponseEntity<AuthenticationResponse>{

       return ResponseEntity.ok(service.register(request))
    }

    @PostMapping("/authentication")
    fun register(
        @RequestBody request:AuthenticationRequest
    ):ResponseEntity<AuthenticationResponse>{
        return ResponseEntity.ok(service.authenticate(request))
    }


    @GetMapping("/logout")
    @ResponseBody
    fun logout(request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<String> {
        // Custom logout logic (if needed)
        SecurityContextHolder.clearContext()
        // Invalidate the session
        val session: HttpSession? = request.getSession(false)
        session?.invalidate()

        // Return a success message indicating the logout was successful
        return ResponseEntity("Logout Successful", HttpStatus.OK)
    }



}