package com.internshipproject.patientregistration.service

import com.internshipproject.patientregistration.dto._public.AppointmentDTOPublic
import com.internshipproject.patientregistration.entity.appointment.Appointment
import com.internshipproject.patientregistration.entity.appointment.AppointmentStatus
import com.internshipproject.patientregistration.entity.user.Role
import com.internshipproject.patientregistration.entity.user.types.Doctor
import com.internshipproject.patientregistration.entity.user.types.Patient
import com.internshipproject.patientregistration.exception.NoUserFoundException
import com.internshipproject.patientregistration.exception.YourCustomEmailAlreadyExistsException
import com.internshipproject.patientregistration.repository.jpa.*
import com.internshipproject.patientregistration.util.appointment.AppointmentUtils
import com.internshipproject.patientregistration.util.doctorEntityList
import com.internshipproject.patientregistration.util.patientEntity
import com.internshipproject.patientregistration.util.roleEntity
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import mu.KotlinLogging
import org.junit.Assert.assertThrows
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.*
import org.mockito.BDDMockito.anyInt
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.time.Duration
import java.time.LocalDateTime
import java.util.*


@ExtendWith(MockitoExtension::class)
class AppointmentServiceUnitTest {
    @Mock
    private lateinit var appointmentRepository: AppointmentRepository

    @Mock
    private lateinit var userService: UserService

    @InjectMocks
    private lateinit var appointmentService: AppointmentService


    @Mock
    private lateinit var appointmentUtils: AppointmentUtils

    private val localDateTimeNow: LocalDateTime = LocalDateTime.of(2023, 11, 4, 9, 0)
    // Mock appointmentInput
    private val appointmentInput = AppointmentDTOPublic(
        1,
        2,
        "2023-11-04 09:00"
    )

    private val existingDoctorRole = roleEntity("Doctor")
    private val existingPatientRole = roleEntity("Patient")

    // Mock existing doctor and patient
    private val existingDoctor = doctorEntityList(existingDoctorRole).first()
    private val existingPatient = patientEntity(existingPatientRole)
    @BeforeEach
    fun setUp(){



    }
    companion object {
        private val logger = KotlinLogging.logger {}
    }
    object MockitoHelper {
        fun <T> anyObject(): T {
            Mockito.any<T>()
            return uninitialized()
        }
        @Suppress("UNCHECKED_CAST")
        fun <T> uninitialized(): T =  null as T
    }

    @Test
    fun `test addAppointment when appointment date is available`() {


        `when`(userService.getDoctor(anyInt())).thenReturn(Optional.of(existingDoctor))
        `when`(userService.getPatient(anyInt())).thenReturn(Optional.of(existingPatient))
        `when`(appointmentUtils.convertStringToLocalDateTime(anyString())).thenReturn(localDateTimeNow)

        // Use localDateTimeNow directly as an argument
        `when`(
            appointmentRepository.existsAppointmentWithDoctorPatientAndDate(
                anyInt(),
                MockitoHelper.anyObject()
            )
        ).thenReturn(false)

        val savedAppointment = Appointment(
            1,
            existingDoctor,
            existingPatient,
            localDateTimeNow,
            Duration.ofHours(1),
            AppointmentStatus.ACTIVE
        )
        `when`(appointmentRepository.save(any(Appointment::class.java))).thenReturn(savedAppointment)

        val result = appointmentService.addAppointment(appointmentInput)

        // Assertions
        Assertions.assertNotNull(result)
        Assertions.assertEquals(result.id, savedAppointment.id)
    }

    @Test
    fun `test addAppointment when appointment date is not available`() {
        `when`(appointmentUtils.convertStringToLocalDateTime(anyString())).thenReturn(localDateTimeNow)
        // Use localDateTimeNow directly as an argument
        `when`(
            appointmentRepository.existsAppointmentWithDoctorPatientAndDate(
                anyInt(),
                MockitoHelper.anyObject()
            )
        ).thenReturn(true)
        // Assertions
        assertThrows(YourCustomEmailAlreadyExistsException::class.java) {
            appointmentService.addAppointment(appointmentInput)
        }
    }

    @Test
    fun `test addAppointment when doctor not found`() {
        // Similar setup as the previous tests but mock userService methods to return Optional.empty()

        `when`(userService.getDoctor(anyInt())).thenReturn(Optional.empty())
        //`when`(userService.getPatient(anyInt())).thenReturn(Optional.of(existingPatient))
        `when`(appointmentUtils.convertStringToLocalDateTime(anyString())).thenReturn(localDateTimeNow)


        `when`(
            appointmentRepository.existsAppointmentWithDoctorPatientAndDate(
                anyInt(),
                MockitoHelper.anyObject()
            )
        ).thenReturn(false)



        // Assertions
        assertThrows(NoUserFoundException::class.java) {
            appointmentService.addAppointment(appointmentInput)
        }
    }
    @Test
    fun `test addAppointment when patient not found`() {
        // Similar setup as the previous tests but mock userService methods to return Optional.empty()

        `when`(userService.getDoctor(anyInt())).thenReturn(Optional.of(existingDoctor))
        `when`(userService.getPatient(anyInt())).thenReturn(Optional.empty())
        `when`(appointmentUtils.convertStringToLocalDateTime(anyString())).thenReturn(localDateTimeNow)
        `when`(
            appointmentRepository.existsAppointmentWithDoctorPatientAndDate(
                anyInt(),
                MockitoHelper.anyObject()
            )
        ).thenReturn(false)

        // Assertions
        assertThrows(NoUserFoundException::class.java) {
            appointmentService.addAppointment(appointmentInput)
        }
    }

}
