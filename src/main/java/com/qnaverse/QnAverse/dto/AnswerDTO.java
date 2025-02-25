package com.qnaverse.QnAverse.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for handling answer-related requests.
 */
@Getter
@Setter
public class AnswerDTO {

    @NotBlank(message = "Answer content cannot be empty")
    private String content;
}
