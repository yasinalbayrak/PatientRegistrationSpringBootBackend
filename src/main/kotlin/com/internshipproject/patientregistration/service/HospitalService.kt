package com.internshipproject.patientregistration.service

import com.internshipproject.patientregistration.dto._internal.HospitalDTO
import com.internshipproject.patientregistration.entity.hospital.Hospital
import com.internshipproject.patientregistration.repository.jpa.HospitalRepository
import com.internshipproject.patientregistration.exception.InvalidInputException
import com.internshipproject.patientregistration.exception.NoUserFoundException
import com.internshipproject.patientregistration.exception.YourCustomEmailAlreadyExistsException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service


@Service
class HospitalService (
    val hospitalRepository: HospitalRepository
) {
    fun addHospital(hospitalInput: HospitalDTO): HospitalDTO {
        if(hospitalRepository.existsByName(hospitalInput.name))
            throw YourCustomEmailAlreadyExistsException("This name is already belong to another hospital.", HttpStatus.CONFLICT)
        val hospitalEntity : Hospital = hospitalInput.let {


            Hospital
                .Builder()
                .name(it.name)
                .build()
        }

        hospitalRepository.save(hospitalEntity)

        var result = hospitalEntity.let {
            HospitalDTO(it.id,it.name)
        }
        return result
    }

    fun retrieveAllHospitals(): Collection<HospitalDTO> {
        return hospitalRepository.findAll().map {
            HospitalDTO(
                it.id,it.name
            )
        }
    }

    fun retrieveHospital(id: Any): HospitalDTO {
        when (val idInt = id.toString().toIntOrNull()) {
            is Int -> {
                val hospitalOptional = hospitalRepository.findById(idInt)
                val hospital = hospitalOptional.orElseThrow { NoUserFoundException("Hospital with ID $id not found") }

                return HospitalDTO(
                    hospital.id,hospital.name
                )
            }
            else -> throw InvalidInputException("Invalid ID format. ID must be an integer.")
        }
    }

    fun deletePatient(id: Any): ResponseEntity<Any> {
        when (val idInt = id.toString().toIntOrNull()) {
            is Int -> {
                val hospitalOptional = hospitalRepository.findById(idInt)
                val hospital = hospitalOptional.orElseThrow { NoUserFoundException("Hospital with ID $id not found") }

                hospitalRepository.deleteById(idInt)

                return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
            }
            else -> throw InvalidInputException("Invalid ID format. ID must be an integer.")
        }
    }

    fun updateHospital(hospitalInput: HospitalDTO, id: Any): Any? {
        when (val idInt = id.toString().toIntOrNull()) {
            is Int -> {
                val hospitalOptional = hospitalRepository.findById(idInt)
                val hospital : Hospital = hospitalOptional.orElseThrow { NoUserFoundException("Hospital with ID $id not found") }

                val updatedHospital = hospital.let {
                    it.name = hospitalInput.name

                    hospitalRepository.save(it)
                    HospitalDTO(it.id,it.name)


                }

                return updatedHospital
            }
            else -> throw InvalidInputException("Invalid ID format. ID must be an integer.")
        }
    }

    fun getHospital(id : Int) = hospitalRepository.findById(id)


}