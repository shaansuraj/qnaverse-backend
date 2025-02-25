package com.qnaverse.QnAverse.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for handling question-related requests.
 */
@Getter
@Setter
public class QuestionDTO {

    @NotBlank(message = "Content cannot be empty")
    private String content;

    private List<String> tags;
}
