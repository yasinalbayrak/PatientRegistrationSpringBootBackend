package com.internshipproject.patientregistration.repository.jpa

import com.internshipproject.patientregistration.entity.user.types.Doctor
import org.springframework.data.jpa.repository.JpaRepository

interface DoctorRepository : JpaRepository<Doctor,Int> {
}