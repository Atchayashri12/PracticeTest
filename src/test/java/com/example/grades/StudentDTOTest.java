package com.example.grades;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.grades.dto.StudentDTO;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class StudentDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidStudentDTO() {
        StudentDTO student = new StudentDTO(
                1L,
                "Atchaya",
                "Sankar",
                "atchaya@example.com",
                "CS101",
                "Computer Science",
                3
        );

        Set<ConstraintViolation<StudentDTO>> violations = validator.validate(student);

        assertTrue(violations.isEmpty());
    }

   
    @Test
    void testBlankFirstName() {
        StudentDTO student = new StudentDTO(
                1L,
                "",
                "Sankar",
                "atchaya@example.com",
                "CS101",
                "Computer Science",
                3
        );

        Set<ConstraintViolation<StudentDTO>> violations = validator.validate(student);

        assertFalse(violations.isEmpty());
    }

  
    @Test
    void testInvalidEmail() {
        StudentDTO student = new StudentDTO(
                1L,
                "Atchaya",
                "Sankar",
                "invalid-email",
                "CS101",
                "Computer Science",
                3
        );

        Set<ConstraintViolation<StudentDTO>> violations = validator.validate(student);

        assertFalse(violations.isEmpty());
    }

    
    @Test
    void testInvalidYearOfStudy() {
        StudentDTO student = new StudentDTO(
                1L,
                "Atchaya",
                "Sankar",
                "atchaya@example.com",
                "CS101",
                "Computer Science",
                10
        );

        Set<ConstraintViolation<StudentDTO>> violations = validator.validate(student);

        assertFalse(violations.isEmpty());
    }
}