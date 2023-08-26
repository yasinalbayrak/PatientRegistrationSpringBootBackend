package com.internshipproject.patientregistration.controller

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.internshipproject.patientregistration.exception.securityExceptions.CustomJsonFormatResponse
import com.internshipproject.patientregistration.dto._internal.DoctorDTO
import com.internshipproject.patientregistration.dto._internal.HospitalDTO
import com.internshipproject.patientregistration.dto._internal.PatientDTO
import com.internshipproject.patientregistration.dto._internal.UserDTO
import com.internshipproject.patientregistration.dto._public.PatientDTOPublic
import com.internshipproject.patientregistration.entity.user.Gender
import com.internshipproject.patientregistration.exception.InvalidInputException
import com.internshipproject.patientregistration.service.HospitalService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/v1/hospital")
class HospitalController (
    val hospitalService: HospitalService
) {
    fun validateHospitalJson(jsonNode: JsonNode): HospitalDTO {
        val name = jsonNode["name"]?.textValue() ?: throw InvalidInputException("Invalid JSON format: 'name' is missing")

        val fieldNames = jsonNode.fieldNames().asSequence().toSet()
        val validFieldNames = setOf("name")
        if (!validFieldNames.containsAll(fieldNames)) {
            val extraFields = fieldNames - validFieldNames
            throw InvalidInputException("Invalid JSON format: Extra fields found: $extraFields")
        }

        return HospitalDTO(name = name)
    }


    @PostMapping
    fun addHospital(@RequestBody requestBody: JsonNode): ResponseEntity<Any> {
        val objectMapper = ObjectMapper()

        return try {
            val hospitalInput = validateHospitalJson(requestBody)

            val hospitalDTO: HospitalDTO = hospitalService.addHospital(hospitalInput)

            ResponseEntity.status(HttpStatus.CREATED).body(hospitalDTO)
        } catch (e: InvalidInputException) {

            ResponseEntity.badRequest().body(
                CustomJsonFormatResponse(
                    "${HttpStatus.BAD_REQUEST}",
                    e.message ?: "Wrong JSON Format",
                    HospitalDTO(1, "Hospital Name")
                )
            )
        }
    }

    @GetMapping()
    fun retrieveAllHospitals(): Collection<HospitalDTO>{
        return hospitalService.retrieveAllHospitals()
    }

    @GetMapping("/{id}")
    fun retrieveHospital(@PathVariable id:Any): HospitalDTO {
        return hospitalService.retrieveHospital(id)
    }

    @DeleteMapping("/{id}")
    fun deleteHospital(@PathVariable id:Any): ResponseEntity<Any> {
        return hospitalService.deletePatient(id)
    }


    @PutMapping("/{id}")
    fun updateHospital(@PathVariable id:Any, @RequestBody requestBody: JsonNode): ResponseEntity<Any> {
        return try {
            val hospitalInput = validateHospitalJson(requestBody)

            val hospitalDTO = hospitalService.updateHospital(hospitalInput,id)

            ResponseEntity.ok(hospitalDTO)
        } catch (e: InvalidInputException) {

            ResponseEntity.badRequest().body(
                CustomJsonFormatResponse(
                    "${HttpStatus.BAD_REQUEST}",
                    e.message ?: "Wrong JSON Format",
                    HospitalDTO(name = "Hospital Name")
                )
            )
        }

    }
}