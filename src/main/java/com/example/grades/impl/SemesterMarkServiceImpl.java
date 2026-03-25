
package com.example.grades.impl;

import com.example.grades.dto.SemesterMarkDTO;
import com.example.grades.entity.SemesterMark;
import com.example.grades.entity.Student;
import com.example.grades.exception.NotFoundException;
import com.example.grades.repository.SemesterMarkRepository;
import com.example.grades.repository.StudentRepository;
import com.example.grades.service.SemesterMarkService;
import com.example.grades.util.GradeCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service @Transactional
public class SemesterMarkServiceImpl implements SemesterMarkService {

 private final SemesterMarkRepository repo;
 private final StudentRepository studentRepo;

 public SemesterMarkServiceImpl(SemesterMarkRepository repo, StudentRepository studentRepo) {
     this.repo = repo; this.studentRepo = studentRepo;
 }

 private SemesterMarkDTO map(SemesterMark m) {
     return new SemesterMarkDTO(m.getId(), m.getSemester(), m.getAssignment(),
             m.getMidterm(), m.getFinalExam(), m.getTotal(), m.getLetterGrade());
 }

 @Override
 public SemesterMarkDTO upsert(Long studentId, SemesterMarkDTO dto) {
     Student student = studentRepo.findById(studentId)
             .orElseThrow();

     SemesterMark mark = repo.findByStudentAndSemester(student, dto.semester())
             .orElseGet(() -> {
                 SemesterMark m = new SemesterMark();
                 m.setStudent(student);
                 m.setSemester(dto.semester());
                 return m;
             });

     mark.setAssignment(dto.assignment());
     mark.setMidterm(dto.midterm());
     mark.setFinalExam(dto.finalExam());

     double total = GradeCalculator.total(dto.assignment(), dto.midterm(), dto.finalExam());
     mark.setTotal(Math.round(total * 100.0) / 100.0);
     mark.setLetterGrade(GradeCalculator.letter(total));
     mark.setUpdatedAt(Instant.now());

     return map(repo.save(mark));
 }

 @Override
 @Transactional(readOnly = true)
 public List<SemesterMarkDTO> listByStudent(Long studentId) {
     if (!studentRepo.existsById(studentId))
         throw new NotFoundException("Student not found: " + studentId);
     return repo.findByStudentId(studentId).stream().map(this::map).toList();
 }

 @Override
 public void delete(Long markId) {
     if (!repo.existsById(markId)) throw new NotFoundException("Mark not found: " + markId);
     repo.deleteById(markId);
 }
}