package com.grup14.luterano.auth.application;


import com.grup14.luterano.auth.infrastructure.AuthenticateException;
import com.grup14.luterano.auth.infrastructure.AuthenticationRequest;
import com.grup14.luterano.auth.infrastructure.AuthenticationResponse;
import com.grup14.luterano.auth.infrastructure.RegisterRequest;

public interface AuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest request) throws AuthenticateException;

    AuthenticationResponse register(RegisterRequest registerRequest) throws AuthenticateException;
}
