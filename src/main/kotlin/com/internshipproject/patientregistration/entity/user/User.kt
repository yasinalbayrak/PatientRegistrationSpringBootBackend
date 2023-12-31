package com.internshipproject.patientregistration.entity.user

import com.internshipproject.patientregistration.dto._internal.UserStatus
import com.internshipproject.patientregistration.entity.auth.Token
import jakarta.persistence.*

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "user_type")
@Table(name = "_user")
abstract class User(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    open var id: Int = 0,
    open var firstName: String = "",
    open var lastName: String = "",
    @Column(unique = true)
    open var email: String = "",

    @Enumerated(EnumType.STRING)
    open var gender: Gender = Gender.MALE,

    open var age:Int = 0,

    open var passw: String = "",

    @Enumerated(EnumType.STRING)
    open var userStatus: UserStatus = UserStatus.INACTIVE,

    @OneToMany(mappedBy = "user")
    open val tokens: List<Token> = emptyList(),

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    open var roles: Set<Role> = emptySet()

) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return roles.map { SimpleGrantedAuthority(it.name) }
    }


    override fun getPassword(): String {
        return passw
    }

    override fun getUsername(): String {
        return email
    }

    override fun isAccountNonExpired(): Boolean {
        return true // Implement your logic for account expiration
    }

    override fun isAccountNonLocked(): Boolean {
        return true // Implement your logic for account locking
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true // Implement your logic for credentials expiration
    }

    override fun isEnabled(): Boolean {
        return true
    }

    override fun toString(): String {
        return "User(id=$id, firstName='$firstName', lastName='$lastName', email='$email', passw='$passw', roles=$roles)"
    }


}




