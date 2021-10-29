package com.example.demo

import com.okta.spring.boot.oauth.Okta
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@SpringBootApplication
class DemoApplication

fun main(args: Array<String>) {
	runApplication<DemoApplication>(*args)
}

@RestController
class MyController {
	@GetMapping("/public")
	fun public(): String {
		return "Hello, Anonymous!";
	}

	@GetMapping("/protected")
	fun protected(@AuthenticationPrincipal jwt: Jwt): String {
		return "Hello, ${jwt.subject}!"
	}
}

@EnableWebSecurity
class OAuth2ResourceServerSecurityConfiguration : WebSecurityConfigurerAdapter() {
	@Throws(Exception::class)
	override fun configure(http: HttpSecurity) {
		http.authorizeRequests() // allow anonymous access to the root page
			.antMatchers("/public").permitAll() // all other requests
			.anyRequest().authenticated()
			.and()
			.oauth2ResourceServer().jwt() // replace .jwt() with .opaqueToken() for Opaque Token case

		// Send a 401 message to the browser (w/o this, you'll see a blank page)
		Okta.configureResourceServer401ResponseBody(http)
	}
}