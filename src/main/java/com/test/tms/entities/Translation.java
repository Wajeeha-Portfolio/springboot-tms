package com.test.tms.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "translations")
public class Translation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String field;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String locale;

    @Column(nullable = false)
    private List<String> tags;
}
