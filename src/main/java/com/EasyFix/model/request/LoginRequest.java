package com.EasyFix.model.request;

public record LoginRequest(
        String email,
        String password
) {
}
