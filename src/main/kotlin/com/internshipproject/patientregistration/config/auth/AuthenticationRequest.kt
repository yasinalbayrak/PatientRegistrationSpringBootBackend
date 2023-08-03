package com.internshipproject.patientregistration.config.auth

data class AuthenticationRequest (
    val email: String,
    val password: String
) {
    companion object {
        fun builder() = Builder()
    }

    data class Builder(
        var email: String = "",
        var password: String = ""
    ) {
        fun email(email: String) = apply { this.email = email }
        fun password(password: String) = apply { this.password = password }

        fun build() = AuthenticationRequest(email, password)
    }
}
