package com.internshipproject.patientregistration.config.auth


import com.internshipproject.patientregistration.exception.YourCustomEmailAlreadyExistsException
import com.internshipproject.patientregistration.service.JwtService
import com.internshipproject.patientregistration.entity.user.Role
import com.internshipproject.patientregistration.entity.user.RoleRepository
import com.internshipproject.patientregistration.entity.user.User
import com.internshipproject.patientregistration.entity.user.UserRepository
import com.internshipproject.patientregistration.entity.user.types.Doctor
import com.internshipproject.patientregistration.entity.user.types.DoctorRepository
import com.internshipproject.patientregistration.entity.user.types.Patient
import com.internshipproject.patientregistration.service.RoleService
import lombok.RequiredArgsConstructor
import mu.KLogging
import org.springframework.dao.DataIntegrityViolationException
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
    private val authenticationManager: AuthenticationManager
) {

    companion object: KLogging()


    fun register(request: RegisterRequest): AuthenticationResponse {

        if (repository.existsByEmail(request.email))
        // Handle the case when the email is already registered
            throw YourCustomEmailAlreadyExistsException("This email is already registered.",HttpStatus.CONFLICT)

        //val doctorRole = Role()
        //doctorRole.name = "Doctor"

        val patientRole = roleService.addRole("Patient")
        logger.info ("Yasin: ${request.firstname}")
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
            .build()






        // Alternatively, you can return an error response to the user
        // and skip throwing an exception, based on your application's requirements.

        logger.info ("Yasin: ${user.firstName}")
        repository.save(user)
        val roleMap : Map<String,Any> = mapOf(
            "role" to  patientRole.name
        )
        var jwtToken = jwtService.generateToken(userDetails =  user, extraClaims =  roleMap)



        return AuthenticationResponse
            .builder()
            .token(jwtToken)
            .id(user.id)
            .build()

    }

    fun authenticate(request: AuthenticationRequest): AuthenticationResponse {
        val user = repository.findByEmail(request.email).orElseThrow{YourCustomEmailAlreadyExistsException("Wrong or missing credentials",HttpStatus.BAD_REQUEST)}

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

        return AuthenticationResponse
            .builder()
            .token(jwtToken)
            .id(user.id)
            .build()
    }

}