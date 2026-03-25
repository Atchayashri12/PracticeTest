
package com.example.grades.service;

import com.example.grades.dto.StudentDTO;

import java.util.List;

public interface StudentService {
    StudentDTO create(StudentDTO dto);
    List<StudentDTO> list();
    StudentDTO get(Long id);
    StudentDTO update(Long id, StudentDTO dto);
    void delete(Long id);
}
