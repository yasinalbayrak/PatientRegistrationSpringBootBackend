package com.internshipproject.patientregistration.service

import com.internshipproject.patientregistration.entity.user.Role
import com.internshipproject.patientregistration.repository.jpa.RoleRepository
import org.springframework.stereotype.Service


@Service
class RoleService (
    val roleRepository: RoleRepository
) {

    fun addRole(name:String): Role {
        val optionalRole = roleRepository.findByName(name)

        return if(optionalRole.isPresent){
            optionalRole.get()
        } else{
            val role = Role.builder().name(name).build()
            roleRepository.save(role)
        }
    }
}