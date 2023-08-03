package com.internshipproject.patientregistration.entity.user.types

import org.springframework.data.jpa.repository.JpaRepository

interface PatientRepository :JpaRepository<Patient,Int>{
}