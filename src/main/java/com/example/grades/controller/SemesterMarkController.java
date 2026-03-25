
package com.example.grades.controller;

import com.example.grades.dto.SemesterMarkDTO;
import com.example.grades.service.SemesterMarkService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students/{studentId}/marks")
@CrossOrigin(origins="http://localhost:5173")
public class SemesterMarkController {

    private final SemesterMarkService service;
    public SemesterMarkController(SemesterMarkService service){ this.service = service; }

    @PostMapping
    public SemesterMarkDTO upsert(@PathVariable Long studentId, @Valid @RequestBody SemesterMarkDTO dto){
        return service.upsert(studentId, dto);
    }

    @GetMapping
    public List<SemesterMarkDTO> list(@PathVariable Long studentId){
        return service.listByStudent(studentId);
    }

    @DeleteMapping("/{markId}")
    public void delete(@PathVariable Long studentId, @PathVariable Long markId){
        service.delete(markId);
    }
}