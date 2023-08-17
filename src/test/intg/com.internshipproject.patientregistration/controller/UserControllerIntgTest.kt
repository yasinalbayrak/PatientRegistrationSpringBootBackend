package com.internshipproject.patientregistration.controller

import com.internshipproject.patientregistration.dto._internal.DoctorDTO
import com.internshipproject.patientregistration.dto._internal.PatientDTO
import com.internshipproject.patientregistration.dto._public.DoctorDTOPublic
import com.internshipproject.patientregistration.dto._public.PatientDTOPublic
import com.internshipproject.patientregistration.dto._public.UserDTOPublic
import com.internshipproject.patientregistration.entity.user.Gender
import com.internshipproject.patientregistration.entity.user.Role
import com.internshipproject.patientregistration.repository.RoleRepository
import com.internshipproject.patientregistration.repository.UserRepository
import com.internshipproject.patientregistration.entity.user.types.Doctor
import com.internshipproject.patientregistration.repository.DoctorRepository
import com.internshipproject.patientregistration.entity.user.types.Patient
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test") // always configure to test
@AutoConfigureWebTestClient
@Testcontainers
class UserControllerIntgTest () {

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

    @BeforeEach
    fun setUp(){
        userRepository.deleteAll()
        doctorRepository.deleteAll()
        patientRepository.deleteAll()
        roleRepository.deleteAll()

        val roleDoctor = Role.builder().name("Doctor").build()
        val rolePatient = Role.builder().name("Patient").build()
        roleRepository.saveAll(setOf(roleDoctor,rolePatient))

        val patient = patientEntity(rolePatient)
        val doctors = doctorEntityList(roleDoctor)
        doctorRepository.saveAll(doctors)
        patientRepository.save(patient)

    }

    @Test
    fun addDoctor(){
        val doctorDTO = DoctorDTOPublic(
            UserDTOPublic(firstname = "doctor", lastname = "dr", email = "doctor@gmail.com", passw = "1234", gender = Gender.MALE ,age = 30),
            specialization = "cardiology",

        )
        val savedDoctorDTO = webTestClient
            .post()
            .uri("/api/v1/user/doctor")
            .bodyValue(doctorDTO)
            .exchange()
            .expectStatus().isCreated
            .expectBody(DoctorDTO::class.java)
            .returnResult()
            .responseBody

        Assertions.assertTrue(
            savedDoctorDTO!!.user.id != null
        )
    }
    @Test
    fun addPatient(){
        val patientDTO = PatientDTOPublic(
            firstname = "patient", lastname = "pt", email = "patient@gmail.com", passw = "1234", gender = Gender.MALE ,age = 30

        )
        val savedPatientDTO = webTestClient
            .post()
            .uri("/api/v1/user/patient")
            .bodyValue(patientDTO)
            .exchange()
            .expectStatus().isCreated
            .expectBody(PatientDTO::class.java)
            .returnResult()
            .responseBody

        Assertions.assertTrue(
            savedPatientDTO!!.id != null
        )
    }
    @Test
    fun retrieveAllDoctors(){
        val doctorDTOs = webTestClient
            .get()
            .uri("/api/v1/user/doctor")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(DoctorDTO::class.java)
            .returnResult()
            .responseBody

        Assertions.assertEquals(3,doctorDTOs!!.size)
    }
    @Test
    fun retrieveAllPatients(){
        val patientDTOs = webTestClient
            .get()
            .uri("/api/v1/user/patient")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(PatientDTO::class.java)
            .returnResult()
            .responseBody

        Assertions.assertEquals(1,patientDTOs!!.size)
    }

    @Test
    fun updateDoctor(){

        val role = roleRepository.findByName("Doctor").get()

        val doctor = Doctor.builder()
            .age(22)
            .email("sila1@gmail.com")
            .gender(Gender.FEMALE)
            .firstName("sila")
            .lastName("ozinan")
            .passw("1234")
            .roles(setOf(role))
            .specialization("cardiology")
            .build()

        doctorRepository.save(doctor)

        val updatedDoctorDTO = DoctorDTOPublic(
            UserDTOPublic(
                age = 22,
                email = "sila.ozinan@gmail.com",
                gender = Gender.FEMALE,
                firstname = "sila",
                lastname = "ozinan",
                passw = "1234"
            ),
            specialization = "cardiology",

        )
        val doctorDTO = webTestClient
            .put()
            .uri("/api/v1/user/doctor/${doctor.id}")
            .bodyValue(updatedDoctorDTO)
            .exchange()
            .expectStatus().isOk
            .expectBody(DoctorDTO::class.java)
            .returnResult()
            .responseBody

        Assertions.assertEquals(
            "sila.ozinan@gmail.com",
            doctorDTO!!.user.email
        )

    }
    @Test
    fun updatePatient(){

        val role = roleRepository.findByName("Patient").get()

        val patient = Patient.builder()
            .age(22)
            .email("sila1@gmail.com")
            .gender(Gender.FEMALE)
            .firstName("sila")
            .lastName("ozinan")
            .passw("1234")
            .roles(setOf(role))
            .build()

        patientRepository.save(patient)

        val updatedPatientDTO = PatientDTOPublic(
            age = 22,
            email = "sila.ozinan@gmail.com",
            gender = Gender.FEMALE,
            firstname = "sila",
            lastname = "ozinan",
            passw = "1234"

        )
        val patientDTO = webTestClient
            .put()
            .uri("/api/v1/user/patient/${patient.id}")
            .bodyValue(updatedPatientDTO)
            .exchange()
            .expectStatus().isOk
            .expectBody(PatientDTO::class.java)
            .returnResult()
            .responseBody

        Assertions.assertEquals(
            "sila.ozinan@gmail.com",
            patientDTO!!.email
        )

    }


    @Test
    fun deleteDoctor(){
        val role = roleRepository.findByName("Doctor").get()

        val doctor = Doctor.builder()
            .age(22)
            .email("sila1@gmail.com")
            .gender(Gender.FEMALE)
            .firstName("sila")
            .lastName("ozinan")
            .passw("1234")
            .roles(setOf(role))
            .specialization("cardiology")

            .build()

        doctorRepository.save(doctor)

        webTestClient
            .delete()
            .uri("/api/v1/user/doctor/${doctor.id}")
            .exchange()
            .expectStatus().isNoContent

    }


    @Test
    fun deletePatient(){
        val role = roleRepository.findByName("Patient").get()

        val patient = Patient.builder()
            .age(22)
            .email("sila1@gmail.com")
            .gender(Gender.FEMALE)
            .firstName("sila")
            .lastName("ozinan")
            .passw("1234")
            .roles(setOf(role))
            .build()

        patientRepository.save(patient)

        webTestClient
            .delete()
            .uri("/api/v1/user/patient/${patient.id}")
            .exchange()
            .expectStatus().isNoContent

    }

}
