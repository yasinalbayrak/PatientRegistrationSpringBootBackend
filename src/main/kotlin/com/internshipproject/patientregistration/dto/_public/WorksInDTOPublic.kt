package com.internshipproject.patientregistration.dto._public

import com.internshipproject.patientregistration.dto._internal.DoctorDTO
import com.internshipproject.patientregistration.dto._internal.HospitalDTO

class WorksInDTOPublic(

    var doctorID: Int,
    var hospitalID: Int,
    var startDate: String,
    var endDate: String? = null,
    var salary: Double,


) {
}