package com.internshipproject.patientregistration.entity.user.types

import com.internshipproject.patientregistration.dto._internal.UserStatus
import com.internshipproject.patientregistration.entity.hospital.Hospital
import com.internshipproject.patientregistration.entity.user.Gender
import com.internshipproject.patientregistration.entity.user.Role
import com.internshipproject.patientregistration.entity.user.User
import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

@Entity
@PrimaryKeyJoinColumn(name = "id") // Replace "doctor_id" with the appropriate column name
@DiscriminatorValue("doctor") // The discriminator value to identify this subclass
data class Doctor(

    var specialization: String = "",

    @ManyToMany
    @JoinTable(name = "appointment",
        joinColumns = [JoinColumn(name = "doctor_id")],
        inverseJoinColumns = [JoinColumn(name = "patient_id")]
    )
    private val patients:MutableSet<Patient> =  mutableSetOf(),

    @ManyToMany(mappedBy = "doctors")
    private val hospitals: MutableSet<Hospital> = mutableSetOf()
) : User() {

    companion object {
        fun builder() : Builder = Builder()
    }
    data class Builder(
        private var firstName: String = "",
        private var lastName: String = "",
        private var email: String = "",
        private var passw: String = "",
        private var gender: Gender = Gender.MALE,
        private var age: Int = 0,
        private var userStatus: UserStatus = UserStatus.INACTIVE,
        private var roles: Set<Role> = emptySet(),
        private var specialization: String = "",

        ) {

        fun firstName(firstName: String) = apply { this.firstName = firstName }
        fun lastName(lastName: String) = apply { this.lastName = lastName }
        fun email(email: String) = apply { this.email = email }
        fun passw(passw: String) = apply { this.passw = passw }
        fun gender(gender: Gender) = apply { this.gender = gender }
        fun age(age: Int) = apply { this.age = age }
        fun roles(roles: Set<Role>) = apply { this.roles = roles }
        fun specialization(specialization: String) = apply { this.specialization = specialization }

        fun userStatus(userStatus: UserStatus) = apply { this.userStatus = userStatus }

        fun build() : Doctor {
            val newDoctor = Doctor(
                specialization = specialization,

            )
            newDoctor.firstName = firstName
            newDoctor.lastName = lastName
            newDoctor.email = email
            newDoctor.passw = passw
            newDoctor.age= age
            newDoctor.gender = gender
            newDoctor.roles = roles
            newDoctor.userStatus = userStatus
            return newDoctor
        }
    }
}



