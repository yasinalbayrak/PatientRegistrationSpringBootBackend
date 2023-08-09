package com.internshipproject.patientregistration.controller

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.internshipproject.patientregistration.config.securityExceptions.CustomJsonFormatResponse
import com.internshipproject.patientregistration.dto._internal.AppointmentDTO
import com.internshipproject.patientregistration.dto._internal.PatientDTO
import com.internshipproject.patientregistration.dto._public.AppointmentDTOPublic
import com.internshipproject.patientregistration.dto._public.PatientDTOPublic
import com.internshipproject.patientregistration.exception.InvalidInputException
import com.internshipproject.patientregistration.service.AppointmentService
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
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*


@RestController
@RequestMapping("/api/v1/appointment")
class AppointmentController(
    val appointmentService: AppointmentService
) {


    // Function to validate if the input string is a valid date with full hour between 08:00 and 18:00
    fun isValidDateWithFullHourAndWorkingHours(input: String, dateFormat: String): Boolean {
        val formatter = DateTimeFormatter.ofPattern(dateFormat)
        return try {
            val parsedDate = LocalDateTime.parse(input, formatter)
            val hour = parsedDate.hour
            val minute = parsedDate.minute
            val workingStart = LocalTime.of(8, 0)
            val workingEnd = LocalTime.of(18, 0)
            val time = LocalTime.of(hour, minute)

            if (parsedDate.isBefore(LocalDateTime.now())) {
                return false
            }

            minute == 0 && ((time == workingStart) || (time.isAfter(workingStart) && time.isBefore(workingEnd)))
        } catch (e: Exception) {
            // Handle or log the exception here
            false
        }
    }



    fun validateAppointmentJson(jsonNode: JsonNode): AppointmentDTOPublic {
        if (jsonNode["doctorID"] == null || !jsonNode["doctorID"].isNumber) {
            throw InvalidInputException("Invalid JSON format: 'doctorID' is missing or not a valid numeric value")
        }

        if (jsonNode["patientID"] == null || !jsonNode["patientID"].isNumber) {
            throw InvalidInputException("Invalid JSON format: 'patientID' is missing or not a valid numeric value")
        }
        if (jsonNode["date"] == null || !isValidDateWithFullHourAndWorkingHours(jsonNode["date"].textValue(),"yyyy-MM-dd HH:mm")) {
            throw InvalidInputException("Invalid JSON format: 'date' is missing or not a valid date.")
        }
        val doctorID = jsonNode["doctorID"].asInt()
        val patientID = jsonNode["patientID"].asInt()
        val date = jsonNode["date"].textValue()

        // Check for extra properties in the 'user' object
        val appointmentFieldNames = jsonNode.fieldNames().asSequence().toSet()
        val validAppointmentFieldNames = setOf("doctorID", "patientID","date")
        if (!validAppointmentFieldNames.containsAll(appointmentFieldNames)) {
            val extraFields = appointmentFieldNames - validAppointmentFieldNames
            throw InvalidInputException("Invalid JSON format: Extra fields found: $extraFields")
        }

        return AppointmentDTOPublic(doctorID, patientID,date)

    }


    @PostMapping
    fun addAppointment(@RequestBody requestBody: JsonNode): ResponseEntity<Any> {
        val objectMapper = ObjectMapper()

        return try {
            val appointmentInput = validateAppointmentJson(requestBody)

            val appointmentDTO: AppointmentDTO = appointmentService.addAppointment(appointmentInput)

            ResponseEntity.status(HttpStatus.CREATED).body(appointmentDTO)
        } catch (e: InvalidInputException) {

            ResponseEntity.badRequest().body(
                CustomJsonFormatResponse(
                    "${HttpStatus.BAD_REQUEST}",
                    e.message ?: "Wrong JSON Format",
                    AppointmentDTOPublic(1, 1 ,"yyyy-MM-dd HH:mm")
                )
            )
        }
    }

    @GetMapping
    fun getAllAppointments() : Collection<AppointmentDTO> {
        return appointmentService.retrieveAllAppointments()
    }

    @GetMapping("/user/{id}")
    fun getAllAppointmentsByUserId(@PathVariable id: Any) : Collection<AppointmentDTO> {
        return appointmentService.getAllAppointmentsByUserId(id)
    }


    @GetMapping("/{id}")
    fun getAppointment(@PathVariable id:Any): AppointmentDTO{
        return appointmentService.retrieveAppointment(id)
    }

    @DeleteMapping("/{id}")
    fun deleteAppointment(@PathVariable id:Any) : ResponseEntity<Any> {
        return appointmentService.deleteAppointment(id)
    }

    @PutMapping("/{id}")
    fun updateAppointment(@RequestBody requestBody: JsonNode,@PathVariable id:Any): ResponseEntity<Any> {
        val objectMapper = ObjectMapper()

        return try {
            val appointmentInput = validateAppointmentJson(requestBody)

            val appointmentDTO: AppointmentDTO = appointmentService.updateAppointment(appointmentInput,id)

            ResponseEntity.ok(appointmentDTO)
        } catch (e: InvalidInputException) {

            ResponseEntity.badRequest().body(
                CustomJsonFormatResponse(
                    "${HttpStatus.BAD_REQUEST}",
                    e.message ?: "Wrong JSON Format",
                    AppointmentDTOPublic(1, 1  )
                )
            )
        }
    }


    @PostMapping("/{id}")
    fun cancelAppointment(@PathVariable id:Any): ResponseEntity<Any> {
        return appointmentService.cancelAppointment(id)
    }


}