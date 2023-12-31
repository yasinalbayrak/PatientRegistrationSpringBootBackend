package com.internshipproject.patientregistration.util

import com.internshipproject.patientregistration.entity.user.Gender
import com.internshipproject.patientregistration.entity.user.Role
import com.internshipproject.patientregistration.entity.user.types.Doctor
import com.internshipproject.patientregistration.entity.user.types.Patient
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.util.*


fun doctorEntityList(role: Role): List<Doctor> = listOf(
    Doctor.builder()
        .age(22)
        .email("sila@gmail.com")
        .gender(Gender.FEMALE)
        .firstName("sila")
        .lastName("ozinan")
        .passw("1234")
        .roles(setOf(role))
        .specialization("cardiology")
        .build(),

    Doctor.builder()
        .age(30)
        .email("john@gmail.com")
        .gender(Gender.MALE)
        .firstName("John")
        .lastName("Doe")
        .passw("1234")
        .roles(setOf(role))
        .specialization("cardiology")

        .build(),

    Doctor.builder()
        .age(28)
        .email("emma@gmail.com")
        .gender(Gender.FEMALE)
        .firstName("Emma")
        .lastName("Johnson")
        .passw("1234")
        .roles(setOf(role))
        .specialization("cardiology")

        .build(),

    // Add more doctors if needed...
)


fun patientEntity(role:Role) = Patient
    .builder()
    .age(22)
    .email("yasin@gmail.com")
    .gender(Gender.MALE)
    .firstName("yasin")
    .lastName("albayrak")
    .passw("1234")
    .roles(setOf(role))
    .build()


fun roleEntity(role: String) = Role.builder().name(role).build()


