package com.internshipproject.patientregistration.config

import com.internshipproject.patientregistration.repository.TokenRepository
import com.internshipproject.patientregistration.service.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import lombok.NonNull
import lombok.RequiredArgsConstructor
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter


@Component
@RequiredArgsConstructor
class JwtAuthenticationFilter(
    val jwtService: JwtService,
    val userDetailsService: UserDetailsService,
    val tokenRepository: TokenRepository
) : OncePerRequestFilter() {


    override fun doFilterInternal(
        @NonNull request: HttpServletRequest,
        @NonNull response: HttpServletResponse,
        @NonNull filterChain: FilterChain
    ) {
        val authHeader :String? = request.getHeader("Authorization")
        var jwt :String
        val userEmail :String?

        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response)
            return
        }

        //jwt= authHeader.substring(7)
        jwt = authHeader.split(" ")[1].trim();
        //logger.info("Token: $jwt")

        // extract the userEmail from JWT Token
        try {
            userEmail = jwtService.extractUsername(jwt)
        }
        catch (ex:Exception){
            sendErrorResponse(response, "Token is not valid", HttpServletResponse.SC_UNAUTHORIZED)
            return
        }

        if( userEmail != null && SecurityContextHolder.getContext().authentication == null){
            val userDetails:UserDetails = this.userDetailsService.loadUserByUsername(userEmail)

            var isTokenValid = tokenRepository.findByToken(jwt)
                .map {
                    !it.expired && !it.revoked
                }.orElse(false)

            if (jwtService.isTokenValid(jwt,userDetails) && isTokenValid) run {
                val authToken: UsernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.authorities

                )
                authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication=authToken
            }else {
                sendErrorResponse(response, "Token is not valid", HttpServletResponse.SC_UNAUTHORIZED)
                return
            }

        }

        filterChain.doFilter(request, response)




    }
    private fun sendErrorResponse(response: HttpServletResponse, errorMessage: String, status: Int) {
        response.contentType = "application/json"
        response.status = status
        response.writer.write("{\"error\": \"$errorMessage\"}")
        response.writer.flush()
    }

}