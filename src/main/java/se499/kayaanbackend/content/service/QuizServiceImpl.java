package se499.kayaanbackend.content.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se499.kayaanbackend.content.dto.*;
import se499.kayaanbackend.content.entity.*;
import se499.kayaanbackend.content.enums.ContentType;
import se499.kayaanbackend.content.repository.*;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizServiceImpl implements QuizService {

    private final ContentInformationRepository contentRepository;
    private final QuizInformationRepository quizRepository;

    @Override
    public QuizDto createQuiz(Long userId, QuizDto dto) {
        ContentInformation content = ContentInformation.builder()
                .userId(userId)
                .contentType(ContentType.QUIZ)
                .build();
        contentRepository.save(content);

        QuizInformation quiz = QuizInformation.builder()
                .contentInformation(content)
                .title(dto.getTitle())
                .build();

        if (dto.getQuestions() != null) {
            quiz.setQuestions(dto.getQuestions().stream()
                    .map(q -> toQuestionEntity(q, quiz))
                    .collect(Collectors.toList()));
        }

        QuizInformation saved = quizRepository.save(quiz);
        dto.setQuizId(saved.getQuizId());
        dto.setContentId(content.getContentId());
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public QuizDto getQuiz(Long id) {
        QuizInformation quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        return toDto(quiz);
    }

    @Override
    public QuizDto updateQuiz(Long id, QuizDto dto) {
        QuizInformation quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        quiz.setTitle(dto.getTitle());
        quiz.getQuestions().clear();
        if (dto.getQuestions() != null) {
            quiz.getQuestions().addAll(
                    dto.getQuestions().stream()
                            .map(q -> toQuestionEntity(q, quiz))
                            .collect(Collectors.toList()));
        }
        return toDto(quizRepository.save(quiz));
    }

    @Override
    public void deleteQuiz(Long id) {
        QuizInformation quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        quiz.setDeletedAt(LocalDateTime.now());
        quiz.getContentInformation().setDeletedAt(LocalDateTime.now());
        quizRepository.save(quiz);
    }

    @Override
    public void restoreQuiz(Long id) {
        QuizInformation quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        quiz.setDeletedAt(null);
        quiz.getContentInformation().setDeletedAt(null);
        quizRepository.save(quiz);
    }

    private QuizDto toDto(QuizInformation quiz) {
        QuizDto dto = new QuizDto();
        dto.setQuizId(quiz.getQuizId());
        dto.setContentId(quiz.getContentInformation().getContentId());
        dto.setTitle(quiz.getTitle());
        dto.setQuestions(
                quiz.getQuestions().stream()
                        .map(this::toQuestionDto)
                        .collect(Collectors.toList())
        );
        return dto;
    }

    private QuizQuestionDto toQuestionDto(QuizQuestionInformation question) {
        QuizQuestionDto dto = new QuizQuestionDto();
        dto.setQuestionId(question.getQuestionId());
        dto.setQuestionText(question.getQuestionText());
        dto.setChoices(
                question.getChoices().stream()
                        .map(c -> {
                            QuizChoiceDto cdto = new QuizChoiceDto();
                            cdto.setChoiceId(c.getChoiceId());
                            cdto.setChoiceDetail(c.getChoiceDetail());
                            return cdto;
                        }).collect(Collectors.toList())
        );
        return dto;
    }

    private QuizQuestionInformation toQuestionEntity(QuizQuestionDto dto, QuizInformation quiz) {
        QuizQuestionInformation question = QuizQuestionInformation.builder()
                .quiz(quiz)
                .questionText(dto.getQuestionText())
                .build();
        if (dto.getChoices() != null) {
            question.setChoices(dto.getChoices().stream()
                    .map(c -> QuizQuestionChoice.builder()
                            .question(question)
                            .choiceDetail(c.getChoiceDetail())
                            .build())
                    .collect(Collectors.toList()));
        }
        return question;
    }
}
