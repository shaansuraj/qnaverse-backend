package com.qnaverse.QnAverse.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qnaverse.QnAverse.models.Answer;
import com.qnaverse.QnAverse.models.AnswerLog;
import com.qnaverse.QnAverse.models.User;
import com.qnaverse.QnAverse.repositories.AnswerLogRepository;

@Service
@Transactional
public class AnswerLogService {

    private final AnswerLogRepository answerLogRepository;

    @Autowired
    public AnswerLogService(AnswerLogRepository answerLogRepository) {
        this.answerLogRepository = answerLogRepository;
    }

    public List<AnswerLog> getAnswerLogsByUser(String username) {
        return answerLogRepository.findByUserUsername(username);  // Fetch answer logs by the username of the user
    }

    public void logAnswerAction(User user, Answer answer, String action) {
        AnswerLog answerLog = new AnswerLog(user, answer, action);
        answerLogRepository.save(answerLog);
    }
}
