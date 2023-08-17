package com.internshipproject.patientregistration.dto._internal

import com.internshipproject.patientregistration.entity.hospital.Hospital

class WorksInDTO(
    var id: Int? = null,
    var doctor: DoctorDTO,
    var hospital: HospitalDTO,
    var startDate: String,
    var endDate: String,
    var salary: Double,
    var stillWorks: Boolean

) {
}