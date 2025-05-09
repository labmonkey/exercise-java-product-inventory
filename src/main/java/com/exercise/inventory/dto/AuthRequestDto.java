package com.exercise.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequestDto {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}