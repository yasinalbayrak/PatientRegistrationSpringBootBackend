package com.internshipproject.patientregistration.controller

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.internshipproject.patientregistration.exception.securityExceptions.CustomJsonFormatResponse
import com.internshipproject.patientregistration.dto._internal.PatientDTO
import com.internshipproject.patientregistration.dto._public.PatientDTOPublic
import com.internshipproject.patientregistration.entity.user.Gender
import com.internshipproject.patientregistration.exception.InvalidInputException
import com.internshipproject.patientregistration.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/v1/user/patient")
class PatientController (
    val userService: UserService

) {
    fun validatePatientJson(jsonNode: JsonNode): PatientDTO {

        val firstName = jsonNode["firstname"]?.textValue() ?: throw InvalidInputException("Invalid JSON format: 'firstname' is missing")
        val lastName = jsonNode["lastname"]?.textValue() ?: throw InvalidInputException("Invalid JSON format: 'lastname' is missing")
        val email = jsonNode["email"]?.textValue() ?: throw InvalidInputException("Invalid JSON format: 'email' is missing")
        val passw = jsonNode["passw"]?.textValue() ?: throw InvalidInputException("Invalid JSON format: 'passw' is missing")
        val gender = jsonNode["gender"]?.textValue() ?: throw InvalidInputException("Invalid JSON format: 'gender' is missing")
        val age =   if (jsonNode["age"] == null || !jsonNode["age"].isNumber )
            throw InvalidInputException("Invalid JSON format: 'age' is missing")
        else
            jsonNode["age"].intValue()
        // Check for extra properties in the 'user' object
        val userFieldNames = jsonNode.fieldNames().asSequence().toSet()
        val validUserFieldNames = setOf("firstname", "lastname", "email","passw","gender","age")
        if (!validUserFieldNames.containsAll(userFieldNames)) {
            val extraFields = userFieldNames - validUserFieldNames
            throw InvalidInputException("Invalid JSON format: Extra fields found in 'user': $extraFields")
        }

        return PatientDTO(null, firstName, lastName, email, "1234",gender.let {
            when (it) {
                "MALE" -> Gender.MALE
                "FEMALE" -> Gender.FEMALE
                else -> throw InvalidInputException("Invalid JSON format: Gender should be Male or Female")
            }

        },age )

    }


    @PostMapping()
    fun addPatient(@RequestBody requestBody: JsonNode): ResponseEntity<Any> {
        val objectMapper = ObjectMapper()

        return try {
            val patientInput = validatePatientJson(requestBody)

            val userDTO: PatientDTO = userService.addPatient(patientInput)

            ResponseEntity.status(HttpStatus.CREATED).body(userDTO)
        } catch (e: InvalidInputException) {

            ResponseEntity.badRequest().body(
                CustomJsonFormatResponse(
                "${HttpStatus.BAD_REQUEST}",
                e.message ?: "Wrong JSON Format",
                PatientDTOPublic("firstname", "lastname" , "email" ,"password",Gender.MALE,21)
            )
            )
        }
    }




    @GetMapping()
    fun retrieveAllPatients(): Collection<PatientDTO>{
        return userService.retrieveAllPatients()
    }

    @GetMapping("/{id}")
    fun retrievePatient(@PathVariable id:Any): PatientDTO {
        return userService.retrievePatient(id)
    }


    @DeleteMapping("/{id}")
    fun deletePatient(@PathVariable id:Any): ResponseEntity<Any> {
        return userService.deletePatient(id)
    }

    @PutMapping("/{id}")
    fun updatePatient(@PathVariable id:Any, @RequestBody requestBody: JsonNode): ResponseEntity<Any> {
        return try {
            val patientInput = validatePatientJson(requestBody)

            val patientDTO = userService.updatePatient(patientInput,id)

            ResponseEntity.ok(patientDTO)
        } catch (e: InvalidInputException) {

            ResponseEntity.badRequest().body(
                CustomJsonFormatResponse(
                "${HttpStatus.BAD_REQUEST}",
                e.message ?: "Wrong JSON Format",
                    PatientDTOPublic("firstname", "lastname" , "email" ,"password", Gender.MALE,21)
            )
            )
        }

    }

}