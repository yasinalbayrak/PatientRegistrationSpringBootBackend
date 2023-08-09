package com.internshipproject.patientregistration.config.auth

import com.internshipproject.patientregistration.entity.user.Gender

data class RegisterRequest(
    val firstname: String,
    val lastname: String,
    val email: String,
    val password: String,
    val gender: Gender,
    val age: Int

) {

    companion object {
        fun builder() = Builder()
    }
    data class Builder(
        var firstname: String = "",
        var lastname: String = "",
        var email: String = "",
        var password: String = "",
        var gender: Gender = Gender.MALE,
        var age: Int = 25
    ) {
        fun firstname(firstname: String) = apply { this.firstname = firstname }
        fun lastname(lastname: String) = apply { this.lastname = lastname }
        fun email(email: String) = apply { this.email = email }
        fun password(password: String) = apply { this.password = password }

        fun gender(gender: Gender) = apply { this.gender = gender }

        fun age(age: Int) = apply{ this.age = age}
        fun build() = RegisterRequest(firstname, lastname, email, password,gender,age)
    }
}
