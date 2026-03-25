package com.example.grades;

import com.example.grades.dto.StudentDTO;
import com.example.grades.exception.NotFoundException;
import com.example.grades.service.StudentService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StudentServiceTest {

    @Autowired
    private StudentService studentService;

    @Test
    @Order(1)
    @Transactional
    void create_and_list() {
        
        StudentDTO toCreate = new StudentDTO(
                null,
                "Alice",
                "Brown",
                "alice.brown@test.com",
                "ROLL001",
                "CSE",
                2
        );

        
        StudentDTO created = studentService.create(toCreate);
        assertNotNull(created.id(), "Created student should have an ID");
        assertEquals("alice.brown@test.com", created.email());

        List<StudentDTO> all = studentService.list();
        assertTrue(all.stream().anyMatch(s -> s.id().equals(created.id())));
    }

    @Test
    @Order(2)
    @Transactional
    void get_and_update() {
        
        StudentDTO created = studentService.create(new StudentDTO(
                null,
                "Bob",
                "Green",
                "bob.green@test.com",
                "ROLL002",
                "ECE",
                1
        ));

        
        StudentDTO fetched = studentService.get(created.id());
        assertEquals("bob.green@test.com", fetched.email());
        assertEquals("ECE", fetched.department());

        StudentDTO updated = studentService.update(created.id(), new StudentDTO(
                created.id(),
                "Bob",
                "Green",
                "bob.green.updated@test.com", 
                "ROLL002",                     
                "EEE",                         
                2                              
        ));

        assertEquals("bob.green.updated@test.com", updated.email());
        assertEquals("EEE", updated.department());
        assertEquals(2, updated.yearOfStudy());
    }

    @Test
    @Order(3)
    @Transactional
    void delete_and_verify_not_found() {
       
        StudentDTO created = studentService.create(new StudentDTO(
                null,
                "Carol",
                "White",
                "carol.white@test.com",
                "ROLL003",
                "MECH",
                3
        ));

        
        assertDoesNotThrow(() -> studentService.delete(created.id()));

        
        assertThrows(NotFoundException.class, () -> studentService.get(created.id()));
    }

    @Test
    @Order(4)
    @Transactional
    void duplicate_checks_should_fail() {
        
        StudentDTO base = studentService.create(new StudentDTO(
                null,
                "Dan",
                "Gray",
                "dan.gray@test.com",
                "ROLL004",
                "CIVIL",
                1
        ));

     
        assertThrows(IllegalArgumentException.class, () ->
                studentService.create(new StudentDTO(
                        null,
                        "Danny",
                        "Gray",
                        "dan.gray@test.com", 
                        "ROLL005",
                        "CIVIL",
                        1
                ))
        );

       
        assertThrows(IllegalArgumentException.class, () ->
                studentService.create(new StudentDTO(
                        null,
                        "Dan",
                        "Grayson",
                        "other.email@test.com",
                        "ROLL004", 
                        "CIVIL",
                        1
                ))
        );

      
        StudentDTO other = studentService.create(new StudentDTO(
                null,
                "Eve",
                "Stone",
                "eve.stone@test.com",
                "ROLL006",
                "IT",
                2
        ));

        assertThrows(IllegalArgumentException.class, () ->
                studentService.update(other.id(), new StudentDTO(
                        other.id(),
                        "Eve",
                        "Stone",
                        "dan.gray@test.com", 
                        "ROLL006",
                        "IT",
                        2
                ))
        );

        
        assertThrows(IllegalArgumentException.class, () ->
                studentService.update(other.id(), new StudentDTO(
                        other.id(),
                        "Eve",
                        "Stone",
                        "eve.stone@test.com",
                        "ROLL004", 
                        "IT",
                        2
                ))
        );
    }
}