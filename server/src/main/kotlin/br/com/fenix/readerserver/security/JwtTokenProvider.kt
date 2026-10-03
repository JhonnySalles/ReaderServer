package br.com.fenix.readerserver.security

import br.com.fenix.readerserver.dto.auth.TokenDto
import br.com.fenix.readerserver.service.auth.UsuarioService
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import jakarta.annotation.PostConstruct
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Service
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.*

@Service
class JwtTokenProvider {

    @Value("\${security.jwt.token.secret-key:53cr37}")
    private var secretKey = "53cr37"

    @Value("\${security.jwt.token.expire-length:3600000}")
    private val validity: Long = 3600000 * 6 // 6h

    @Autowired
    private lateinit var service: UsuarioService

    private lateinit var algorithm: Algorithm

    @PostConstruct
    protected fun init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.toByteArray())
        algorithm = Algorithm.HMAC256(secretKey.toByteArray())
    }

    fun createAccessToken(username: String, roles: List<String>): TokenDto {
        val now = Date()
        val validityDate = Date(now.time + validity)
        val accessToken = getAccessToken(username, roles, now, validityDate)
        val refreshToken = getRefreshToken(username, roles, now)
        return TokenDto(username, true, now, validityDate, accessToken, refreshToken)
    }

    fun refreshToken(refreshToken: String): TokenDto {
        var token = refreshToken
        if (token.contains("Bearer ")) token = token.substring("Bearer ".length)
        val verifier: JWTVerifier = JWT.require(algorithm).build()
        val decodedJWT: DecodedJWT = verifier.verify(token)
        val username: String = decodedJWT.subject
        val roles: List<String> = decodedJWT.getClaim("roles").asList(String::class.java)
        return createAccessToken(username, roles)
    }

    private fun getAccessToken(username: String, roles: List<String>, now: Date, validity: Date): String {
        val issuerUrl = try {
            ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()
        } catch (e: Exception) {
            "http://localhost:8080"
        }
        return JWT.create()
            .withClaim("roles", roles)
            .withIssuedAt(now)
            .withExpiresAt(validity)
            .withSubject(username)
            .withIssuer(issuerUrl)
            .sign(algorithm)
            .trim()
    }

    private fun getRefreshToken(username: String, roles: List<String>, now: Date): String {
        val validityRefreshToken = Date(now.time + validity * 3)
        return JWT.create()
            .withClaim("roles", roles)
            .withIssuedAt(now)
            .withExpiresAt(validityRefreshToken)
            .withSubject(username)
            .sign(algorithm)
            .trim()
    }

    fun getAuthentication(token: String): Authentication {
        val decodedJWT: DecodedJWT = decodedToken(token)
        val username = decodedJWT.subject
        val roles: List<String> = decodedJWT.getClaim("roles").asList(String::class.java) ?: emptyList()
        val authorities = roles.map { SimpleGrantedAuthority(it) }
        val principal = User(username, "", authorities)
        return UsernamePasswordAuthenticationToken(principal, "", authorities)
    }

    private fun decodedToken(token: String): DecodedJWT {
        val verifier: JWTVerifier = JWT.require(algorithm).build()
        return verifier.verify(token)
    }

    fun resolveToken(req: HttpServletRequest): String? {
        val bearerToken = req.getHeader("Authorization")
        return if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring("Bearer ".length)
        } else null
    }

    fun validateToken(token: String): Boolean {
        return try {
            decodedToken(token)
            true
        } catch (e: Exception) {
            false
        }
    }
}
