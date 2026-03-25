
package com.example.grades.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record SemesterMarkDTO(
    Long id,
    @Min(1) @Max(8) int semester,
    @DecimalMin("0.0") @DecimalMax("100.0") double assignment,
    @DecimalMin("0.0") @DecimalMax("100.0") double midterm,
    @DecimalMin("0.0") @DecimalMax("100.0") double finalExam,
    Double total,
    String letterGrade
) {}