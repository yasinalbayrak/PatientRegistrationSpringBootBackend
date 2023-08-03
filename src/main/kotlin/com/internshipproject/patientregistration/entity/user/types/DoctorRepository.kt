package com.internshipproject.patientregistration.entity.user.types

import org.springframework.data.jpa.repository.JpaRepository

interface DoctorRepository : JpaRepository<Doctor,Int> {
}