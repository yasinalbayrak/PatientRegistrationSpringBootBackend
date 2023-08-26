package com.internshipproject.patientregistration.dto._public

import com.fasterxml.jackson.annotation.JsonView
import com.internshipproject.patientregistration.entity.user.Gender
import com.internshipproject.patientregistration.view.Views

class UserDTOPublic(
    var firstname: String,

    var lastname: String,

    var email: String,

    var passw: String,

    var gender: Gender,
    var age: Int,

) {

}