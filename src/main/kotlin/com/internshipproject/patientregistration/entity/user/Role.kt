package com.internshipproject.patientregistration.entity.user

import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority

@Entity
data class Role(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    var name: String = ""

){
    companion object{
        fun builder(): Builder = Builder()

    }

    class Builder(
         private var name: String = ""
    ) {
        fun name(name: String) = apply { this.name = name }

        fun build(): Role {
            return Role(name = this.name)
        }

    }
}
