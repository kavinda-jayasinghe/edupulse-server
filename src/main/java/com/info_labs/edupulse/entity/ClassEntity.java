package com.info_labs.edupulse.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "students")
@Entity
@Table(name = "classes")
public class ClassEntity {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(unique = true, length = 20)
    private String classCode;

    @Column(length = 100)
    private String subject;

    @ManyToMany(mappedBy = "classes", fetch = FetchType.LAZY)
    private Set<User> students = new HashSet<>();
}
