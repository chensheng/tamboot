package com.tamboot.security.core;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordEncoderFactory {
    private final PasswordEncoder defaultPasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private ObjectProvider<PasswordEncoder> passwordEncoders;

    public PasswordEncoderFactory(ObjectProvider<PasswordEncoder> passwordEncoders) {
        this.passwordEncoders = passwordEncoders;
    }

    public PasswordEncoder get() {
        PasswordEncoder encoder = passwordEncoders.getIfAvailable();
        if (encoder != null) {
            return encoder;
        }

        return defaultPasswordEncoder;
    }
}
