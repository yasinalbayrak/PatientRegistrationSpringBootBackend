package com.internshipproject.patientregistration.repository

import com.internshipproject.patientregistration.entity.user.types.Admin
import org.springframework.data.jpa.repository.JpaRepository

interface AdminRepository : JpaRepository<Admin,Int> {
}
