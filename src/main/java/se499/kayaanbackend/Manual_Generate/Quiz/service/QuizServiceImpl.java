package se499.kayaanbackend.Manual_Generate.Quiz.service;


import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se499.kayaanbackend.Manual_Generate.Quiz.dto.QuizQuestionRequestDTO;
import se499.kayaanbackend.Manual_Generate.Quiz.dto.QuizRequestDTO;
import se499.kayaanbackend.Manual_Generate.Quiz.dto.QuizResponseDTO;
import se499.kayaanbackend.Manual_Generate.Quiz.entity.Quiz;
import se499.kayaanbackend.Manual_Generate.Quiz.entity.QuizQuestion;
import se499.kayaanbackend.Manual_Generate.Quiz.repository.QuizQuestionRepository;
import se499.kayaanbackend.Manual_Generate.Quiz.repository.QuizRepository;
import se499.kayaanbackend.Manual_Generate.Group.entity.Group;
import se499.kayaanbackend.Manual_Generate.Group.repository.GroupRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizServiceImpl implements QuizService {
    private final QuizRepository quizRepository;
    private final QuizQuestionRepository questionRepository;
    private final GroupRepository groupRepository;

    @Override
    public QuizResponseDTO createQuiz(QuizRequestDTO requestDto, String createdByUsername) {
        // 1. Build Quiz entity (without questions yet)
        List<Group> groups = requestDto.getGroupIds() == null ? java.util.Collections.emptyList() :
                groupRepository.findAllById(requestDto.getGroupIds());

        Quiz quiz = Quiz.builder()
                .title(requestDto.getTitle())
                .category(requestDto.getCategory())
                .createdByUsername(createdByUsername)
                .sharedGroups(groups)
                .build();

        // 2. Convert each QuestionRequestDto → Question entity
        List<QuizQuestion> questionEntities = requestDto.getQuestions().stream().map(qDTO -> {
            QuizQuestion question = QuizQuestion.builder()
                    .quiz(quiz)
                    .questionText(qDTO.getQuestionText())
                    .type(mapDtoTypeToEntityType(qDTO.getType()))
                    .choices(qDTO.getChoices())
                    .correctAnswer(qDTO.getCorrectAnswer())
                    .subject(qDTO.getSubject())
                    .difficulty(qDTO.getDifficulty())
                    .tags(qDTO.getTags())
                    .build();
            return question;
        }).collect(Collectors.toList());

        // 3. Set questions on Quiz and save
        quiz.setQuestions(questionEntities);
        Quiz savedQuiz = quizRepository.save(quiz);

        // 4. Convert savedQuiz → QuizResponseDto
        return mapToResponseDTO(savedQuiz);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizResponseDTO> getAllQuizzesForUser(String username) {
        List<Quiz> quizzes = quizRepository.findByCreatedByUsername(username);
        return quizzes.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizResponseDTO> getQuizzesByCategory(String username, String category) {
        List<Quiz> quizzes = quizRepository.findByCreatedByUsernameAndCategory(username, category);
        return quizzes.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizResponseDTO> getQuizzesBySubject(String username, String subject) {
        List<Quiz> quizzes = quizRepository.findByCreatedByUsernameAndQuestions_Subject(username, subject);
        return quizzes.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public QuizResponseDTO getQuizById(Long quizId, String username) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found: " + quizId));

        if (!quiz.getCreatedByUsername().equals(username)) {
            throw new SecurityException("You do not have access to this quiz.");
        }
        return mapToResponseDTO(quiz);
    }

    @Override
    public QuizResponseDTO updateQuiz(Long quizId, QuizRequestDTO requestDto, String username) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found: " + quizId));
        if (!quiz.getCreatedByUsername().equals(username)) {
            throw new SecurityException("You do not have permission to edit this quiz.");
        }
        quiz.setTitle(requestDto.getTitle());
        quiz.setCategory(requestDto.getCategory());
        List<Group> groups = requestDto.getGroupIds() == null ? java.util.Collections.emptyList() :
                groupRepository.findAllById(requestDto.getGroupIds());
        quiz.setSharedGroups(groups);
        quiz.getQuestions().clear();
        List<QuizQuestion> questionEntities = requestDto.getQuestions().stream().map(qDTO -> QuizQuestion.builder()
                .quiz(quiz)
                .questionText(qDTO.getQuestionText())
                .type(mapDtoTypeToEntityType(qDTO.getType()))
                .choices(qDTO.getChoices())
                .correctAnswer(qDTO.getCorrectAnswer())
                .subject(qDTO.getSubject())
                .difficulty(qDTO.getDifficulty())
                .tags(qDTO.getTags())
                .build()).collect(Collectors.toList());
        quiz.getQuestions().addAll(questionEntities);
        Quiz saved = quizRepository.save(quiz);
        return mapToResponseDTO(saved);
    }

    @Override
    public void deleteQuiz(Long quizId, String username) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found: " + quizId));

        if (!quiz.getCreatedByUsername().equals(username)) {
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
                        .choices(q.getChoices())
                        .correctAnswer(q.getCorrectAnswer())
                        .subject(q.getSubject())
                        .difficulty(q.getDifficulty())
                        .tags(q.getTags())
                        .build()
        ).collect(Collectors.toList());

        return QuizResponseDTO.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .createdByUsername(quiz.getCreatedByUsername())
                .category(quiz.getCategory())
                .groupIds(quiz.getSharedGroups().stream().map(Group::getId).collect(Collectors.toList()))
                .questions(questionResponses)
                .build();
    }

}
