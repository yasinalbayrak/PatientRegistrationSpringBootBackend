package com.internshipproject.patientregistration.service

import com.internshipproject.patientregistration.dto._internal.*
import com.internshipproject.patientregistration.dto._public.WorksInDTOPublic
import com.internshipproject.patientregistration.entity.hospital.Hospital
import com.internshipproject.patientregistration.entity.user.types.Doctor
import com.internshipproject.patientregistration.entity.worksIn.WorksIn
import com.internshipproject.patientregistration.exception.InvalidInputException
import com.internshipproject.patientregistration.repository.WorksInRepository
import com.internshipproject.patientregistration.exception.NoUserFoundException
import com.internshipproject.patientregistration.exception.YourCustomEmailAlreadyExistsException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date


@Service
class WorksInService (
    val worksInRepository: WorksInRepository,
    val userService: UserService,
    val hospitalService: HospitalService
) {
    fun convertStringToLocalDate(dateString: String?): LocalDate? {
        if(dateString == null) return null
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return LocalDate.parse(dateString, formatter)
    }
    fun addWorksIn(workInput: WorksInDTOPublic): WorksInDTO {

        val optionalDoctor =  userService.getDoctor(doctorId = workInput.doctorID)
        val doctor : Doctor = optionalDoctor.orElseThrow { NoUserFoundException("Doctor with ID ${workInput.doctorID} not found") }

        val optionalHospital =  hospitalService.getHospital(id = workInput.hospitalID)
        val hospital : Hospital = optionalHospital.orElseThrow { NoUserFoundException("Hospital with ID ${workInput.hospitalID} not found") }

        val startDate : LocalDate = convertStringToLocalDate(workInput.startDate) ?: throw InvalidInputException("startDate should not be null.")
        val endDate : LocalDate? = convertStringToLocalDate(workInput.endDate)

        // validate end Date
        endDate?.takeIf {
            it.isBefore(startDate) || it.isAfter(LocalDate.now())  }
                ?.let {
                    throw InvalidInputException("Invalid endDate")
                }


        if(worksInRepository.conflictsWithOtherWorkDates(workInput.doctorID,startDate,endDate))
            throw YourCustomEmailAlreadyExistsException("This date is conflicting with other working dates, check it again.", HttpStatus.CONFLICT)





        val worksInEntity : WorksIn = workInput.let {
            WorksIn.builder()
                .doctor(doctor)
                .hospital(hospital)
                .startDate(startDate)
                .endDate(endDate)
                .stillWorks(it.endDate.let { date ->
                    when(date){
                        null -> true
                        else -> false
                    }
                })
                .salary(it.salary)
                .build()
        }

        worksInRepository.save(worksInEntity)

        var result = worksInEntity.let {
            WorksInDTO(
                id = it.id,
                doctor = DoctorDTO(
                    UserDTO(id = it.doctor!!.id, it.doctor!!.firstName,
                        it.doctor!!.lastName,it.doctor!!.email,it.doctor!!.passw, it.doctor!!.gender,it.doctor!!.age,"Doctor"),
                    it.doctor!!.specialization,
                ),

                hospital = HospitalDTO(id = it.hospital!!.id, name = it.hospital!!.name),
                startDate = it.startDate.toString(),
                endDate = it.endDate.toString(),
                salary = it.salary!!,
                stillWorks = it.stillWorks
            )
        }
        return result
    }

    fun retrieveAllWorksIns(): Collection<WorksInDTO> {
        return worksInRepository.findAll().map {
            WorksInDTO(
                id= it.id,
                doctor = DoctorDTO(
                    UserDTO(id = it.doctor!!.id, it.doctor!!.firstName,it.doctor!!.lastName,it.doctor!!.email,it.doctor!!.passw,it.doctor!!.gender,it.doctor!!.age,"Doctor"),
                    it.doctor!!.specialization,
                ),
                hospital = HospitalDTO(id = it.hospital!!.id, name = it.hospital!!.name ),
                startDate = it.startDate.toString(),
                endDate = it.endDate.toString(),
                salary = it.salary!!,
                stillWorks = it.stillWorks
            )
        }
    }

    fun retrieveWorksIn(id: Any): WorksInDTO {
        when (val idInt = id.toString().toIntOrNull()) {
            is Int -> {
                val worksInOptional = worksInRepository.findById(idInt)
                val worksIn = worksInOptional.orElseThrow { NoUserFoundException("WorksIn with ID $id not found") }

                return WorksInDTO(
                    id= worksIn.id,
                    doctor = DoctorDTO(
                        UserDTO(id = worksIn.doctor!!.id, worksIn.doctor!!.firstName,worksIn.doctor!!.lastName,worksIn.doctor!!.email,worksIn.doctor!!.passw,worksIn.doctor!!.gender,worksIn.doctor!!.age,"Doctor"),
                        worksIn.doctor!!.specialization,
                    ),
                    hospital = HospitalDTO(id = worksIn.hospital!!.id, name = worksIn.hospital!!.name ),
                    startDate = worksIn.startDate.toString(),
                    endDate = worksIn.endDate.toString(),
                    salary = worksIn.salary!!,
                    stillWorks = worksIn.stillWorks
                )
            }
            else -> throw InvalidInputException("Invalid ID format. ID must be an integer.")
        }
    }

    fun getAllWorksInsByDoctorId(id: Any): Collection<WorksInDTO> {
        when (val idInt = id.toString().toIntOrNull()) {
            is Int -> {
                val worksInOptional = worksInRepository.findByDoctorId(idInt)

                if (!worksInOptional.isPresent) {
                    throw NoUserFoundException("Works In for the Doctor with ID $id not found")
                }

                val worksIns = worksInOptional.get()

                return worksIns.map {
                    WorksInDTO(
                    id= it.id,
                    doctor = DoctorDTO(
                        UserDTO(id = it.doctor!!.id, it.doctor!!.firstName,it.doctor!!.lastName,it.doctor!!.email,it.doctor!!.passw,it.doctor!!.gender,it.doctor!!.age,"Doctor"),
                        it.doctor!!.specialization,
                    ),
                    hospital = HospitalDTO(id = it.hospital!!.id, name = it.hospital!!.name ),
                    startDate = it.startDate.toString(),
                    endDate = it.endDate.toString(),
                    salary = it.salary!!,
                    stillWorks = it.stillWorks
                )}
            }
            else -> throw InvalidInputException("Invalid ID format. ID must be an integer.")
        }
    }


    fun getAllWorksInsByHospitalId(id: Any): Collection<WorksInDTO> {
        when (val idInt = id.toString().toIntOrNull()) {
            is Int -> {
                val worksInOptional = worksInRepository.findByHospitalId(idInt)
                val worksIns = worksInOptional.orElseThrow { NoUserFoundException("Works In for the Doctor with ID $id not found") }

                return worksIns.map {
                    WorksInDTO(
                        id= it.id,
                        doctor = DoctorDTO(
                            UserDTO(id = it.doctor!!.id, it.doctor!!.firstName,it.doctor!!.lastName,it.doctor!!.email,it.doctor!!.passw,it.doctor!!.gender,it.doctor!!.age,"Doctor"),
                            it.doctor!!.specialization,
                        ),
                        hospital = HospitalDTO(id = it.hospital!!.id, name = it.hospital!!.name ),
                        startDate = it.startDate.toString(),
                        endDate = it.endDate.toString(),
                        salary = it.salary!!,
                        stillWorks = it.stillWorks
                    )}
            }
            else -> throw InvalidInputException("Invalid ID format. ID must be an integer.")
        }
    }

    fun deleteWorksIn(id: Any): ResponseEntity<Any> {
        when (val idInt = id.toString().toIntOrNull()) {
            is Int -> {
                val worksInOptional = worksInRepository.findById(idInt)
                val worksIn = worksInOptional.orElseThrow { NoUserFoundException("WorksIn with ID $id not found") }

                worksInRepository.deleteById(idInt)

                return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
            }
            else -> throw InvalidInputException("Invalid ID format. ID must be an integer.")
        }
    }

    fun updateWorksIn(worksInInput: WorksInDTOPublic, id: Any): WorksInDTO {
        when (val idInt = id.toString().toIntOrNull()) {
            is Int -> {
                val worksInOptional = worksInRepository.findById(idInt)
                val worksIn = worksInOptional.orElseThrow { NoUserFoundException("WorksIn with ID $id not found") }

                val updatedWorksIn = worksIn.let {
                    it.doctor = userService.getDoctor(worksInInput.doctorID).orElseThrow{NoUserFoundException("Doctor with ID ${worksInInput.doctorID} not found")}
                    it.hospital = hospitalService.getHospital(worksInInput.hospitalID).orElseThrow{NoUserFoundException("Hospital with ID ${worksInInput.hospitalID} not found")}
                    it.startDate =   convertStringToLocalDate(worksInInput.startDate)
                    it.endDate = convertStringToLocalDate(worksInInput.endDate)
                    it.salary = worksInInput.salary
                    it.stillWorks = worksInInput.endDate.let { date ->
                        when(date){
                            null -> true
                            else -> false
                        }
                    }
                    val startDate : LocalDate = convertStringToLocalDate(worksInInput.startDate) ?: throw InvalidInputException("startDate should not be null.")
                    val endDate : LocalDate? = convertStringToLocalDate(worksInInput.endDate)
                    endDate?.takeIf { date ->
                        date.isBefore(startDate) || date.isAfter(LocalDate.now())  }
                        ?.let {
                            throw InvalidInputException("Invalid endDate")
                        }

                    if(worksInRepository.conflictsWithOtherWorkDates(worksInInput.doctorID,startDate,endDate,1))
                        throw YourCustomEmailAlreadyExistsException("This date is conflicting with other working dates, check it again.", HttpStatus.CONFLICT)


                    // validate end DAte


                    worksInRepository.save(it)
                    WorksInDTO(
                        id= it.id,
                        doctor = DoctorDTO(
                            UserDTO(id = it.doctor!!.id, it.doctor!!.firstName,it.doctor!!.lastName,it.doctor!!.email,it.doctor!!.passw,it.doctor!!.gender,it.doctor!!.age,"Doctor"),
                            it.doctor!!.specialization,
                        ),
                        hospital = HospitalDTO(id = it.hospital!!.id, name = it.hospital!!.name ),
                        startDate = it.startDate.toString(),
                        endDate = it.endDate.toString(),
                        salary = it.salary!!,
                        stillWorks = it.stillWorks
                    )


                }

                return updatedWorksIn
            }
            else -> throw InvalidInputException("Invalid ID format. ID must be an integer.")
        }
    }
}
