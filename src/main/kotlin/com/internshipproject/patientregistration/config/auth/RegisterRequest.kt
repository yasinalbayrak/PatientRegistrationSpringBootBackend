package com.internshipproject.patientregistration.config.auth

data class RegisterRequest(
    val firstname: String,
    val lastname: String,
    val email: String,
    val password: String,

) {

    companion object {
        fun builder() = Builder()
    }
    data class Builder(
        var firstname: String = "",
        var lastname: String = "",
        var email: String = "",
        var password: String = ""
    ) {
        fun firstname(firstname: String) = apply { this.firstname = firstname }
        fun lastname(lastname: String) = apply { this.lastname = lastname }
        fun email(email: String) = apply { this.email = email }
        fun password(password: String) = apply { this.password = password }
        fun build() = RegisterRequest(firstname, lastname, email, password)
    }
}
