package com.internshipproject.patientregistration.entity.appointment

import com.internshipproject.patientregistration.entity.user.types.Doctor
import com.internshipproject.patientregistration.entity.user.types.Patient
import jakarta.persistence.*
import java.time.Duration
import java.time.LocalDateTime
import java.util.Date


@Entity
@Table(uniqueConstraints = [
    UniqueConstraint(columnNames = ["doctor_id", "patient_id", "appointment_date"])
])
class Appointment(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int = 0,

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    var doctor: Doctor? = null,

    @ManyToOne
    @JoinColumn(name = "patient_id")
    var patient: Patient? = null,

    @Column(name = "appointment_date", nullable = false)
    var date : LocalDateTime? = null,

    var duration: Duration = Duration.ofHours(1),

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: AppointmentStatus = AppointmentStatus.ACTIVE
) {

    data class Builder(
        var id: Int = 0,
        var doctor: Doctor? = null,
        var patient: Patient? = null,
        var date: LocalDateTime? = null,
        var duration: Duration = Duration.ofHours(1)
    ) {
        fun doctor(doctor: Doctor) = apply { this.doctor = doctor }
        fun patient(patient: Patient) = apply { this.patient = patient }
        fun date(date: LocalDateTime) = apply { this.date = date }

        fun build() = Appointment(id, doctor, patient, date,duration)
    }

    companion object {
        fun builder() = Builder()
    }


}