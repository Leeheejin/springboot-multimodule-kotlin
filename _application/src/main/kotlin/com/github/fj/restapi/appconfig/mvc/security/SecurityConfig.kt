/*
 * springboot-multimodule-kotlin skeleton.
 * Under no licences and warranty.
 */
package com.github.fj.restapi.appconfig.mvc.security

import com.github.fj.lib.annotation.AllOpen
import com.github.fj.restapi.appconfig.mvc.security.internal.*
import com.github.fj.restapi.endpoint.ApiPaths
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import javax.inject.Inject

/**
 * @author Francesco Jo(nimbusob@gmail.com)
 * @since 27 - Oct - 2018
 */
@AllOpen
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfig @Inject constructor(
        private val successHandler: SavedRequestAwareAuthenticationSuccessHandler,
        private val failureHandler: AuthenticationFailureHandler
) : WebSecurityConfigurerAdapter() {

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.authenticationProvider(HttpAuthorizationTokenAuthenticationProvider(LOG))
                .userDetailsService(UserDetailsServiceImpl())
//
//        auth.inMemoryAuthentication()
//                .withUser("admin").password("adminPass").roles("ADMIN")
//                .and()
//                .withUser("user").password("userPass").roles("USER")
    }

    override fun configure(http: HttpSecurity) {
        http.addFilterBefore(HttpServletRequestAuthorizationHeaderFilter(LOG), BasicAuthenticationFilter::class.java)
                .cors().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling().authenticationEntryPoint(AuthenticationEntryPointImpl())
                .and()
                .authorizeRequests()
                // Restrict Spring actuator access except from localhost for security
                .antMatchers("/actuator/**").hasIpAddress("localhost")
                .antMatchers(HttpMethod.POST, ApiPaths.API_V1_ACCOUNT).permitAll()
                .antMatchers(HttpMethod.PATCH, ApiPaths.API_V1_ACCOUNT).permitAll()
                .antMatchers("${ApiPaths.API}/**").authenticated()
                .and()
                .formLogin()
                .successHandler(successHandler)
                .failureHandler(failureHandler)
                .and()
                .logout()
                .deleteCookies()
                .invalidateHttpSession(true)
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(SecurityConfig::class.java)
    }
}