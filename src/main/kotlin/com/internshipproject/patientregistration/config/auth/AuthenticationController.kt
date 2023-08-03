package com.internshipproject.patientregistration.config.auth

import lombok.RequiredArgsConstructor
import org.springframework.http.ResponseEntity
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

}