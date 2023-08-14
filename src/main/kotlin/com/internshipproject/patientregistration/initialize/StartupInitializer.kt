package com.internshipproject.patientregistration.initialize

import com.internshipproject.patientregistration.dto._internal.DoctorDTO
import com.internshipproject.patientregistration.dto._internal.HospitalDTO
import com.internshipproject.patientregistration.dto._internal.PatientDTO
import com.internshipproject.patientregistration.dto._internal.UserDTO
import com.internshipproject.patientregistration.dto._public.AppointmentDTOPublic
import com.internshipproject.patientregistration.dto._public.DoctorDTOPublic
import com.internshipproject.patientregistration.dto._public.PatientDTOPublic
import com.internshipproject.patientregistration.dto._public.UserDTOPublic
import com.internshipproject.patientregistration.entity.user.Gender
import com.internshipproject.patientregistration.entity.user.types.Doctor
import com.internshipproject.patientregistration.service.AppointmentService
import com.internshipproject.patientregistration.service.HospitalService
import com.internshipproject.patientregistration.service.UserService
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component

@Component
class StartupInitializer(private val userService: UserService, private val appointmentService: AppointmentService, private val hospitalService: HospitalService) {

    @PostConstruct
    fun initialize() {
        val doctorDTO = DoctorDTO(
            UserDTO(firstname = "doctor", lastname = "dr", email = "doctor@gmail.com", passw = "1234", gender = Gender.MALE ,age = 30),
            specialization = "cardiology",

        )
        val doctor = userService.addDoctor(doctorDTO)

        val patientDTO = PatientDTO(
            firstname = "patient", lastname = "pt", email = "patient@gmail.com", passw = "1234", gender = Gender.MALE ,age = 30
        )
        val patient = userService.addPatient(patientDTO)

        appointmentService.addAppointment(AppointmentDTOPublic(doctor.user.id, patient.id,"2023-08-15 15:00" ))


        val hospitalDTO = HospitalDTO(
            name = "Avicenna"
        )
        hospitalService.addHospital(hospitalDTO)

    }
}