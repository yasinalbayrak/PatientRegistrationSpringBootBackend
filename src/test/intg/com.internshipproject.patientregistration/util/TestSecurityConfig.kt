package com.internshipproject.patientregistration.util

import com.internshipproject.patientregistration.config.security.JwtAuthenticationFilter
import com.internshipproject.patientregistration.exception.securityExceptions.CustomAccessDeniedHandler
import com.internshipproject.patientregistration.exception.securityExceptions.CustomAuthenticationEntryPoint
import lombok.RequiredArgsConstructor
import mu.KLogging
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@TestConfiguration
@EnableWebSecurity
@RequiredArgsConstructor
class SecurityConfig {

    companion object : KLogging()

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        jwtAuthFilter: JwtAuthenticationFilter,
        authenticationProvider: AuthenticationProvider,
        customAuthenticationEntryPoint: CustomAuthenticationEntryPoint,
        customAccessDeniedHandler: CustomAccessDeniedHandler
    ): SecurityFilterChain {


        http
            .csrf {
                it.disable()
            }
            .cors {
                it.configurationSource(corsConfigurationSource())
            }
            .authorizeHttpRequests {
                it
                    .requestMatchers("/api/v1/auth/**").permitAll()

                    .anyRequest().permitAll()

            }
            .sessionManagement {
                it
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .exceptionHandling {
                it.authenticationEntryPoint(customAuthenticationEntryPoint)
                it.accessDeniedHandler(customAccessDeniedHandler)

            }
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)


        return http.build()
    }
    private fun corsConfigurationSource(): UrlBasedCorsConfigurationSource {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.addAllowedOrigin("http://localhost:3000") // Change this to your frontend app's origin
        config.addAllowedOrigin("http://192.168.56.1:3000")
        config.addAllowedOriginPattern("*")
        config.addAllowedMethod("*")
        config.addAllowedHeader("*")

        config.allowCredentials = true
        source.registerCorsConfiguration("/**", config)
        return source
    }
}