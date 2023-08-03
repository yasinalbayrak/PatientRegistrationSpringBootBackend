package com.internshipproject.patientregistration.entity.user.types
import com.internshipproject.patientregistration.entity.user.Role
import com.internshipproject.patientregistration.entity.user.User
import jakarta.persistence.*

@Entity
@PrimaryKeyJoinColumn(name = "id") // Replace "patient_id" with the appropriate column name
@DiscriminatorValue("patient") // The discriminator value to identify this subclass
class Patient(

    @ManyToMany(mappedBy = "patients")
    private val doctors:MutableSet<Doctor> = mutableSetOf()


) : User() {

    companion object {
        fun builder() : Builder = Builder()
    }
    data class Builder(
        private var firstName: String = "",
        private var lastName: String = "",
        private var email: String = "",
        private var passw: String = "",
        private var roles: Set<Role> = emptySet(),

        ) {

        fun firstName(firstName: String) = apply { this.firstName = firstName }
        fun lastName(lastName: String) = apply { this.lastName = lastName }
        fun email(email: String) = apply { this.email = email }
        fun passw(passw: String) = apply { this.passw = passw }
        fun roles(roles: Set<Role>) = apply { this.roles = roles }


        fun build() : Patient {
            val newPatient = Patient()
            newPatient.firstName = firstName
            newPatient.lastName = lastName
            newPatient.email = email
            newPatient.passw = passw
            newPatient.roles = roles
            return newPatient
        }
    }



}