package com.internshipproject.patientregistration.config.demo

import com.internshipproject.patientregistration.service.JwtService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/v1/demo-controller")
class DemoController(
    val service: JwtService
) {
    @GetMapping
    fun sayHello(@RequestHeader("Authorization") token: String):ResponseEntity<String>{
        val original_token = token.split(" ")[1].trim();
        val username = service.extractUsername(original_token)
        return ResponseEntity.ok("Hello $username to secure endpoint.")
    }

}