package com.internshipproject.patientregistration.config.auth



data class AuthenticationResponse (
    val token: String = ""
) {
    companion object {
        fun builder() = Builder()
    }

    class Builder(
        var token: String = ""
    ) {
        fun token(token: String) = apply { this.token = token }
        fun build() = AuthenticationResponse(token)
    }
}
