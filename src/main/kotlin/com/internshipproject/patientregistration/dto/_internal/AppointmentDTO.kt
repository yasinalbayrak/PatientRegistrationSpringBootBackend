package com.internshipproject.patientregistration.dto._internal

import com.internshipproject.patientregistration.entity.appointment.AppointmentStatus
import com.internshipproject.patientregistration.entity.user.types.Doctor
import jakarta.validation.constraints.NotNull
import java.time.Duration
import java.util.*

class AppointmentDTO(
    var id: Int? = 0,

    @get:NotNull(message = "AppointmentDTO.doctor must not be null")
    var doctor: DoctorDTO? = null,
    @get:NotNull(message = "AppointmentDTO.patient must not be null")
    var patient: PatientDTO? = null,

    var date: String? = null,

    var duration: Duration = Duration.ofHours(1),
    var status: AppointmentStatus = AppointmentStatus.ACTIVE
) {

}
