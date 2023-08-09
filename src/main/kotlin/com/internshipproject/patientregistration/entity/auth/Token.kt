package com.internshipproject.patientregistration.entity.auth

import com.internshipproject.patientregistration.entity.user.User
import jakarta.persistence.*
import org.antlr.v4.runtime.Token
import kotlin.math.exp


@Entity
data class Token(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    val token: String,

    @Enumerated(EnumType.STRING)
    val tokenType: TokenType,

    var expired : Boolean,

    var revoked: Boolean,


    @ManyToOne
    @JoinColumn(
        name = "user_id"
    )
    val user:User

){
    companion object{
        fun builder() = Builder()
    }

    class Builder (
        var token: String = "",
        var tokenType: TokenType = TokenType.BEARER,
        var expired: Boolean = false,
        var revoked: Boolean = false,
        var user:User? = null
    ){
        fun token(token: String) = apply { this.token = token }

        fun tokenType(tokenType: TokenType) = apply { this.tokenType = tokenType }

        fun expired(expired: Boolean) = apply { this.expired = expired }

        fun revoked(revoked: Boolean) = apply { this.revoked = revoked }

        fun user(user: User) = apply { this.user = user }

        fun build() = Token(token =  token, tokenType =  tokenType, expired =  expired, revoked =  revoked, user =  user!!)


    }



}
