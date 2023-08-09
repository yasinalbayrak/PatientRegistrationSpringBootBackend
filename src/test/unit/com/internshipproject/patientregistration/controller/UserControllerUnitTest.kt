package com.internshipproject.patientregistration.controller

import com.internshipproject.patientregistration.config.auth.AuthenticationResponse
import com.internshipproject.patientregistration.config.auth.AuthenticationService
import com.internshipproject.patientregistration.dto._internal.DoctorDTO
import com.internshipproject.patientregistration.dto._internal.UserDTO
import com.internshipproject.patientregistration.dto._public.DoctorDTOPublic
import com.internshipproject.patientregistration.dto._public.UserDTOPublic
import com.internshipproject.patientregistration.entity.user.Gender
import com.internshipproject.patientregistration.service.JwtService
import com.internshipproject.patientregistration.service.UserService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper
import java.util.*

@WebMvcTest(controllers = [DoctorController::class, PatientController::class])

@AutoConfigureMockMvc(addFilters = false)
class UserControllerUnitTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var userServiceMock: UserService

    @MockkBean
    lateinit var jwtService: JwtService



    @Test // Add the mock user annotation
    fun addDoctor() {
        val doctorDTOs = DoctorDTOPublic(
            UserDTOPublic(firstname = "doctor", lastname = "dr", email = "doctor@gmail.com", passw = "1234", gender = Gender.MALE, age = 30),
            specialization = "cardiology",
            salary = 1000.0
        )

        every { userServiceMock.addDoctor(any()) } returns DoctorDTO(
            UserDTO(1, firstname = "doctor", lastname = "dr", email = "doctor@gmail.com", passw = "1234", gender = Gender.MALE, age = 30),
            specialization = "cardiology",
            salary = 1000.0
        )

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/user/doctor")
            .content(ObjectMapper().writeValueAsString(doctorDTOs))
            .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.user.id").exists())
    }
}
