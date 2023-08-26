package com.internshipproject.patientregistration.repository.jpa

import com.internshipproject.patientregistration.entity.hospital.Hospital
import org.springframework.data.jpa.repository.JpaRepository

interface HospitalRepository : JpaRepository<Hospital,Int> {

    fun existsByName(name: String): Boolean
}