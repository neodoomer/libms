package com.maids.libms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor
public class AuthRequest {
    @Email
    @NotBlank
    private String email;
    
    @NotBlank
    private String password;
} 