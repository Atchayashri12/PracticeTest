package com.example.grades.repository;

import com.example.grades.entity.SemesterMark;
import com.example.grades.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SemesterMarkRepository extends JpaRepository<SemesterMark, Long> {
    List<SemesterMark> findByStudentId(Long studentId);
    Optional<SemesterMark> findByStudentAndSemester(Student student, int semester);
}