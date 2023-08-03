package com.internshipproject.patientregistration.entity.appointment

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime
import java.util.*

interface AppointmentRepository : JpaRepository<Appointment,Int> {

    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.doctor.id = :doctorId AND a.patient.id = :patientId AND a.date = :appointmentDate")
    fun existsAppointmentWithDoctorPatientAndDate(doctorId: Int, patientId: Int, appointmentDate: LocalDateTime): Boolean


}