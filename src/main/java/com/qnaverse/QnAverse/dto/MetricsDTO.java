package com.qnaverse.QnAverse.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents an answer entity in the database.
 */
@Getter
@Setter
@NoArgsConstructor

public class MetricsDTO {

    // User Metrics
    private long totalRegisteredUsers;
    private long dailyRegisteredUsers;
    private long weeklyRegisteredUsers;
    private long monthlyRegisteredUsers;
    private long yearlyRegisteredUsers;

    // Question Metrics
    private long totalQuestions;
    private long dailyQuestions;
    private long weeklyQuestions;
    private long monthlyQuestions;
    private long yearlyQuestions;
    private long answeredQuestions;
    private long unansweredQuestions;

}
