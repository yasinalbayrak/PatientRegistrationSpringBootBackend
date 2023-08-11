package com.internshipproject.patientregistration.entity.appointment

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime
import java.util.*

interface AppointmentRepository : JpaRepository<Appointment,Int> {

    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.doctor.id = :doctorId AND a.date = :appointmentDate AND a.status = 'ACTIVE' ")
    fun existsAppointmentWithDoctorPatientAndDate(doctorId: Int, appointmentDate: LocalDateTime): Boolean


    @Query("Select a from Appointment a where a.patient.id = :userId")
    fun findByUserId(userId: Int) :Optional<List<Appointment>>

    @Query("Select a from Appointment a where a.doctor.id = :userId")
    fun findByDoctorId(userId: Int) :Optional<List<Appointment>>
}