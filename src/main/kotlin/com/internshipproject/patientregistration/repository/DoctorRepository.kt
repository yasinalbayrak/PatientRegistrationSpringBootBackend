package com.internshipproject.patientregistration.repository

import com.internshipproject.patientregistration.entity.user.types.Doctor
import org.springframework.data.jpa.repository.JpaRepository

interface DoctorRepository : JpaRepository<Doctor,Int> {
}