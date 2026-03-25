package com.example.grades.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "students",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_roll", columnNames = "rollNumber"),
        @UniqueConstraint(name = "uk_email", columnNames = "email")
    }
)
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(max = 50)
    private String firstName;

    @NotBlank @Size(max = 50)
    private String lastName;

    @NotBlank @Email @Size(max = 120)
    private String email;

    @NotBlank @Size(max = 20)
    private String rollNumber;

    @NotBlank @Size(max = 50)
    private String department;

    @Min(1) @Max(8)
    private int yearOfStudy;

    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SemesterMark> marks = new ArrayList<>();

    // ---- getters / setters ----
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getRollNumber() { return rollNumber; }

    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public String getDepartment() { return department; }

    public void setDepartment(String department) { this.department = department; }

    public int getYearOfStudy() { return yearOfStudy; }

    public void setYearOfStudy(int yearOfStudy) { this.yearOfStudy = yearOfStudy; }

    public Instant getCreatedAt() { return createdAt; }

    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public List<SemesterMark> getMarks() { return marks; }

    public void setMarks(List<SemesterMark> marks) { this.marks = marks; }
}
