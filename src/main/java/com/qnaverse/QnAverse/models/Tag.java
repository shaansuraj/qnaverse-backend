package com.qnaverse.QnAverse.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a single row in the "tags" table,
 * e.g. (id=1, tagName="react").
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tags")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;  // 'id int AI PK' in DB

    @Column(name = "tag_name", nullable = false, length = 50)
    private String tagName;

    // Constructors
    public Tag(String tagName) {
        this.tagName = tagName;
    }
}
