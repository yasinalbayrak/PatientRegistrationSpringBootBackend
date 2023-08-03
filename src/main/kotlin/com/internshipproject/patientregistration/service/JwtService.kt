package com.internshipproject.patientregistration.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.security.Key
import java.util.*
import java.util.function.Function
import kotlin.collections.HashMap


@Service
class JwtService {
    private val SECRET_KEY :String = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"

    fun extractUsername(token: String): String? {
        return extractClaim(token,Claims::getSubject)
    }

    fun <T> extractClaim(token: String, claimsResolver: Function<Claims, T>): T {
        val claims: Claims = extractAllClaims(token)
        return claimsResolver.apply(claims)

    }

    fun generateToken(
        userDetails: UserDetails
    ):String{
        return generateToken(HashMap(),userDetails)
    }

    fun generateToken(
        extraClaims: Map<String,Any>,
        userDetails: UserDetails
    ):String{
        return Jwts
            .builder()
            .setClaims(extraClaims)
            .setSubject(userDetails.username)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis()+
                    1000    // 1 second
                    *60     // 1 minute
                    *60     // 1 hour
            ))
            .signWith(getSignInKey(),SignatureAlgorithm.HS256)
            .compact();
    }

    fun isTokenValid(token :String,userDetails: UserDetails): Boolean {
        val username :String = extractUsername(token)!!
        return (username == userDetails.username && !isTokenExpired(token))


    }

    private fun isTokenExpired(token: String): Boolean {
        return  extractExpiration(token).before(Date())
    }

    private fun extractExpiration(token: String): Date {
        return extractClaim(token,Claims::getExpiration)

    }

    private fun  extractAllClaims(token: String): Claims{
        return Jwts
            .parserBuilder()
            .setSigningKey(getSignInKey())
            .build()
            .parseClaimsJws(token)
            .body
    }

    private fun getSignInKey(): Key {
        val keyBytes : ByteArray? = Decoders.BASE64.decode(SECRET_KEY)
        return Keys.hmacShaKeyFor(keyBytes)
    }

}
