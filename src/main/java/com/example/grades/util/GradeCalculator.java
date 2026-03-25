
package com.example.grades.util;

public final class GradeCalculator {
    private GradeCalculator(){}

    
    public static final double W_ASSIGNMENT = 0.30;
    public static final double W_MIDTERM    = 0.30;
    public static final double W_FINAL      = 0.40;

    public static double total(double assignment, double midterm, double finalExam) {
        return assignment * W_ASSIGNMENT + midterm * W_MIDTERM + finalExam * W_FINAL;
    }

    public static String letter(double total) {
        if (total >= 90) return "A";
        if (total >= 80) return "B";
        if (total >= 70) return "C";
        if (total >= 60) return "D";
        return "F";
    }
}