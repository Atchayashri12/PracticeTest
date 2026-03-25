package com.example.grades.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StudentDTO(
    Long id,
    @NotBlank @Size(max = 50) String firstName,
    @NotBlank @Size(max = 50) String lastName,
    @NotBlank @Email @Size(max = 120) String email,
    @NotBlank @Size(max = 20) String rollNumber,
    @NotBlank @Size(max = 50) String department,
    @Min(1) @Max(8) int yearOfStudy
) {}
