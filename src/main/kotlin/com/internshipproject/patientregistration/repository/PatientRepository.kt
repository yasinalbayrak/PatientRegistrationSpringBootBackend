package com.internshipproject.patientregistration.repository

import com.internshipproject.patientregistration.entity.user.types.Patient
import org.springframework.data.jpa.repository.JpaRepository

interface PatientRepository :JpaRepository<Patient,Int>{
}