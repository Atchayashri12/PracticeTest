package com.example.grades.impl;

import com.example.grades.dto.StudentDTO;
import com.example.grades.entity.Student;
import com.example.grades.exception.NotFoundException;
import com.example.grades.repository.StudentRepository;
import com.example.grades.service.StudentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository repo;

    public StudentServiceImpl(StudentRepository repo) {
        this.repo = repo;
    }

    private StudentDTO map(Student s) {
        return new StudentDTO(
                s.getId(),
                s.getFirstName(),
                s.getLastName(),
                s.getEmail(),
                s.getRollNumber(),
                s.getDepartment(),
                s.getYearOfStudy()
        );
    }

    private void apply(Student s, StudentDTO dto) {
        s.setFirstName(dto.firstName());
        s.setLastName(dto.lastName());
        s.setEmail(dto.email());
        s.setRollNumber(dto.rollNumber());
        s.setDepartment(dto.department());
        s.setYearOfStudy(dto.yearOfStudy());
    }

    @Override
    public StudentDTO create(StudentDTO dto) {
        // Proactive duplicate checks to avoid DB unique-key explosion (which leads to 500)
        if (repo.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("Email already exists: " + dto.email());
        }
        if (repo.existsByRollNumber(dto.rollNumber())) {
            throw new IllegalArgumentException("Roll number already exists: " + dto.rollNumber());
        }

        Student s = new Student();
        apply(s, dto);
        s = repo.save(s);
        return map(s);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDTO> list() {
        return repo.findAll().stream().map(this::map).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDTO get(Long id) {
        Student s = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Student not found: " + id));
        return map(s);
    }

    @Override
    public StudentDTO update(Long id, StudentDTO dto) {
        Student s = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Student not found: " + id));

        // If email/rollNumber changed, ensure uniqueness against other records
        if (!s.getEmail().equalsIgnoreCase(dto.email()) && repo.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("Email already exists: " + dto.email());
        }
        if (!s.getRollNumber().equalsIgnoreCase(dto.rollNumber()) && repo.existsByRollNumber(dto.rollNumber())) {
            throw new IllegalArgumentException("Roll number already exists: " + dto.rollNumber());
        }

        apply(s, dto);
        s = repo.save(s);
        return map(s);
    }

    @Override
    public void delete(Long id){
        if (!repo.existsById(id)) {
            throw new NotFoundException("Student not found: " + id);
        }
        repo.deleteById(id);
    }
}
