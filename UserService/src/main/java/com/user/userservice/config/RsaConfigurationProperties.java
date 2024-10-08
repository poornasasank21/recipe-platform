package com.user.userservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@ConfigurationProperties(prefix = "rsa")
public record RsaConfigurationProperties(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
}
