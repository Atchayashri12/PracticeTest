
package com.example.grades.service;

import com.example.grades.dto.SemesterMarkDTO;

import java.util.List;

public interface SemesterMarkService {
 SemesterMarkDTO upsert(Long studentId, SemesterMarkDTO dto);
 List<SemesterMarkDTO> listByStudent(Long studentId);
 void delete(Long markId);
}
