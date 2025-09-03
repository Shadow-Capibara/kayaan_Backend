package se499.kayaanbackend.Manual_Generate.Quiz.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se499.kayaanbackend.Manual_Generate.Quiz.dto.QuizQuestionRequestDTO;
import se499.kayaanbackend.Manual_Generate.Quiz.dto.QuizRequestDTO;
import se499.kayaanbackend.Manual_Generate.Quiz.dto.QuizResponseDTO;
import se499.kayaanbackend.Manual_Generate.entity.ManualQuiz;
import se499.kayaanbackend.Manual_Generate.entity.ManualQuizQuestion;
import se499.kayaanbackend.Manual_Generate.repository.ManualQuizRepository;
import se499.kayaanbackend.Manual_Generate.repository.ManualQuizQuestionRepository;
import se499.kayaanbackend.security.user.User;
import se499.kayaanbackend.security.user.UserRepository;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class QuizServiceImpl implements QuizService {
    
    private final ManualQuizRepository quizRepository;
    private final ManualQuizQuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    
    @Override
    public QuizResponseDTO createQuiz(QuizRequestDTO requestDto, String username) {
        try {
            log.info("Creating quiz '{}' for user: {}", requestDto.getTitle(), username);
            
            // Find user
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
            
            // Extract metadata from questions
            String subject = extractSubjectFromQuestions(requestDto.getQuestions());
            String difficulty = extractDifficultyFromQuestions(requestDto.getQuestions());
            String tags = extractTagsFromQuestions(requestDto.getQuestions());
            
            // Create quiz
            ManualQuiz quiz = ManualQuiz.builder()
                .user(user)
                .title(requestDto.getTitle())
                .subject(subject)
                .difficulty(difficulty)
                .tags(tags)
                .build();
                
            ManualQuiz savedQuiz = quizRepository.save(quiz);
            log.info("Quiz created with ID: {}", savedQuiz.getId());
            
            // Create questions
            AtomicInteger orderCounter = new AtomicInteger(1);
            List<ManualQuizQuestion> questions = requestDto.getQuestions().stream()
                .map((QuizQuestionRequestDTO questionDto) -> {
                    try {
                        return ManualQuizQuestion.builder()
                            .quiz(savedQuiz)
                            .questionText(questionDto.getQuestionText())
                            .questionType(ManualQuizQuestion.QuestionType.valueOf(questionDto.getType().name()))
                            .choices(questionDto.getChoices() != null && !questionDto.getChoices().isEmpty() ? 
                                objectMapper.writeValueAsString(questionDto.getChoices()) : null)
                            .correctAnswer(questionDto.getCorrectAnswer())
                            .questionOrder(orderCounter.getAndIncrement())
                            .subject(questionDto.getSubject())
                            .difficulty(questionDto.getDifficulty())
                            .tags(questionDto.getTags() != null ? String.join(",", questionDto.getTags()) : null)
                            .build();
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Failed to serialize choices for question: " + questionDto.getQuestionText(), e);
                    }
                })
                .collect(Collectors.toList());
                
            questionRepository.saveAll(questions);
            log.info("Created {} questions for quiz ID: {}", questions.size(), savedQuiz.getId());
            
            return mapToResponseDTO(savedQuiz, questions);
            
        } catch (Exception e) {
            log.error("Error creating quiz for user: {}", username, e);
            throw new RuntimeException("Failed to create quiz: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<QuizResponseDTO> getAllQuizzesForUser(String username) {
        try {
            log.info("Getting all quizzes for user: {}", username);
            
            List<ManualQuiz> quizzes = quizRepository.findByUsernameAndNotDeleted(username);
            
            return quizzes.stream()
                .map(quiz -> {
                    List<ManualQuizQuestion> questions = questionRepository.findByQuizIdOrderByOrder(quiz.getId());
                    return mapToResponseDTO(quiz, questions);
                })
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            log.error("Error getting quizzes for user: {}", username, e);
            throw new RuntimeException("Failed to get quizzes: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public QuizResponseDTO getQuizById(Long id, String username) {
        try {
            log.info("Getting quiz {} for user: {}", id, username);
            
            ManualQuiz quiz = quizRepository.findByIdAndUsernameAndNotDeleted(id, username)
                .orElseThrow(() -> new RuntimeException("Quiz not found or access denied: " + id));
            
            List<ManualQuizQuestion> questions = questionRepository.findByQuizIdOrderByOrder(quiz.getId());
            
            return mapToResponseDTO(quiz, questions);
            
        } catch (Exception e) {
            log.error("Error getting quiz {} for user: {}", id, username, e);
            throw new RuntimeException("Failed to get quiz: " + e.getMessage());
        }
    }
    
    @Override
    public void deleteQuiz(Long id, String username) {
        try {
            log.info("Deleting quiz {} for user: {}", id, username);
            
            ManualQuiz quiz = quizRepository.findByIdAndUsernameAndNotDeleted(id, username)
                .orElseThrow(() -> new RuntimeException("Quiz not found or access denied: " + id));
            
            // Soft delete
            quiz.markAsDeleted();
            quizRepository.save(quiz);
            
            log.info("Quiz {} deleted successfully", id);
            
        } catch (Exception e) {
            log.error("Error deleting quiz {} for user: {}", id, username, e);
            throw new RuntimeException("Failed to delete quiz: " + e.getMessage());
        }
    }
    
    private QuizResponseDTO mapToResponseDTO(ManualQuiz quiz, List<ManualQuizQuestion> questions) {
        List<QuizResponseDTO.QuestionResponse> questionResponses = questions.stream()
            .sorted(Comparator.comparing(ManualQuizQuestion::getQuestionOrder))
            .map(q -> QuizResponseDTO.QuestionResponse.builder()
                .id(q.getId())
                .questionText(q.getQuestionText())
                .type(q.getQuestionType())
                .choices(parseChoices(q.getChoices()))
                .correctAnswer(q.getCorrectAnswer())
                .questionOrder(q.getQuestionOrder())
                .subject(q.getSubject())
                .difficulty(q.getDifficulty())
                .tags(q.getTags())
                .build())
            .collect(Collectors.toList());
            
        return QuizResponseDTO.builder()
            .id(quiz.getId())
            .title(quiz.getTitle())
            .createdByUsername(quiz.getUser().getUsername())
            .subject(quiz.getSubject())
            .difficulty(quiz.getDifficulty())
            .tags(quiz.getTags())
            .createdAt(quiz.getCreatedAt())
            .updatedAt(quiz.getUpdatedAt())
            .questions(questionResponses)
            .build();
    }
    
    private List<String> parseChoices(String choicesJson) {
        if (choicesJson == null || choicesJson.trim().isEmpty()) {
            return List.of();
        }
        
        try {
            return objectMapper.readValue(choicesJson, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse choices JSON: {}", choicesJson, e);
            return List.of();
        }
    }
    
    private String extractSubjectFromQuestions(List<QuizQuestionRequestDTO> questions) {
        return questions.stream()
            .map(QuizQuestionRequestDTO::getSubject)
            .filter(subject -> subject != null && !subject.trim().isEmpty())
            .findFirst()
            .orElse(null);
    }
    
    private String extractDifficultyFromQuestions(List<QuizQuestionRequestDTO> questions) {
        return questions.stream()
            .map(QuizQuestionRequestDTO::getDifficulty)
            .filter(difficulty -> difficulty != null && !difficulty.trim().isEmpty())
            .findFirst()
            .orElse(null);
    }
    
    private String extractTagsFromQuestions(List<QuizQuestionRequestDTO> questions) {
        return questions.stream()
            .flatMap(q -> q.getTags() != null ? q.getTags().stream() : List.<String>of().stream())
            .filter(tag -> tag != null && !tag.trim().isEmpty())
            .distinct()
            .collect(Collectors.joining(","));
    }
}
