package se499.kayaanbackend.Manual_Generate.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "manual_quiz_question")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManualQuizQuestion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private ManualQuiz quiz;
    
    @Column(name = "question_text", columnDefinition = "TEXT", nullable = false)
    private String questionText;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false)
    private QuestionType questionType;
    
    @Column(columnDefinition = "JSON")
    private String choices; // JSON array for multiple choice options
    
    @Column(name = "correct_answer")
    private String correctAnswer;
    
    @Column(name = "question_order")
    private Integer questionOrder;
    
    private String subject;
    
    private String difficulty;
    
    @Column(columnDefinition = "TEXT")
    private String tags;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public enum QuestionType {
        MULTIPLE_CHOICE,
        TRUE_FALSE,
        OPEN_ENDED
    }
}
