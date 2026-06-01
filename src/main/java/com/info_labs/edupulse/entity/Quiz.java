package com.info_labs.edupulse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "quizzes")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String instruction;

    @Column(nullable = false)
    private Integer timeDuration;

    @Column(length = 20)
    private String dueDate;

    @Column(length = 255)
    private String paperFileName;

    @Column(length = 100)
    private String paperFileType;

    @Column
    private Long paperFileSize;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] paperFileData;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private ClassEntity classEntity;

    @Column(nullable = false)
    private Integer teacherId;

    @Column(nullable = false, length = 20)
    private String createdAt;
}
