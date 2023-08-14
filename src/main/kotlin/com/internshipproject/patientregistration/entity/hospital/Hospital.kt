package com.internshipproject.patientregistration.entity.hospital

import com.internshipproject.patientregistration.entity.user.types.Doctor
import com.internshipproject.patientregistration.entity.user.types.Patient
import jakarta.persistence.*


@Entity
class Hospital(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Int = 0,

    var name: String,

    @ManyToMany
    @JoinTable(name = "WorksIn",
        joinColumns = [JoinColumn(name="hospital_id")],
        inverseJoinColumns = [JoinColumn(name= "doctor_id")]

    )
    val doctors: MutableSet<Doctor>,

    ) {

    data class Builder(
        var id: Int = 0,
        var doctors: MutableSet<Doctor> = mutableSetOf(),
        var name: String? = null

    ){

        fun id(id:Int) = apply { this.id = id }
        fun doctors(doctors:MutableSet<Doctor>) = apply { this.doctors = doctors }
        fun name(name : String) = apply { this.name = name }

        fun build() = Hospital(id,name!!,doctors)

    }

}