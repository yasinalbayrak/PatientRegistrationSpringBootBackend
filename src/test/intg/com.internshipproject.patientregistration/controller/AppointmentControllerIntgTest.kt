package com.internshipproject.patientregistration.controller

import com.internshipproject.patientregistration.dto._internal.AppointmentDTO
import com.internshipproject.patientregistration.dto._public.AppointmentDTOPublic
import com.internshipproject.patientregistration.entity.appointment.Appointment
import com.internshipproject.patientregistration.repository.AppointmentRepository
import com.internshipproject.patientregistration.entity.user.Role
import com.internshipproject.patientregistration.repository.RoleRepository
import com.internshipproject.patientregistration.repository.UserRepository
import com.internshipproject.patientregistration.repository.DoctorRepository
import com.internshipproject.patientregistration.repository.PatientRepository
import com.internshipproject.patientregistration.util.doctorEntityList
import com.internshipproject.patientregistration.util.patientEntity
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDateTime

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test") // always configure to test
@AutoConfigureWebTestClient
@Testcontainers
class AppointmentControllerIntgTest {
    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var doctorRepository: DoctorRepository

    @Autowired
    lateinit var patientRepository: PatientRepository

    @Autowired
    lateinit var appointmentRepository: AppointmentRepository

    @Autowired
    lateinit var roleRepository: RoleRepository

    @Autowired
    lateinit var webTestClient: WebTestClient

    @BeforeEach
    fun setUp(){
        userRepository.deleteAll()
        doctorRepository.deleteAll()
        patientRepository.deleteAll()
        roleRepository.deleteAll()
        appointmentRepository.deleteAll()


        val roleDoctor = Role.builder().name("Doctor").build()
        val rolePatient = Role.builder().name("Patient").build()
        roleRepository.saveAll(setOf(roleDoctor,rolePatient))

        val patient = patientEntity(rolePatient)
        val doctors = doctorEntityList(roleDoctor)
        doctorRepository.saveAll(doctors)
        patientRepository.save(patient)


        val appointment1 = Appointment
            .builder()
            .patient(patient)
            .doctor(doctors.first())
            .date(LocalDateTime.of(2023,8,5,17,0))
            .build()
        val appointment2 = Appointment
            .builder()
            .patient(patient)
            .doctor(doctors.first())
            .date(LocalDateTime.of(2023,8,17,17,0))
            .build()
        appointmentRepository.saveAll(listOf(appointment1,appointment2))
    }

    @Test
    fun addAppointment(){
        val appointmentDTO = AppointmentDTOPublic(
            doctorID = 1,
            patientID = 4,
            "2023-08-15 17:00"
        )
        val savedAppointmentDTO = webTestClient
            .post()
            .uri("/api/v1/appointment")
            .bodyValue(appointmentDTO)
            .exchange()
            .expectStatus().isCreated
            .expectBody(AppointmentDTO::class.java)
            .returnResult()
            .responseBody

        Assertions.assertTrue(
            savedAppointmentDTO!!.id != null
        )
    }

    @Test
    fun retrieveAllAppointments(){
        val appointmentDTOs = webTestClient
            .get()
            .uri("/api/v1/appointment")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(AppointmentDTO::class.java)
            .returnResult()
            .responseBody

        Assertions.assertEquals(2,appointmentDTOs!!.size)
    }

    @Test
    fun updateAppointment(){

        val appointment = Appointment
            .builder()
            .patient(patientRepository.findAll().first())
            .doctor(doctorRepository.findAll().first())
            .date(LocalDateTime.of(2023,8,25,17,0))
            .build()

        appointmentRepository.save(appointment)

        val updatedAppointmentDTO = AppointmentDTOPublic(
            1,
            4,
            "2023-09-15 17:00"
        )
        val appointmentDTO = webTestClient
            .put()
            .uri("/api/v1/appointment/${appointment.id}")
            .bodyValue(updatedAppointmentDTO)
            .exchange()
            .expectStatus().isOk
            .expectBody(AppointmentDTO::class.java)
            .returnResult()
            .responseBody

        Assertions.assertEquals(
            "2023-09-15T17:00",
            appointmentDTO!!.date
        )

    }


    @Test
    fun deleteAppointment(){
        val appointment = Appointment
            .builder()
            .patient(patientRepository.findAll().first())
            .doctor(doctorRepository.findAll().first())
            .date(LocalDateTime.of(2023,8,25,17,0))
            .build()


        appointmentRepository.save(appointment)

        webTestClient
            .delete()
            .uri("/api/v1/appointment/${appointment.id}")
            .exchange()
            .expectStatus().isNoContent

    }



}