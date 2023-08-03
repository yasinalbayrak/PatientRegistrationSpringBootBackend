package com.internshipproject.patientregistration

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class PatientRegistrationApplication

fun main(args: Array<String>) {
	runApplication<PatientRegistrationApplication>(*args)
}
