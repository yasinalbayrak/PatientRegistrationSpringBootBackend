package com.internshipproject.patientregistration.controller

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.internshipproject.patientregistration.exception.securityExceptions.CustomJsonFormatResponse
import com.internshipproject.patientregistration.dto._internal.AppointmentDTO
import com.internshipproject.patientregistration.dto._internal.WorksInDTO
import com.internshipproject.patientregistration.dto._public.AppointmentDTOPublic
import com.internshipproject.patientregistration.dto._public.WorksInDTOPublic
import com.internshipproject.patientregistration.exception.InvalidInputException
import com.internshipproject.patientregistration.exception.NoUserFoundException
import com.internshipproject.patientregistration.service.WorksInService

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/v1/worksin")
class WorksInController(
    val worksInService: WorksInService
) {
    fun validateWorksInJson(jsonNode: JsonNode): WorksInDTOPublic {
        val doctorID = jsonNode["doctorID"]?.intValue() ?: throw InvalidInputException("Invalid JSON format: 'doctorID' is missing")
        val hospitalID = jsonNode["hospitalID"]?.intValue() ?: throw InvalidInputException("Invalid JSON format: 'hospitalID' is missing")
        val startDate = jsonNode["startDate"]?.textValue() ?: throw InvalidInputException("Invalid JSON format: 'startDate' is missing")
        val endDate = jsonNode["endDate"]?.textValue()
        val salary = jsonNode["salary"]?.doubleValue() ?: throw InvalidInputException("Invalid JSON format: 'salary' is missing")


        val fieldNames = jsonNode.fieldNames().asSequence().toSet()
        val validFieldNames = setOf("doctorID", "hospitalID", "startDate", "endDate", "salary")
        if (!validFieldNames.containsAll(fieldNames)) {
            val extraFields = fieldNames - validFieldNames
            throw InvalidInputException("Invalid JSON format: Extra fields found: $extraFields")
        }

        return WorksInDTOPublic(doctorID, hospitalID, startDate, endDate, salary)
    }

    @PostMapping
    fun addWorksIn(@RequestBody requestBody: JsonNode): ResponseEntity<Any> {
        return try {
            val workInput = validateWorksInJson(requestBody)

            val worksInDTO: WorksInDTO = worksInService.addWorksIn(workInput)

            ResponseEntity.status(HttpStatus.CREATED).body(worksInDTO)
        } catch (e: InvalidInputException) {

            ResponseEntity.badRequest().body(
                CustomJsonFormatResponse(
                    "${HttpStatus.BAD_REQUEST}",
                    e.message ?: "Wrong JSON Format",
                    WorksInDTOPublic(1, 1 ,"yyyy-MM-dd","yyyy-MM-dd",10000.0)
                )
            )
        }
    }

    @GetMapping
    fun getAllWorksIns() : Collection<WorksInDTO> {
        return worksInService.retrieveAllWorksIns()
    }

    @GetMapping("/{id}")
    fun getWorksIn(@PathVariable id:Any): WorksInDTO{
        return worksInService.retrieveWorksIn(id)
    }

    @GetMapping("/doctor/{id}")
    fun getAllWorksInsByDoctorId(@PathVariable id: Any) : Collection<WorksInDTO> {
        return worksInService.getAllWorksInsByDoctorId(id)
    }
    @GetMapping("/hospital/{id}")
    fun getAllWorksInsByHospitalId(@PathVariable id: Any) : Collection<WorksInDTO> {
        return worksInService.getAllWorksInsByHospitalId(id)
    }

    @DeleteMapping("/{id}")
    fun deleteWorksIn(@PathVariable id:Any) : ResponseEntity<Any> {
        return worksInService.deleteWorksIn(id)
    }

    @PutMapping("/{id}")
    fun updateAppointment(@RequestBody requestBody: JsonNode,@PathVariable id:Any): ResponseEntity<Any> {

        return try {
            val worksInInput = validateWorksInJson(requestBody)

            val appointmentDTO: WorksInDTO = worksInService.updateWorksIn(worksInInput,id)

            ResponseEntity.ok(appointmentDTO)
        } catch (e: InvalidInputException) {

            ResponseEntity.badRequest().body(
                CustomJsonFormatResponse(
                    "${HttpStatus.BAD_REQUEST}",
                    e.message ?: "Wrong JSON Format",
                    WorksInDTOPublic(1, 1 ,"yyyy-MM-dd","yyyy-MM-dd",10000.0)
                )
            )
        }
    }

}