// package com.qnaverse.QnAverse.services;

// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import com.qnaverse.QnAverse.models.LikeLog;
// import com.qnaverse.QnAverse.models.Question;
// import com.qnaverse.QnAverse.models.User;
// import com.qnaverse.QnAverse.repositories.LikeLogRepository;

// @Service
// @Transactional
// public class LikeLogService {

//     private final LikeLogRepository likeLogRepository;

//     @Autowired
//     public LikeLogService(LikeLogRepository likeLogRepository) {
//         this.likeLogRepository = likeLogRepository;
//     }

//     public List<LikeLog> getLikeLogsByUser(String username) {
//         return likeLogRepository.findByUserUsername(username);  // Fetch like logs by the username of the user
//     }

//     public void logLikeAction(User user, Question question, String action) {
//         LikeLog likeLog = new LikeLog(user, question, action);
//         likeLogRepository.save(likeLog);
//     }
// }
