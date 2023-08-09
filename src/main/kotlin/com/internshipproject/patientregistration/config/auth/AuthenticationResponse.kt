package com.internshipproject.patientregistration.config.auth

import com.fasterxml.jackson.annotation.JsonProperty


data class AuthenticationResponse (
    val token: String,
    val id: Int = 0
) {
    companion object {
        fun builder() = Builder()
    }

    class Builder(
        private var token: String = "",
        var id : Int = 0
    ) {
        fun token(token: String) = apply { this.token = token }

        fun id(id:Int) = apply {this.id = id}
        fun build() = AuthenticationResponse(token,id)
    }
}
