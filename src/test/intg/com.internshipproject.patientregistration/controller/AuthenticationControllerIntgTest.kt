package com.internshipproject.patientregistration.controller

import com.internshipproject.patientregistration.config.auth.AuthenticationRequest
import com.internshipproject.patientregistration.config.auth.AuthenticationResponse
import com.internshipproject.patientregistration.config.auth.RegisterRequest
import com.internshipproject.patientregistration.entity.user.Gender
import com.internshipproject.patientregistration.entity.user.Role
import com.internshipproject.patientregistration.repository.jpa.RoleRepository
import com.internshipproject.patientregistration.repository.jpa.UserRepository
import com.internshipproject.patientregistration.repository.jpa.DoctorRepository
import com.internshipproject.patientregistration.entity.user.types.Patient
import com.internshipproject.patientregistration.repository.jpa.PatientRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.junit.jupiter.Testcontainers


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test") // always configure to test
@AutoConfigureWebTestClient
@Testcontainers
class AuthenticationControllerIntgTest {


    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var doctorRepository: DoctorRepository

    @Autowired
    lateinit var patientRepository: PatientRepository

    @Autowired
    lateinit var roleRepository: RoleRepository

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    @BeforeEach
    fun setUp(){
        userRepository.deleteAll()
        doctorRepository.deleteAll()
        patientRepository.deleteAll()
        roleRepository.deleteAll()
    }


     @Test
     fun register(){
         val registerRequest = RegisterRequest(
             firstname = "patient", lastname = "pt", email = "patient@gmail.com", password = "1234", gender = Gender.MALE ,age = 30
         )
         val response = webTestClient
             .post()
             .uri("/api/v1/auth/register")
             .bodyValue(registerRequest)
             .exchange()
             .expectStatus().isOk
             .expectBody(AuthenticationResponse::class.java)
             .returnResult()
             .responseBody

         Assertions.assertTrue(
             response?.id != null
         )
     }

    @Test
    fun authenticate(){
        val role = roleRepository.save(Role.builder().name("Patient").build())

        val patient = Patient.builder()
            .age(22)
            .email("sila1@gmail.com")
            .gender(Gender.FEMALE)
            .firstName("sila")
            .lastName("ozinan")
            .passw(passwordEncoder.encode("1234"))
            .roles(setOf(role))
            .build()

        patientRepository.save(patient)

        val authenticationRequest = AuthenticationRequest.builder().email("sila1@gmail.com").password("1234").build()
        val response = webTestClient

            .post()
            .uri("/api/v1/auth/authentication")
            .bodyValue(authenticationRequest)
            .exchange()
            .expectStatus().isOk
            .expectBody(AuthenticationResponse::class.java)
            .returnResult()
            .responseBody

        Assertions.assertTrue(
            response?.id != null
        )
    }

}