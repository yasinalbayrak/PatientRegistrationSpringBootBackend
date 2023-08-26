package com.internshipproject.patientregistration.repository.jpa

import com.internshipproject.patientregistration.entity.appointment.Appointment
import com.internshipproject.patientregistration.entity.worksIn.WorksIn
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate
import java.util.*


interface WorksInRepository : JpaRepository<WorksIn,Int> {

    @Query("SELECT COUNT(w) > :howMany FROM WorksIn w WHERE w.doctor.id = :doctorID AND (" +
            "    (w.startDate >= :startDate AND w.startDate <= COALESCE(:endDate, CURRENT_DATE)) OR" +
            "    (w.endDate IS NOT NULL AND w.endDate >= :startDate AND w.endDate <= COALESCE(:endDate, CURRENT_DATE)) OR" +
            "    (w.startDate <= :startDate AND (w.endDate IS NULL OR  w.endDate >= COALESCE(:endDate, CURRENT_DATE)))" +
            ")")
    fun conflictsWithOtherWorkDates(doctorID:Int,startDate:LocalDate,endDate:LocalDate? , howMany: Int = 0) : Boolean


    @Query("Select w from WorksIn w where w.doctor.id = :doctorID")
    fun findByDoctorId(doctorID: Int) : Optional<List<WorksIn>>

    @Query("Select w from WorksIn w where w.hospital.id = :hospitalID")
    fun findByHospitalId(hospitalID: Int) : Optional<List<WorksIn>>
}