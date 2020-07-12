package com.java.zoo.web.controller;


import com.java.zoo.dto.JwtRequest;
import com.java.zoo.exception.BadRequestAlertException;
import com.java.zoo.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing authenticating the User
 */
@RestController
@CrossOrigin
public class JwtAuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    /**
     * {@code POST  /JwtRequest} : Authorize the user.
     *
     * @param authenticationRequest UserName and Password of the user
     * @return JwtToken if authenticated
     */
    @PostMapping(value = "/authenticate")
    public ResponseEntity<String> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername().toLowerCase(), authenticationRequest.getPassword()));
            return ResponseEntity.ok(jwtTokenUtil.createToken(authenticationRequest.getUsername().toLowerCase()));
        } catch (AuthenticationException e) {
            throw new BadRequestAlertException("Authentication Denied", "zoo application", "Invalid Username/Password");
        }
    }

}
