package fr.avenirsesr.portfolio.security.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RolesController {

	@GetMapping("/roles")
	public String getRoles(@RequestHeader(value="Authorization") String authHeader) {
		
		return  "getRoles: " + authHeader;
	}
	
}
