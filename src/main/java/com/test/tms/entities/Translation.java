package com.test.tms.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

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

    @Column(nullable = false, name = "translation_key")
    private String key;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String locale;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "translation_tags",
            joinColumns = @JoinColumn(name = "translation_id")
    )
    @Column(name = "tag")
    private Set<String> tags;
}
