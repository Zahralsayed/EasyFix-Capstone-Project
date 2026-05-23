package com.EasyFix.enums;

public enum UserStatus {
    PENDING_VERIFICATION, // Account created, but email verification link hasn't been clicked
    PENDING_APPROVAL,     // Email is verified, profile details are saved, waiting for Admin review
    ACTIVE, // Account is live and visible to customers on EasyFix
    INACTIVE, // Account is soft-deleted or suspended by an Admin
    BLOCKED
}
