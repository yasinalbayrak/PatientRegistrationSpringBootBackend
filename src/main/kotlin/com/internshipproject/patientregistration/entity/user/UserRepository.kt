package com.internshipproject.patientregistration.entity.user

import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserRepository : JpaRepository<User,Int> {

    fun findByEmail(email:String):Optional<User>
    fun existsByEmail(email: String): Boolean
}