package com.qnaverse.QnAverse.services;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.qnaverse.QnAverse.dto.MetricsDTO;
import com.qnaverse.QnAverse.repositories.QuestionRepository;
import com.qnaverse.QnAverse.repositories.UserRepository;

@Service
public class MetricsService {

    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;

    public MetricsService(UserRepository userRepository,
                          QuestionRepository questionRepository) {
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
    }

    public MetricsDTO getAllMetrics() {
        MetricsDTO dto = new MetricsDTO();

        // ========== USER METRICS ==========
        long totalUsers = userRepository.count();  // total
        dto.setTotalQuestions(totalUsers);

        // We'll get "now" and subtract intervals for daily, weekly, monthly, etc.
        LocalDate today = LocalDate.now();
        Date startOfDay = asDate(today); // midnight today
        Date startOfWeek = asDate(today.minusDays(7));
        Date startOfMonth = asDate(today.minusMonths(1));
        Date startOfYear = asDate(today.minusYears(1));

        // Example: you can define these query methods or adapt them to your code:
        long dailyNewUsers = userRepository.countByCreatedAtBetween(startOfDay, new Date());
        long weeklyNewUsers = userRepository.countByCreatedAtBetween(startOfWeek, new Date());
        long monthlyNewUsers = userRepository.countByCreatedAtBetween(startOfMonth, new Date());
        long yearlyNewUsers = userRepository.countByCreatedAtBetween(startOfYear, new Date());

        dto.setDailyRegisteredUsers(dailyNewUsers);
        dto.setWeeklyRegisteredUsers(weeklyNewUsers);
        dto.setMonthlyRegisteredUsers(monthlyNewUsers);
        dto.setYearlyRegisteredUsers(yearlyNewUsers);

        // ========== QUESTION METRICS ==========
        long totalQuestions = questionRepository.count(); // total
        dto.setTotalQuestions(totalQuestions);

        // We assume you added a createdAt to your Question entity
        long dailyQuestions = questionRepository.countByCreatedAtBetween(startOfDay, new Date());
        long weeklyQuestions = questionRepository.countByCreatedAtBetween(startOfWeek, new Date());
        long monthlyQuestions = questionRepository.countByCreatedAtBetween(startOfMonth, new Date());
        long yearlyQuestions = questionRepository.countByCreatedAtBetween(startOfYear, new Date());

        dto.setDailyQuestions(dailyQuestions);
        dto.setWeeklyQuestions(weeklyQuestions);
        dto.setMonthlyQuestions(monthlyQuestions);
        dto.setYearlyQuestions(yearlyQuestions);

        // answered vs. unanswered (assuming you track `answerCount` in question)
        long answered = questionRepository.countByAnswerCountGreaterThan(0);
        long unanswered = questionRepository.countByAnswerCount(0);

        dto.setAnsweredQuestions(answered);
        dto.setUnansweredQuestions(unanswered);

        return dto;
    }

    // Helper to convert LocalDate to Date
    private Date asDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
