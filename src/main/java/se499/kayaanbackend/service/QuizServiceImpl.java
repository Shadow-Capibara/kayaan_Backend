package se499.kayaanbackend.service;


import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se499.kayaanbackend.DTO.QuizQuestionRequestDTO;
import se499.kayaanbackend.DTO.QuizRequestDTO;
import se499.kayaanbackend.DTO.QuizResponseDTO;
import se499.kayaanbackend.entity.ContentInformation;
import se499.kayaanbackend.entity.Quiz;
import se499.kayaanbackend.entity.QuizQuestion;
import se499.kayaanbackend.repository.ContentInformationRepository;
import se499.kayaanbackend.repository.QuizQuestionChoiceRepository;
import se499.kayaanbackend.repository.QuizQuestionRepository;
import se499.kayaanbackend.repository.QuizRepository;
import se499.kayaanbackend.security.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizServiceImpl implements QuizService {
    private final QuizRepository quizRepository;
    private final QuizQuestionRepository questionRepository;
    private final QuizQuestionChoiceRepository choiceRepository;
    private final ContentInformationRepository contentInformationRepository;
    private final UserService userService;

    @Override
    public QuizResponseDTO createQuiz(QuizRequestDTO requestDto, String createdByUsername) {
        var user = userService.findByUsername(createdByUsername);

        String subject = requestDto.getQuestions().isEmpty() ? null : requestDto.getQuestions().get(0).getSubject();
        String difficulty = requestDto.getQuestions().isEmpty() ? "MEDIUM" : requestDto.getQuestions().get(0).getDifficulty();
        String tag = requestDto.getQuestions().isEmpty() ? null : (requestDto.getQuestions().get(0).getTags() != null && !requestDto.getQuestions().get(0).getTags().isEmpty() ? requestDto.getQuestions().get(0).getTags().get(0) : null);

        ContentInformation content = ContentInformation.builder()
                .contentType(ContentInformation.ContentType.QUIZ)
                .contentSubject(subject)
                .contentTitle(requestDto.getTitle())
                .tag(tag)
                .difficulty(ContentInformation.Difficulty.valueOf(difficulty.toUpperCase()))
                .user(user)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();

        content = contentInformationRepository.save(content);

        Quiz quiz = Quiz.builder()
                .contentInformation(content)
                .quizType(Quiz.QuizType.MULTIPLE_CHOICE)
                .quizDetail(requestDto.getTitle())
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();

        List<QuizQuestion> questionEntities = requestDto.getQuestions().stream().map(qDTO -> {
            QuizQuestion question = QuizQuestion.builder()
                    .quiz(quiz)
                    .questionText(qDTO.getQuestionText())
                    .type(mapDtoTypeToEntityType(qDTO.getType()))
                    .correctAnswer(qDTO.getCorrectAnswer())
                    .createdAt(java.time.LocalDateTime.now())
                    .updatedAt(java.time.LocalDateTime.now())
                    .build();
            if (qDTO.getChoices() != null) {
                List<se499.kayaanbackend.entity.QuizQuestionChoice> choices = qDTO.getChoices().stream()
                        .map(choiceStr -> se499.kayaanbackend.entity.QuizQuestionChoice.builder()
                                .question(question)
                                .choiceDetail(choiceStr)
                                .createdAt(java.time.LocalDateTime.now())
                                .updatedAt(java.time.LocalDateTime.now())
                                .build())
                        .collect(Collectors.toList());
                question.setChoices(choices);
            }
            return question;
        }).collect(Collectors.toList());

        quiz.setQuestions(questionEntities);
        Quiz savedQuiz = quizRepository.save(quiz);

        return mapToResponseDTO(savedQuiz);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizResponseDTO> getAllQuizzesForUser(String username) {
        List<Quiz> quizzes = quizRepository.findByContentInformation_User_Username(username);
        return quizzes.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public QuizResponseDTO getQuizById(Long quizId, String username) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found: " + quizId));

        if (!quiz.getContentInformation().getUser().getUsername().equals(username)) {
            throw new SecurityException("You do not have access to this quiz.");
        }
        return mapToResponseDTO(quiz);
    }

    @Override
    public void deleteQuiz(Long quizId, String username) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found: " + quizId));

        if (!quiz.getContentInformation().getUser().getUsername().equals(username)) {
            throw new SecurityException("You do not have permission to delete this quiz.");
        }
        quizRepository.delete(quiz);
    }

    // ---------------------
    //  Helper Methods
    // ---------------------

    private QuizQuestion.QuestionType mapDtoTypeToEntityType(QuizQuestionRequestDTO.QuestionType dtoType) {
        switch (dtoType) {
            case MULTIPLE_CHOICE:
                return QuizQuestion.QuestionType.MULTIPLE_CHOICE;
            case TRUE_FALSE:
                return QuizQuestion.QuestionType.TRUE_FALSE;
            case OPEN_ENDED:
                return QuizQuestion.QuestionType.OPEN_ENDED;
            default:
                throw new IllegalArgumentException("Unknown type: " + dtoType);
        }
    }

    private QuizResponseDTO mapToResponseDTO(Quiz quiz) {
        List<QuizResponseDTO.QuestionResponse> questionResponses = quiz.getQuestions().stream().map(q ->
                QuizResponseDTO.QuestionResponse.builder()
                        .id(q.getId())
                        .questionText(q.getQuestionText())
                        .type(QuizQuestionRequestDTO.QuestionType.valueOf(q.getType().name()))
                        .choices(q.getChoices() == null ? null : q.getChoices().stream().map(se499.kayaanbackend.entity.QuizQuestionChoice::getChoiceDetail).collect(Collectors.toList()))
                        .correctAnswer(q.getCorrectAnswer())
                        .build()
        ).collect(Collectors.toList());

        return QuizResponseDTO.builder()
                .id(quiz.getId())
                .title(quiz.getContentInformation().getContentTitle())
                .createdByUsername(quiz.getContentInformation().getUser().getUsername())
                .questions(questionResponses)
                .build();
    }

}
