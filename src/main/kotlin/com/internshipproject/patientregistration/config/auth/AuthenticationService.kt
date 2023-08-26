package com.internshipproject.patientregistration.config.auth


import com.internshipproject.patientregistration.dto._internal.UserStatus
import com.internshipproject.patientregistration.entity.auth.Token
import com.internshipproject.patientregistration.repository.jpa.TokenRepository
import com.internshipproject.patientregistration.entity.auth.TokenType
import com.internshipproject.patientregistration.entity.user.*
import com.internshipproject.patientregistration.exception.YourCustomEmailAlreadyExistsException
import com.internshipproject.patientregistration.service.JwtService
import com.internshipproject.patientregistration.entity.user.types.Patient
import com.internshipproject.patientregistration.repository.jpa.UserRepository
import com.internshipproject.patientregistration.service.RoleService
import lombok.RequiredArgsConstructor
import mu.KLogging
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder

import org.springframework.stereotype.Service

@Service
@RequiredArgsConstructor
class AuthenticationService(
    private val repository: UserRepository,
    private val roleService: RoleService,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager,
    private val tokenRepository: TokenRepository
) {

    companion object: KLogging()


    fun register(request: RegisterRequest): AuthenticationResponse {

        if (repository.existsByEmail(request.email))
        // Handle the case when the email is already registered
            throw YourCustomEmailAlreadyExistsException("This email is already registered.",HttpStatus.CONFLICT)

        //val doctorRole = Role()
        //doctorRole.name = "Doctor"

        val patientRole = roleService.addRole("Patient")

/*        val user : User = Doctor.builder()
            .firstName(request.firstname)
            .lastName(request.lastname)
            .email(request.email)
            .passw(passwordEncoder.encode( request.password))
            .roles(setOf(doctorRole))
            .specialization("Cardiology")
            .salary(20000.0)
            .build()*/
        val user : User = Patient.builder()
            .firstName(request.firstname)
            .lastName(request.lastname)
            .email(request.email)
            .passw(passwordEncoder.encode( request.password))
            .roles(setOf(patientRole))
            .age(request.age)
            .gender(request.gender)
            .userStatus(UserStatus.ACTIVE)
            .build()






        // Alternatively, you can return an error response to the user
        // and skip throwing an exception, based on your application's requirements.


        repository.save(user)
        val roleMap : Map<String,Any> = mapOf(
            "role" to  patientRole.name
        )
        var jwtToken = jwtService.generateToken(userDetails =  user, extraClaims =  roleMap)
        var token = Token.builder()
            .user(user)
            .token(jwtToken)
            .tokenType(TokenType.BEARER)
            .expired(false)
            .revoked(false)
            .build()


        tokenRepository.save(token)

        return AuthenticationResponse
            .builder()
            .token(jwtToken)
            .id(user.id)
            .build()

    }

    fun authenticate(request: AuthenticationRequest): AuthenticationResponse {
        val user = repository.findByEmail(request.email).orElseThrow{YourCustomEmailAlreadyExistsException("Wrong or missing credentials",HttpStatus.BAD_REQUEST)}
        user.userStatus = UserStatus.ACTIVE
        repository.save(user)

        try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    request.email,
                    request.password
                )
            )
        }catch (_: Exception){
            throw YourCustomEmailAlreadyExistsException("Wrong or missing credentials",HttpStatus.BAD_REQUEST)
        }


        val roleMap : Map<String,Any> = mapOf(
            "role" to  user.roles
        )
        val jwtToken = jwtService.generateToken(userDetails = user, extraClaims =  roleMap)
        revokeAllUserTokens(user)

        var token = Token.builder()
            .user(user)
            .token(jwtToken)
            .tokenType(TokenType.BEARER)
            .expired(false)
            .revoked(false)
            .build()


        tokenRepository.save(token)

        return AuthenticationResponse
            .builder()
            .token(jwtToken)
            .id(user.id)
            .build()
    }


    private fun revokeAllUserTokens(user: User){
        val validUserTokens = tokenRepository.findAllValidTokensByUser(user.id)
        if(validUserTokens.isEmpty() )
            return
        validUserTokens.forEach{
            it.expired = true
            it.revoked = true
        }
        tokenRepository.saveAll(validUserTokens)
    }

}