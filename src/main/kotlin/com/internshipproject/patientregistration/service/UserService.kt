package com.internshipproject.patientregistration.service

import com.internshipproject.patientregistration.dto.MessageModel
import com.internshipproject.patientregistration.dto.MessageStatus
import com.internshipproject.patientregistration.dto.ResponseMessageModel
import com.internshipproject.patientregistration.dto._internal.DoctorDTO
import com.internshipproject.patientregistration.dto._internal.PatientDTO
import com.internshipproject.patientregistration.dto._internal.UserDTO
import com.internshipproject.patientregistration.entity.user.User
import com.internshipproject.patientregistration.exception.InvalidInputException
import com.internshipproject.patientregistration.exception.NoUserFoundException
import com.internshipproject.patientregistration.exception.YourCustomEmailAlreadyExistsException
import com.internshipproject.patientregistration.repository.jpa.UserRepository
import com.internshipproject.patientregistration.entity.user.types.Doctor
import com.internshipproject.patientregistration.repository.jpa.DoctorRepository
import com.internshipproject.patientregistration.entity.user.types.Patient
import com.internshipproject.patientregistration.repository.jpa.PatientRepository
import com.internshipproject.patientregistration.repository.mongo.ChatMessageRepository
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.Optional


@Service
class UserService (
    val userRepository: UserRepository,
    val doctorRepository: DoctorRepository,
    val patientRepository: PatientRepository,
    val roleService: RoleService,
    val passwordEncoder: PasswordEncoder,
    val chatMessageRepository: ChatMessageRepository
) {
    companion object {
        private val logger = KotlinLogging.logger {}
    }
    fun addDoctor(doctorDTO: DoctorDTO): DoctorDTO {

        if(userRepository.existsByEmail(doctorDTO.user.email))
            throw YourCustomEmailAlreadyExistsException("This email is already registered.",HttpStatus.CONFLICT)
        val doctorEntity : Doctor = doctorDTO.let {


            val role = roleService.addRole("Doctor")
            Doctor.builder()
                .specialization(it.specialization)
                .firstName(it.user.firstname)
                .lastName(it.user.lastname)
                .email(it.user.email)
                .passw(passwordEncoder.encode(it.user.passw))
                .age(it.user.age)
                .gender(it.user.gender)
                .roles(setOf(role))
                .build()
        }

        userRepository.save(doctorEntity)

        var result = doctorEntity.let {
            DoctorDTO(
                user = UserDTO(it.id,it.firstName,it.lastName,it.email,it.passw,it.gender,it.age,"Doctor",userStatus = it.userStatus),

                specialization = it.specialization
            )
        }
        return result

    }

    fun retrieveAllDoctors(): Collection<DoctorDTO> {
        return doctorRepository.findAll().map {
            DoctorDTO(
                UserDTO(
                    it.id,it.firstName,it.lastName,it.email,it.passw,it.gender,it.age,"Doctor",userStatus = it.userStatus
                ),
                it.specialization,

            )
        }
    }

    fun retrieveDoctor(id: Any): DoctorDTO {
        when (val idInt = id.toString().toIntOrNull()) {
            is Int -> {
                val doctorOptional = doctorRepository.findById(idInt)
                val doctor = doctorOptional.orElseThrow { NoUserFoundException("Doctor with ID $id not found") }

                return DoctorDTO(
                    UserDTO(
                        doctor.id, doctor.firstName, doctor.lastName, doctor.email, doctor.passw,doctor.gender,doctor.age, "Doctor",userStatus = doctor.userStatus
                    ),
                    doctor.specialization,

                )
            }
            else -> throw InvalidInputException("Invalid ID format. ID must be an integer.")
        }
    }

    fun deleteDoctor(id: Any): ResponseEntity<Any> {
        when (val idInt = id.toString().toIntOrNull()) {
            is Int -> {
                val doctorOptional = doctorRepository.findById(idInt)
                val doctor = doctorOptional.orElseThrow { NoUserFoundException("Doctor with ID $id not found") }

                doctorRepository.deleteById(idInt)

                return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
            }
            else -> throw InvalidInputException("Invalid ID format. ID must be an integer.")
        }
    }

    fun updateDoctor(doctorInput: DoctorDTO, id: Any): DoctorDTO {

        when (val idInt = id.toString().toIntOrNull()) {
            is Int -> {
                val doctorOptional = doctorRepository.findById(idInt)
                val doctor : Doctor = doctorOptional.orElseThrow { NoUserFoundException("Doctor with ID $id not found") }

                val updatedDoctor = doctor.let {
                    it.firstName = doctorInput.user.firstname
                    it.lastName = doctorInput.user.lastname
                    it.passw=  passwordEncoder.encode(doctorInput.user.passw)
                    it.age  = doctorInput.user.age
                    it.gender = doctorInput.user.gender
                    it.email = doctorInput.user.email

                    it.specialization= doctorInput.specialization
                    doctorRepository.save(it)
                    DoctorDTO(
                        user= UserDTO(it.id,it.firstName,it.lastName,it.email,it.passw,it.gender,it.age,"Doctor",userStatus = it.userStatus),
                        specialization = it.specialization,

                    )
                }

                return updatedDoctor
            }
            else -> throw InvalidInputException("Invalid ID format. ID must be an integer.")
        }

    }

    fun getDoctor(doctorId:Int) :Optional<Doctor> = doctorRepository.findById(doctorId)

    fun getUser(userID:Int) :Optional<User> = userRepository.findById(userID)
    /*
        ***************  PATIENT ****************
     */
    fun addPatient(patientDTO: PatientDTO): PatientDTO {

        if(userRepository.existsByEmail(patientDTO.email))
            throw YourCustomEmailAlreadyExistsException("This email is already registered.",HttpStatus.CONFLICT)
        val patientEntity : Patient = patientDTO.let {


            val role = roleService.addRole("Patient")
            Patient.builder()
                .firstName(it.firstname)
                .lastName(it.lastname)
                .email(it.email)
                .passw(passwordEncoder.encode(it.passw))
                .roles(setOf(role))
                .age(it.age)
                .gender(it.gender)
                .build()
        }

        userRepository.save(patientEntity)

        var result = patientEntity.let {

              PatientDTO(it.id,it.firstName,it.lastName,it.email,it.passw,it.gender,it.age,"Patient",userStatus = it.userStatus)


        }
        return result
    }

    fun retrieveAllPatients(): Collection<PatientDTO> {
        return patientRepository.findAll().map {
                PatientDTO(
                    it.id,it.firstName,it.lastName,it.email,it.passw,it.gender,it.age,"Patient",userStatus = it.userStatus
                )
        }
    }

    fun retrievePatient(id: Any): PatientDTO {
        when (val idInt = id.toString().toIntOrNull()) {
            is Int -> {
                val patientOptional = patientRepository.findById(idInt)
                val patient = patientOptional.orElseThrow { NoUserFoundException("Patient with ID $id not found") }

                return PatientDTO(
                        patient.id, patient.firstName, patient.lastName, patient.email, patient.passw, patient.gender,patient.age,"Patient",userStatus = patient.userStatus
                    )
            }
            else -> throw InvalidInputException("Invalid ID format. ID must be an integer.")
        }
    }

    fun deletePatient(id: Any): ResponseEntity<Any> {
        when (val idInt = id.toString().toIntOrNull()) {
            is Int -> {
                val patientOptional = patientRepository.findById(idInt)
                val patient = patientOptional.orElseThrow { NoUserFoundException("Patient with ID $id not found") }

                patientRepository.deleteById(idInt)

                return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
            }
            else -> throw InvalidInputException("Invalid ID format. ID must be an integer.")
        }
    }

    fun updatePatient(patientInput: PatientDTO, id: Any): Any? {
        when (val idInt = id.toString().toIntOrNull()) {
            is Int -> {
                val patientOptional = patientRepository.findById(idInt)
                val patient : Patient = patientOptional.orElseThrow { NoUserFoundException("Patient with ID $id not found") }

                val updatedPatient = patient.let {
                    it.firstName = patientInput.firstname
                    it.lastName = patientInput.lastname
                    //it.passw=  passwordEncoder.encode(patientInput.passw)
                    it.email = patientInput.email
                    it.age= patientInput.age
                    it.gender=patientInput.gender
                    patientRepository.save(it)
                    PatientDTO(it.id,it.firstName,it.lastName,it.email,it.passw,it.gender,it.age,"Patient",userStatus = it.userStatus)


                }

                return updatedPatient
            }
            else -> throw InvalidInputException("Invalid ID format. ID must be an integer.")
        }
    }
    fun getPatient(id : Int) = patientRepository.findById(id)
    fun retrieveAllUsers(userId: Int): Collection<UserDTO> {


        return userRepository.findAll().map {
            var lastMessage : ResponseMessageModel? = null

            if (it.id != userId) {
                val messages = chatMessageRepository.findMessagesBetweenUsers(userId.toString(), it.id.toString())

                if (messages.isNotEmpty()) {

                    val lastMessageInConversation = messages.lastOrNull()

                    if (lastMessageInConversation != null) {


                        lastMessage = ResponseMessageModel(
                            lastMessageInConversation.message ?: "",
                            lastMessageInConversation.sender ?: "",
                            lastMessageInConversation.recipient ?: "",
                            MessageStatus.MESSAGE,
                            photoData = lastMessageInConversation.photoData
                        )
                    }
                }
            }

            UserDTO(
                it.id,it.firstName,it.lastName,it.email,it.passw,it.gender,it.age,it.roles.map { role->
                    role.name
                }[0],
                lastMessage,
                userStatus = it.userStatus
            )
        }
    }


}