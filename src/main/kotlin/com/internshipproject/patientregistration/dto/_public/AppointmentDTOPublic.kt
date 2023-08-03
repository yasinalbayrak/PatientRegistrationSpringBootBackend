package com.internshipproject.patientregistration.dto._public

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonView
import jakarta.validation.constraints.NotNull
import java.util.*


class AppointmentDTOPublic(


    @get:NotNull(message = "AppointmentDTO.doctorid must not be null")
    var doctorID: Int? = null,
    @get:NotNull(message = "AppointmentDTO.patientId must not be null")
    var patientID: Int? = null,

    var date: String? = null
) {


}