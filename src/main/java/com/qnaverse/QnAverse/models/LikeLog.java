// package com.qnaverse.QnAverse.models;

// import java.util.Date;

// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.JoinColumn;
// import jakarta.persistence.ManyToOne;
// import jakarta.persistence.Table;
// import jakarta.persistence.Temporal;
// import jakarta.persistence.TemporalType;
// import lombok.Getter;
// import lombok.NoArgsConstructor;
// import lombok.Setter;

// @Getter
// @Setter
// @NoArgsConstructor
// @Entity
// @Table(name = "like_logs")
// public class LikeLog {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     @ManyToOne
//     @JoinColumn(name = "user_id", nullable = false)
//     private User user;

//     @ManyToOne
//     @JoinColumn(name = "question_id", nullable = false)
//     private Question question;

//     @Column(nullable = false)
//     private String action; // "LIKED" or "UNLIKED"

//     @Temporal(TemporalType.TIMESTAMP)
//     private Date createdAt;

//     public LikeLog(User user, Question question, String action) {
//         this.user = user;
//         this.question = question;
//         this.action = action;
//         this.createdAt = new Date();
//     }
// }
