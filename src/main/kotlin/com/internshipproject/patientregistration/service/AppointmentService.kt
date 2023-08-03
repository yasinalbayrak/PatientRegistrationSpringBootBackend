package com.internshipproject.patientregistration.service

import com.internshipproject.patientregistration.dto._internal.AppointmentDTO
import com.internshipproject.patientregistration.dto._internal.DoctorDTO
import com.internshipproject.patientregistration.dto._internal.PatientDTO
import com.internshipproject.patientregistration.dto._internal.UserDTO
import com.internshipproject.patientregistration.dto._public.AppointmentDTOPublic
import com.internshipproject.patientregistration.entity.appointment.Appointment
import com.internshipproject.patientregistration.entity.appointment.AppointmentRepository
import com.internshipproject.patientregistration.entity.appointment.AppointmentStatus
import com.internshipproject.patientregistration.entity.user.types.Doctor
import com.internshipproject.patientregistration.entity.user.types.DoctorRepository
import com.internshipproject.patientregistration.entity.user.types.Patient
import com.internshipproject.patientregistration.exception.InvalidInputException
import com.internshipproject.patientregistration.exception.NoUserFoundException
import com.internshipproject.patientregistration.exception.YourCustomEmailAlreadyExistsException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


@Service
class AppointmentService (
    val appointmentRepository: AppointmentRepository,
    val userService: UserService,
    val doctorRepository: DoctorRepository
) {
    fun convertStringToLocalDateTime(dateTimeString: String): LocalDateTime {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return LocalDateTime.parse(dateTimeString, formatter)
    }

    fun addAppointment(appointmentInput: AppointmentDTOPublic): AppointmentDTO {

        if(appointmentRepository.existsAppointmentWithDoctorPatientAndDate(appointmentInput.doctorID!!, appointmentInput.patientID!!,convertStringToLocalDateTime(appointmentInput.date!!)))
            throw YourCustomEmailAlreadyExistsException("This appointment date is already booked by someone else.", HttpStatus.CONFLICT)


        val optionalDoctor =  userService.getDoctor(doctorId = appointmentInput.doctorID!!)
        val doctor : Doctor = optionalDoctor.orElseThrow { NoUserFoundException("Doctor with ID ${appointmentInput.doctorID} not found") }

        val optionalPatient =  userService.getPatient(id = appointmentInput.patientID!!)
        val patient : Patient = optionalPatient.orElseThrow { NoUserFoundException("Patient with ID ${appointmentInput.patientID} not found") }


        val appointmentEntity : Appointment = appointmentInput.let {
            Appointment.builder()
                .doctor(doctor)
                .patient(patient)
                .date(convertStringToLocalDateTime(it.date!!))
                .build()
        }

        appointmentRepository.save(appointmentEntity)

        var result = appointmentEntity.let {
            AppointmentDTO(
                id = it.id,
                doctor = DoctorDTO(
                    UserDTO(id = it.doctor!!.id, it.doctor!!.firstName,it.doctor!!.lastName,it.doctor!!.email,it.doctor!!.passw,"Doctor"),
                    it.doctor!!.specialization,
                    it.doctor!!.salary),

                patient = PatientDTO(id = it.patient!!.id, it.patient!!.firstName,it.patient!!.lastName,it.patient!!.email,it.patient!!.passw,"Patient"),
                date = it.date.toString()
            )
        }
        return result
    }

    fun retrieveAllAppointments(): Collection<AppointmentDTO> {
        return appointmentRepository.findAll().map {
            AppointmentDTO(
                id= it.id,
                doctor = DoctorDTO(
                    UserDTO(id = it.doctor!!.id, it.doctor!!.firstName,it.doctor!!.lastName,it.doctor!!.email,it.doctor!!.passw,"Doctor"),
                    it.doctor!!.specialization,
                    it.doctor!!.salary),
                patient = PatientDTO(id = it.patient!!.id, it.patient!!.firstName,it.patient!!.lastName,it.patient!!.email,it.patient!!.passw,"Patient"),
                date = it.date.toString()
            )
        }
    }

    fun retrieveAppointment(id : Any): AppointmentDTO {
        when (val idInt = id.toString().toIntOrNull()) {
            is Int -> {
                val appointmentOptional = appointmentRepository.findById(idInt)
                val appointment = appointmentOptional.orElseThrow { NoUserFoundException("Appointment with ID $id not found") }

                return AppointmentDTO(
                    id= appointment.id,
                    doctor = DoctorDTO(
                        UserDTO(id = appointment.doctor!!.id, appointment.doctor!!.firstName,appointment.doctor!!.lastName,appointment.doctor!!.email,appointment.doctor!!.passw,"Doctor"),
                        appointment.doctor!!.specialization,
                        appointment.doctor!!.salary),
                    patient = PatientDTO(id = appointment.patient!!.id, appointment.patient!!.firstName,appointment.patient!!.lastName,appointment.patient!!.email,appointment.patient!!.passw,"Patient"),
                    date = appointment.date.toString()
                )
            }
            else -> throw InvalidInputException("Invalid ID format. ID must be an integer.")
        }
    }

    fun deleteAppointment(id: Any) : ResponseEntity<Any> {
        when (val idInt = id.toString().toIntOrNull()) {
            is Int -> {
                val appintmentOptional = appointmentRepository.findById(idInt)
                val appointment = appintmentOptional.orElseThrow { NoUserFoundException("Appointment with ID $id not found") }

                appointmentRepository.deleteById(idInt)

                return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
            }
            else -> throw InvalidInputException("Invalid ID format. ID must be an integer.")
        }
    }

    fun updateAppointment(appointmentInput: AppointmentDTOPublic, id: Any): AppointmentDTO {
        when (val idInt = id.toString().toIntOrNull()) {
            is Int -> {
                val appointmentOptional = appointmentRepository.findById(idInt)
                val appointment = appointmentOptional.orElseThrow { NoUserFoundException("Appointment with ID $id not found") }

                val updatedAppointment = appointment.let {
                    it.doctor = userService.getDoctor(appointmentInput.doctorID!!).orElseThrow{NoUserFoundException("Doctor with ID ${appointmentInput.doctorID!!} not found")}
                    it.patient = userService.getPatient(appointmentInput.patientID!!).orElseThrow{NoUserFoundException("Patient with ID ${appointmentInput.patientID!!} not found")}
                    it.date =   convertStringToLocalDateTime(appointmentInput.date!!)
                    if(appointmentRepository.existsAppointmentWithDoctorPatientAndDate(appointmentInput.doctorID!!, appointmentInput.patientID!!,convertStringToLocalDateTime(appointmentInput.date!!)))
                        throw YourCustomEmailAlreadyExistsException("This appointment date is already booked by someone else.", HttpStatus.CONFLICT)
                    appointmentRepository.save(it)
                    AppointmentDTO(
                        id= appointment.id,
                        doctor = DoctorDTO(
                            UserDTO(id = appointment.doctor!!.id, appointment.doctor!!.firstName,appointment.doctor!!.lastName,appointment.doctor!!.email,appointment.doctor!!.passw,"Doctor"),
                            appointment.doctor!!.specialization,
                            appointment.doctor!!.salary),
                        patient = PatientDTO(id = appointment.patient!!.id, appointment.patient!!.firstName,appointment.patient!!.lastName,appointment.patient!!.email,appointment.patient!!.passw,"Patient"),
                        date = appointment.date.toString()
                    )


                }

                return updatedAppointment
            }
            else -> throw InvalidInputException("Invalid ID format. ID must be an integer.")
        }
    }


    @Scheduled(cron = "0 0 * * * *") // Run at the beginning of every hour
    fun updateAppointmentStatus() {
        val currentTime = LocalDateTime.now()
        val appointments = appointmentRepository.findAll()

        appointments.forEach { appointment ->
            if (appointment.date?.isBefore(currentTime) == true) {
                appointment.status = AppointmentStatus.PASSED
                appointmentRepository.save(appointment)
            }
        }
    }


}