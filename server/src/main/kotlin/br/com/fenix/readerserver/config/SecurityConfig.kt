package br.com.fenix.readerserver.config

import br.com.fenix.readerserver.exceptions.FilterExceptionHandler
import br.com.fenix.readerserver.security.CustomAuthenticationEntryPoint
import br.com.fenix.readerserver.security.JwtTokenFilter
import br.com.fenix.readerserver.service.auth.UsuarioService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@EnableWebSecurity
@Configuration
class SecurityConfig(
    private var userDetailsService: UsuarioService,
    private val tokenFilter: JwtTokenFilter,
    private val customEntryPoint: CustomAuthenticationEntryPoint,
    private val exceptionHandlerFilter: FilterExceptionHandler
) {

    companion object {
        fun getPasswordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
        fun encodePassword(password: String): String = getPasswordEncoder().encode(password)
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = getPasswordEncoder()

    @Bean
    fun authenticationProvider(): AuthenticationProvider {
        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(userDetailsService)
        authProvider.setPasswordEncoder(passwordEncoder())
        return authProvider
    }

    @Bean
    @Throws(Exception::class)
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
        return config.authenticationManager
    }

    @Bean
    @Order(1)
    @Throws(Exception::class)
    fun publicApiFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .securityMatcher(
                "/error",
                "/auth/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/v3/api-docs/**",
                "/docs/**",
                "/documentacao/**",
                "/webjars/**",
                "/health/**"
            )
            .authorizeHttpRequests { authorize ->
                authorize.anyRequest().permitAll()
            }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .csrf { it.disable() }
            .cors { }
            .httpBasic { it.disable() }

        return http.build()
    }

    @Bean
    @Order(2)
    @Throws(Exception::class)
    fun privateApiFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .securityMatcher(
                "/api/**",
                "/users"
            )
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers("/api/**").authenticated()
                    .requestMatchers("/users").denyAll()
            }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .csrf { it.disable() }
            .cors { }
            .httpBasic { it.disable() }
            .addFilterBefore(exceptionHandlerFilter, UsernamePasswordAuthenticationFilter::class.java)
            .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter::class.java)
            .exceptionHandling {
                it.authenticationEntryPoint(customEntryPoint)
            }

        return http.build()
    }
}
