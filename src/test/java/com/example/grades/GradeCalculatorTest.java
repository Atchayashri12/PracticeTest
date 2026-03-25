package com.example.grades;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.example.grades.util.GradeCalculator;

class GradeCalculatorTest {

    @Test
    void testTotalCalculation() {
        double total = GradeCalculator.total(80, 70, 90);
        
        assertEquals(81.0, total, 0.001);
    }

    @Test
    void testLetterGradeA() {
        assertEquals("A", GradeCalculator.letter(92));
    }

    @Test
    void testLetterGradeB() {
        assertEquals("B", GradeCalculator.letter(85));
    }

    @Test
    void testLetterGradeC() {
        assertEquals("C", GradeCalculator.letter(75));
    }

    @Test
    void testLetterGradeD() {
        assertEquals("D", GradeCalculator.letter(65));
    }

    @Test
    void testLetterGradeF() {
        assertEquals("F", GradeCalculator.letter(50));
    }
}