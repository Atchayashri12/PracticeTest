package com.example.grades;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;

import com.example.grades.dto.SemesterMarkDTO;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SemesterMarkDTOTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void initValidator() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void closeFactory() {
        factory.close();
    }

    
    private SemesterMarkDTO validDto() {
        return new SemesterMarkDTO(
                1L,          
                3,           
                25.0,        
                40.0,        
                80.0,       
                145.0,       
                "A"          
        );
    }

    
    @Test
    void valid() {
        SemesterMarkDTO dto = validDto();

        Set<ConstraintViolation<SemesterMarkDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Expected no validation errors for a valid DTO");
    }

    
    @Test
    void semesterAtLowerBound_isValid() {
        SemesterMarkDTO dto = new SemesterMarkDTO(1L, 1, 10.0, 20.0, 30.0, null, null);

        Set<ConstraintViolation<SemesterMarkDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void semesterAtUpperBound_isValid() {
        SemesterMarkDTO dto = new SemesterMarkDTO(1L, 8, 10.0, 20.0, 30.0, null, null);

        Set<ConstraintViolation<SemesterMarkDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    
    @Test
    void semesterBelowMin_isInvalid() {
        SemesterMarkDTO dto = new SemesterMarkDTO(1L, 0, 10.0, 20.0, 30.0, null, null);

        Set<ConstraintViolation<SemesterMarkDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void semesterAboveMax_isInvalid() {
        SemesterMarkDTO dto = new SemesterMarkDTO(1L, 9, 10.0, 20.0, 30.0, null, null);

        Set<ConstraintViolation<SemesterMarkDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

   
    @Test
    void marksAtLowerBound_areValid() {
        SemesterMarkDTO dto = new SemesterMarkDTO(1L, 2, 0.0, 0.0, 0.0, null, "F");

        Set<ConstraintViolation<SemesterMarkDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void marksAtUpperBound_areValid() {
        SemesterMarkDTO dto = new SemesterMarkDTO(1L, 2, 100.0, 100.0, 100.0, 300.0, "A");

        Set<ConstraintViolation<SemesterMarkDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    
    @Test
    void marksBelowMin_areInvalid() {
        SemesterMarkDTO dto = new SemesterMarkDTO(1L, 2, -0.1, 50.0, 60.0, null, null);

        Set<ConstraintViolation<SemesterMarkDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void marksAboveMax_areInvalid() {
        SemesterMarkDTO dto = new SemesterMarkDTO(1L, 2, 50.0, 100.1, 60.0, null, null);

        Set<ConstraintViolation<SemesterMarkDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

   
    @Test
    void marksAsNaN_areInvalid() {
        SemesterMarkDTO dto = new SemesterMarkDTO(1L, 2, Double.NaN, 50.0, 60.0, null, null);

        Set<ConstraintViolation<SemesterMarkDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "NaN should violate @DecimalMin/@DecimalMax");
    }

    @Test
    void marksAsInfinity_areInvalid() {
        SemesterMarkDTO dto = new SemesterMarkDTO(1L, 2, Double.POSITIVE_INFINITY, 50.0, 60.0, null, null);

        Set<ConstraintViolation<SemesterMarkDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "Infinity should violate @DecimalMax");
    }

    
    @Test
    void nullTotalAndLetterGrade_areAllowed() {
        SemesterMarkDTO dto = new SemesterMarkDTO(1L, 4, 10.0, 20.0, 30.0, null, null);

        Set<ConstraintViolation<SemesterMarkDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }
}