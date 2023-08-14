package com.internshipproject.patientregistration.repository

import com.internshipproject.patientregistration.entity.user.Role
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface RoleRepository : JpaRepository<Role,Long> {

    //fun existsByName(name: String): Boolean
    fun findByName(name:String): Optional<Role>
}