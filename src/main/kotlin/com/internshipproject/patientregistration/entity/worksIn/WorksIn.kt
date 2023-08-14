package com.internshipproject.patientregistration.entity.worksIn

import com.internshipproject.patientregistration.entity.hospital.Hospital
import com.internshipproject.patientregistration.entity.user.types.Doctor
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
class WorksIn(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Int = 0,

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    var doctor: Doctor? = null,

    @ManyToOne
    @JoinColumn(name = "hospital_id")
    var hospital: Hospital? = null,

    var startDate: LocalDate? = null,

    var endDate: LocalDate? = null,

    var salary: Double? = null,

    var stillWorks: Boolean = true
) {
    // Builder class for WorksIn
    data class Builder(
        var id: Int = 0,
        var doctor: Doctor? = null,
        var hospital: Hospital? = null,
        var startDate: LocalDate? = null,
        var endDate: LocalDate? = null,
        var salary: Double? = null,
        var stillWorks: Boolean? = false
    ) {
        fun id(id: Int) = apply { this.id = id }
        fun doctor(doctor: Doctor?) = apply { this.doctor = doctor }
        fun hospital(hospital: Hospital?) = apply { this.hospital = hospital }
        fun startDate(startDate: LocalDate) = apply { this.startDate = startDate }
        fun endDate(endDate: LocalDate? = null) = apply { this.endDate = endDate }
        fun salary(salary: Double?) = apply { this.salary = salary }

        fun stillWorks(stillWorks: Boolean) = apply { this.stillWorks = stillWorks }
        fun build() = WorksIn(id, doctor, hospital, startDate, endDate, salary,stillWorks!!)
    }

    companion object {
        fun builder() = Builder()
    }
}
