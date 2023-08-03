package com.internshipproject.patientregistration.controller

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.internshipproject.patientregistration.config.securityExceptions.CustomJsonFormatResponse
import com.internshipproject.patientregistration.dto._internal.DoctorDTO
import com.internshipproject.patientregistration.dto._public.DoctorDTOPublic
import com.internshipproject.patientregistration.dto._internal.UserDTO
import com.internshipproject.patientregistration.dto._public.UserDTOPublic
import com.internshipproject.patientregistration.exception.InvalidInputException
import com.internshipproject.patientregistration.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/v1/user/doctor")
class DoctorController (
    val userService: UserService
) {
    fun validateDoctorJson(jsonNode: JsonNode): DoctorDTO {
        val userNode = jsonNode["user"] ?: throw InvalidInputException("Invalid JSON format: 'user' is missing")
        val firstName = userNode["firstname"]?.textValue() ?: throw InvalidInputException("Invalid JSON format: 'firstname' is missing")
        val lastName = userNode["lastname"]?.textValue() ?: throw InvalidInputException("Invalid JSON format: 'lastname' is missing")
        val email = userNode["email"]?.textValue() ?: throw InvalidInputException("Invalid JSON format: 'email' is missing")
        val passw = userNode["passw"]?.textValue() ?: throw InvalidInputException("Invalid JSON format: 'passw' is missing")

        // Check for extra properties in the 'user' object
        val userFieldNames = userNode.fieldNames().asSequence().toSet()
        val validUserFieldNames = setOf("firstname", "lastname", "email", "passw")
        if (!validUserFieldNames.containsAll(userFieldNames)) {
            val extraFields = userFieldNames - validUserFieldNames
            throw InvalidInputException("Invalid JSON format: Extra fields found in 'user': $extraFields")
        }

        val specialization = jsonNode["specialization"]?.textValue() ?: throw InvalidInputException("Invalid JSON format: 'specialization' is missing")
        val salary = jsonNode["salary"]?.doubleValue() ?: throw InvalidInputException("Invalid JSON format: 'salary' is missing")

        // Check for extra properties in the main object
        val validFieldNames = setOf("user", "specialization", "salary")
        val mainFieldNames = jsonNode.fieldNames().asSequence().toSet()
        if (!validFieldNames.containsAll(mainFieldNames)) {
            val extraFields = mainFieldNames - validFieldNames
            throw InvalidInputException("Invalid JSON format: Extra fields found: $extraFields")
        }

        return DoctorDTO(
            UserDTO(null, firstName, lastName, email, passw),
            specialization,
            salary
        )
    }


    @PostMapping()
    fun addDoctor(@RequestBody requestBody: JsonNode): ResponseEntity<Any> {
        val objectMapper = ObjectMapper()

        return try {
            val doctorInput = validateDoctorJson(requestBody)

            val doctorDTO = userService.addDoctor(doctorInput)

            ResponseEntity.ok(doctorDTO)
        } catch (e: InvalidInputException) {

            ResponseEntity.badRequest().body(CustomJsonFormatResponse(
                "${HttpStatus.BAD_REQUEST}",
                e.message ?: "Wrong JSON Format",
                DoctorDTOPublic(UserDTOPublic("firstname", "lastname" , "email" ,"password"), "specialization" , 1000.0 )
            ))
        }
    }




    @GetMapping()
    fun retrieveAllDoctors(): Collection<DoctorDTO>{
        return userService.retrieveAllDoctors()
    }

    @GetMapping("/{id}")
    fun retrieveDoctor(@PathVariable id:Any): DoctorDTO {
        return userService.retrieveDoctor(id)
    }

    @DeleteMapping("/{id}")
    fun deleteDoctor(@PathVariable id:Any): ResponseEntity<Any> {
        return userService.deleteDoctor(id)
    }

    @PutMapping("/{id}")
    fun updateDoctor(@PathVariable id:Any, @RequestBody requestBody: JsonNode): ResponseEntity<Any> {
        return try {
            val doctorInput = validateDoctorJson(requestBody)

            val doctorDTO = userService.updateDoctor(doctorInput,id)

            ResponseEntity.ok(doctorDTO)
        } catch (e: InvalidInputException) {

            ResponseEntity.badRequest().body(CustomJsonFormatResponse(
                "${HttpStatus.BAD_REQUEST}",
                e.message ?: "Wrong JSON Format",
                DoctorDTOPublic(UserDTOPublic("firstname", "lastname" , "email" ,"password"), "specialization" , 1000.0 )
            ))
        }

    }

}