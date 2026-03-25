
package com.example.grades.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.Instant;

@Entity
@Table(name = "semester_marks",
    uniqueConstraints = @UniqueConstraint(name="uk_student_sem", columnNames = {"student_id","semester"}))
public class SemesterMark {

 @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @ManyToOne(optional=false, fetch=FetchType.LAZY)
 @JoinColumn(name="student_id")
 private Student student;

 @Min(1) @Max(8)
 private int semester;

 @DecimalMin("0.0") @DecimalMax("100.0")
 private double assignment;

 @DecimalMin("0.0") @DecimalMax("100.0")
 private double midterm;

 @DecimalMin("0.0") @DecimalMax("100.0")
 private double finalExam;

 private double total;        
 private String letterGrade;  

 private Instant updatedAt = Instant.now();

 public Long getId() {
	return id;
 }

 public void setId(Long id) {
	this.id = id;
 }

 public Student getStudent() {
	return student;
 }

 public void setStudent(Student student) {
	this.student = student;
 }

 public int getSemester() {
	return semester;
 }

 public void setSemester(int semester) {
	this.semester = semester;
 }

 public double getAssignment() {
	return assignment;
 }

 public void setAssignment(double assignment) {
	this.assignment = assignment;
 }

 public double getMidterm() {
	return midterm;
 }

 public void setMidterm(double midterm) {
	this.midterm = midterm;
 }

 public double getFinalExam() {
	return finalExam;
 }

 public void setFinalExam(double finalExam) {
	this.finalExam = finalExam;
 }

 public double getTotal() {
	return total;
 }

 public void setTotal(double total) {
	this.total = total;
 }

 public String getLetterGrade() {
	return letterGrade;
 }

 public void setLetterGrade(String letterGrade) {
	this.letterGrade = letterGrade;
 }

 public Instant getUpdatedAt() {
	return updatedAt;
 }

 public void setUpdatedAt(Instant updatedAt) {
	this.updatedAt = updatedAt;
 }
 
 		

 	
}