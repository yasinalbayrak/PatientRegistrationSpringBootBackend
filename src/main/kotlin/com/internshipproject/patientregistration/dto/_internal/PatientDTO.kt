package com.internshipproject.patientregistration.dto._internal

import com.fasterxml.jackson.annotation.JsonView
import com.internshipproject.patientregistration.view.Views

class PatientDTO(
    @JsonView(Views.Internal::class)
    var id: Int? = null,

    @JsonView(Views.Public::class)
    var firstname: String,

    @JsonView(Views.Public::class)
    var lastname: String,

    @JsonView(Views.Public::class)
    var email: String,

    @JsonView(Views.Public::class)
    var passw: String,

    @JsonView(Views.Internal::class)
    var role: String? = null
) {


}