package com.internshipproject.patientregistration.config.security

import com.internshipproject.patientregistration.exception.securityExceptions.CustomAccessDeniedHandler
import com.internshipproject.patientregistration.exception.securityExceptions.CustomAuthenticationEntryPoint
import com.internshipproject.patientregistration.service.LogoutService
import lombok.RequiredArgsConstructor
import mu.KLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
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
        customAccessDeniedHandler: CustomAccessDeniedHandler,
        logoutService: LogoutService
    ): SecurityFilterChain{


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
                        //.requestMatchers("/api/v1/user/**").authenticated()
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

             .logout {
                 it
                     .logoutUrl("/api/v1/auth/logout")
                     .addLogoutHandler(logoutService)
                     .logoutSuccessHandler { request, response, authentication ->
                         SecurityContextHolder.clearContext()

                     }
             }



        return http.build()
    }
    private fun corsConfigurationSource(): UrlBasedCorsConfigurationSource {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.addAllowedOrigin("http://localhost:3000")
        config.addAllowedOrigin("http://192.168.56.1:3000")
        config.addAllowedOriginPattern("*")
        config.addAllowedMethod("*")
        config.addAllowedHeader("*")

        config.allowCredentials = true
        source.registerCorsConfiguration("/**", config)
        return source
    }

}