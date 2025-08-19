package se499.kayaanbackend.Manual_Generate.contentInfo.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se499.kayaanbackend.Manual_Generate.Flashcard.dto.FlashcardRequestDTO;
import se499.kayaanbackend.Manual_Generate.Flashcard.dto.FlashcardResponseDTO;
import se499.kayaanbackend.Manual_Generate.Flashcard.repository.FlashcardRepository;
import se499.kayaanbackend.Manual_Generate.Note.dto.NoteRequestDTO;
import se499.kayaanbackend.Manual_Generate.Note.dto.NoteResponseDTO;
import se499.kayaanbackend.Manual_Generate.Note.entity.Note;
import se499.kayaanbackend.Manual_Generate.Note.repository.NoteRepository;
import se499.kayaanbackend.Manual_Generate.Quiz.dto.QuizQuestionRequestDTO;
import se499.kayaanbackend.Manual_Generate.Quiz.dto.QuizRequestDTO;
import se499.kayaanbackend.Manual_Generate.Quiz.dto.QuizResponseDTO;
import se499.kayaanbackend.Manual_Generate.Quiz.entity.QuizQuestion;
import se499.kayaanbackend.Manual_Generate.Quiz.repository.QuizRepository;
import se499.kayaanbackend.Manual_Generate.contentInfo.entity.ContentInfo;
import se499.kayaanbackend.Manual_Generate.contentInfo.repository.ContentInfoRepository;
import se499.kayaanbackend.security.user.User;
import se499.kayaanbackend.security.user.UserRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ContentServiceImpl implements ContentService {

    private final ContentInfoRepository contentRepository;
    private final NoteRepository noteRepository;
    private final QuizRepository quizRepository;
    private final FlashcardRepository flashcardRepository;
    private final UserRepository userRepository;

    // === NOTE OPERATIONS ===
    @Override
    public NoteResponseDTO createNote(NoteRequestDTO dto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ContentInfo content = ContentInfo.builder()
                .contentTitle(dto.getTitle() != null ? dto.getTitle() : "Untitled")
                .contentTag(dto.getTags() != null ? String.join(",", dto.getTags()) : "")
                .contentSubject(dto.getSubject() != null ? dto.getSubject() : "General")
                .contentDifficulty(mapDifficulty(dto.getDifficulty()))
                .contentType("NOTE")
                .userCreatedAt(user)
                .build();

        content = contentRepository.save(content);

        Note note = Note.builder()
                .content(content)
                .noteText(dto.getContent())
                .build();

        note = noteRepository.save(note);

        return mapToNoteResponse(content, note);
    }

    @Override
    public NoteResponseDTO updateNote(Integer contentId, NoteRequestDTO dto, String username) {
        ContentInfo content = contentRepository.findByContentIdAndUserCreatedAtUsername(contentId, username)
                .orElseThrow(() -> new RuntimeException("Note not found or unauthorized"));

        content.setContentTitle(dto.getTitle());
        content.setContentTag(dto.getTags() != null ? String.join(",", dto.getTags()) : "");
        content.setContentSubject(dto.getSubject());
        content.setContentDifficulty(mapDifficulty(dto.getDifficulty()));

        content = contentRepository.save(content);

        Note note = noteRepository.findByContent_ContentId(contentId)
                .orElseThrow(() -> new RuntimeException("Note data not found"));

        note.setNoteText(dto.getContent());
        note = noteRepository.save(note);

        return mapToNoteResponse(content, note);
    }

    @Override
    public void deleteNote(Integer contentId, String username) {
        ContentInfo content = contentRepository.findByContentIdAndUserCreatedAtUsername(contentId, username)
                .orElseThrow(() -> new RuntimeException("Note not found or unauthorized"));

        contentRepository.delete(content); // Cascade will delete the Note
    }

    @Override
    @Transactional(readOnly = true)
    public NoteResponseDTO getNoteById(Integer contentId, String username) {
        ContentInfo content = contentRepository.findByContentIdAndUserCreatedAtUsername(contentId, username)
                .orElseThrow(() -> new RuntimeException("Note not found or unauthorized"));

        Note note = noteRepository.findByContent_ContentId(contentId)
                .orElseThrow(() -> new RuntimeException("Note data not found"));

        return mapToNoteResponse(content, note);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoteResponseDTO> getAllNotes(String username) {
        List<ContentInfo> contents = contentRepository.findByUserCreatedAtUsernameAndContentType(username, "NOTE");
        return contents.stream()
                .map(content -> {
                    Note note = noteRepository.findByContent_ContentId(content.getContentId())
                            .orElseThrow(() -> new RuntimeException("Note data not found"));
                    return mapToNoteResponse(content, note);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoteResponseDTO> getNotesBySubject(String username, String subject) {
        List<ContentInfo> contents = contentRepository.findByUsernameTypeAndSubject(username, "NOTE", subject);
        return contents.stream()
                .map(content -> {
                    Note note = noteRepository.findByContent_ContentId(content.getContentId())
                            .orElseThrow(() -> new RuntimeException("Note data not found"));
                    return mapToNoteResponse(content, note);
                })
                .collect(Collectors.toList());
    }

    // === QUIZ OPERATIONS ===
    @Override
    public QuizResponseDTO createQuiz(QuizRequestDTO dto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Extract subject from first question if not provided
        String subject = dto.getSubject();
        if (subject == null && dto.getQuestions() != null && !dto.getQuestions().isEmpty()) {
            subject = dto.getQuestions().get(0).getSubject();
        }

        ContentInfo content = ContentInfo.builder()
                .contentTitle(dto.getTitle())
                .contentTag(dto.getTags() != null ? String.join(",", dto.getTags()) : "")
                .contentSubject(subject != null ? subject : "")
                .contentDifficulty(mapDifficulty(dto.getDifficulty()))
                .contentType("QUIZ")
                .userCreatedAt(user)
                .build();

        content = contentRepository.save(content);

        Quiz quiz = Quiz.builder()
                .content(content)
                .questions(new HashSet<>())
                .build();

        quiz = quizRepository.save(quiz);

        // Add questions
        if (dto.getQuestions() != null) {
            final Quiz finalQuiz = quiz;  // Create final reference
            for (QuizQuestionRequestDTO questionDto : dto.getQuestions()) {
                QuizQuestion question = QuizQuestion.builder()
                        .quiz(finalQuiz)
                        .questionText(questionDto.getQuestionText())
                        .questionType(mapQuestionType(questionDto.getType()))
                        .choices(questionDto.getChoices() != null ? String.join("|", questionDto.getChoices()) : null)
                        .correctAnswer(questionDto.getCorrectAnswer())
                        .build();

                finalQuiz.getQuestions().add(question);
            }
            quiz = quizRepository.save(quiz);
        }

        return mapToQuizResponse(content, quiz);
    }

    @Override
    public QuizResponseDTO updateQuiz(Integer contentId, QuizRequestDTO dto, String username) {
        ContentInfo content = contentRepository.findByContentIdAndUserCreatedAtUsername(contentId, username)
                .orElseThrow(() -> new RuntimeException("Quiz not found or unauthorized"));

        content.setContentTitle(dto.getTitle());
        content.setContentTag(dto.getTags() != null ? String.join(",", dto.getTags()) : "");
        content.setContentSubject(dto.getSubject() != null ? dto.getSubject() : "");
        content.setContentDifficulty(mapDifficulty(dto.getDifficulty()));

        content = contentRepository.save(content);

        Quiz quiz = quizRepository.findByContent_ContentId(contentId)
                .orElseThrow(() -> new RuntimeException("Quiz data not found"));

        // Clear existing questions
        quiz.getQuestions().clear();

        // Add new questions
        if (dto.getQuestions() != null) {
            Quiz finalQuiz = quiz;
            dto.getQuestions().forEach(questionDto -> {
                QuizQuestion question = QuizQuestion.builder()
                        .quiz(finalQuiz)
                        .questionText(questionDto.getQuestionText())
                        .questionType(mapQuestionType(questionDto.getType()))
                        .choices(questionDto.getChoices() != null ? String.join("|", questionDto.getChoices()) : null)
                        .correctAnswer(questionDto.getCorrectAnswer())
                        .build();

                finalQuiz.getQuestions().add(question);
            });
        }

        quiz = quizRepository.save(quiz);

        return mapToQuizResponse(content, quiz);
    }

    @Override
    public void deleteQuiz(Integer contentId, String username) {
        ContentInfo content = contentRepository.findByContentIdAndUserCreatedAtUsername(contentId, username)
                .orElseThrow(() -> new RuntimeException("Quiz not found or unauthorized"));

        contentRepository.delete(content); // Cascade will delete the Quiz and Questions
    }

    @Override
    @Transactional(readOnly = true)
    public QuizResponseDTO getQuizById(Integer contentId, String username) {
        ContentInfo content = contentRepository.findByContentIdAndUserCreatedAtUsername(contentId, username)
                .orElseThrow(() -> new RuntimeException("Quiz not found or unauthorized"));

        Quiz quiz = quizRepository.findByContent_ContentId(contentId)
                .orElseThrow(() -> new RuntimeException("Quiz data not found"));

        return mapToQuizResponse(content, quiz);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizResponseDTO> getAllQuizzes(String username) {
        List<ContentInfo> contents = contentRepository.findByUserCreatedAtUsernameAndContentType(username, "QUIZ");
        return contents.stream()
                .map(content -> {
                    Quiz quiz = quizRepository.findByContent_ContentId(content.getContentId())
                            .orElseThrow(() -> new RuntimeException("Quiz data not found"));
                    return mapToQuizResponse(content, quiz);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizResponseDTO> getQuizzesBySubject(String username, String subject) {
        List<ContentInfo> contents = contentRepository.findByUsernameTypeAndSubject(username, "QUIZ", subject);
        return contents.stream()
                .map(content -> {
                    Quiz quiz = quizRepository.findByContent_ContentId(content.getContentId())
                            .orElseThrow(() -> new RuntimeException("Quiz data not found"));
                    return mapToQuizResponse(content, quiz);
                })
                .collect(Collectors.toList());
    }

    // === FLASHCARD OPERATIONS ===
    @Override
    public FlashcardResponseDTO createFlashcard(FlashcardRequestDTO dto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ContentInfo content = ContentInfo.builder()
                .contentTitle(dto.getFrontText())  // Using front text as title
                .contentTag(dto.getTags() != null ? String.join(",", dto.getTags()) : "")
                .contentSubject(dto.getSubject() != null ? dto.getSubject() : "")
                .contentDifficulty(mapDifficulty(dto.getDifficulty()))
                .contentType("FLASHCARD")
                .userCreatedAt(user)
                .build();

        content = contentRepository.save(content);

        Flashcard flashcard = Flashcard.builder()
                .content(content)
                .frontText(dto.getFrontText())
                .backText(dto.getBackText())
                .build();

        flashcard = flashcardRepository.save(flashcard);

        return mapToFlashcardResponse(content, flashcard);
    }

    @Override
    public FlashcardResponseDTO updateFlashcard(Integer contentId, FlashcardRequestDTO dto, String username) {
        ContentInfo content = contentRepository.findByContentIdAndUserCreatedAtUsername(contentId, username)
                .orElseThrow(() -> new RuntimeException("Flashcard not found or unauthorized"));

        content.setContentTitle(dto.getFrontText());
        content.setContentTag(dto.getTags() != null ? String.join(",", dto.getTags()) : "");
        content.setContentSubject(dto.getSubject() != null ? dto.getSubject() : "");
        content.setContentDifficulty(mapDifficulty(dto.getDifficulty()));

        content = contentRepository.save(content);

        Flashcard flashcard = flashcardRepository.findByContent_ContentId(contentId)
                .orElseThrow(() -> new RuntimeException("Flashcard data not found"));

        flashcard.setFrontText(dto.getFrontText());
        flashcard.setBackText(dto.getBackText());
        flashcard = flashcardRepository.save(flashcard);

        return mapToFlashcardResponse(content, flashcard);
    }

    @Override
    public void deleteFlashcard(Integer contentId, String username) {
        ContentInfo content = contentRepository.findByContentIdAndUserCreatedAtUsername(contentId, username)
                .orElseThrow(() -> new RuntimeException("Flashcard not found or unauthorized"));

        contentRepository.delete(content); // Cascade will delete the Flashcard
    }

    @Override
    @Transactional(readOnly = true)
    public FlashcardResponseDTO getFlashcardById(Integer contentId, String username) {
        ContentInfo content = contentRepository.findByContentIdAndUserCreatedAtUsername(contentId, username)
                .orElseThrow(() -> new RuntimeException("Flashcard not found or unauthorized"));

        Flashcard flashcard = flashcardRepository.findByContent_ContentId(contentId)
                .orElseThrow(() -> new RuntimeException("Flashcard data not found"));

        return mapToFlashcardResponse(content, flashcard);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlashcardResponseDTO> getAllFlashcards(String username) {
        List<ContentInfo> contents = contentRepository.findByUserCreatedAtUsernameAndContentType(username, "FLASHCARD");
        return contents.stream()
                .map(content -> {
                    Flashcard flashcard = flashcardRepository.findByContent_ContentId(content.getContentId())
                            .orElseThrow(() -> new RuntimeException("Flashcard data not found"));
                    return mapToFlashcardResponse(content, flashcard);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlashcardResponseDTO> getFlashcardsBySubject(String username, String subject) {
        List<ContentInfo> contents = contentRepository.findByUsernameTypeAndSubject(username, "FLASHCARD", subject);
        return contents.stream()
                .map(content -> {
                    Flashcard flashcard = flashcardRepository.findByContent_ContentId(content.getContentId())
                            .orElseThrow(() -> new RuntimeException("Flashcard data not found"));
                    return mapToFlashcardResponse(content, flashcard);
                })
                .collect(Collectors.toList());
    }

    // === HELPER METHODS ===
    private ContentInfo.ContentDifficulty mapDifficulty(String difficulty) {
        if (difficulty == null) return ContentInfo.ContentDifficulty.Easy;

        switch (difficulty.toUpperCase()) {
            case "EASY":
                return ContentInfo.ContentDifficulty.Easy;
            case "MEDIUM":
                return ContentInfo.ContentDifficulty.Medium;
            case "HARD":
                return ContentInfo.ContentDifficulty.Hard;
            default:
                return ContentInfo.ContentDifficulty.Easy;
        }
    }

    private QuizQuestion.QuestionType mapQuestionType(QuizQuestionRequestDTO.QuestionType type) {
        switch (type) {
            case MULTIPLE_CHOICE:
                return QuizQuestion.QuestionType.MULTIPLE_CHOICE;
            case TRUE_FALSE:
                return QuizQuestion.QuestionType.TRUE_FALSE;
            case OPEN_ENDED:
                return QuizQuestion.QuestionType.OPEN_END;
            default:
                return QuizQuestion.QuestionType.MULTIPLE_CHOICE;
        }
    }

    private NoteResponseDTO mapToNoteResponse(ContentInfo content, Note note) {
        return NoteResponseDTO.builder()
                .id(content.getContentId().longValue())
                .createdByUsername(content.getUserCreatedAt().getUsername())
                .title(content.getContentTitle())
                .content(note.getNoteText())
                .subject(content.getContentSubject())
                .difficulty(content.getContentDifficulty() != null ? content.getContentDifficulty().name() : null)
                .tags(content.getContentTag() != null && !content.getContentTag().isEmpty()
                        ? Arrays.asList(content.getContentTag().split(","))
                        : new ArrayList<>())
                .build();
    }

    private QuizResponseDTO mapToQuizResponse(ContentInfo content, Quiz quiz) {
        List<QuizResponseDTO.QuestionResponse> questions = quiz.getQuestions().stream()
                .map(q -> QuizResponseDTO.QuestionResponse.builder()
                        .id(q.getQuestionId().longValue())
                        .questionText(q.getQuestionText())
                        .type(mapToResponseQuestionType(q.getQuestionType()))
                        .choices(q.getChoices() != null ? Arrays.asList(q.getChoices().split("\\|")) : null)
                        .correctAnswer(q.getCorrectAnswer())
                        .build())
                .collect(Collectors.toList());

        return QuizResponseDTO.builder()
                .id(content.getContentId().longValue())
                .title(content.getContentTitle())
                .createdByUsername(content.getUserCreatedAt().getUsername())
                .category(content.getContentType())
                .questions(questions)
                .build();
    }

    private QuizQuestionRequestDTO.QuestionType mapToResponseQuestionType(QuizQuestion.QuestionType type) {
        switch (type) {
            case MULTIPLE_CHOICE:
                return QuizQuestionRequestDTO.QuestionType.MULTIPLE_CHOICE;
            case TRUE_FALSE:
                return QuizQuestionRequestDTO.QuestionType.TRUE_FALSE;
            case OPEN_END:
                return QuizQuestionRequestDTO.QuestionType.OPEN_ENDED;
            default:
                return QuizQuestionRequestDTO.QuestionType.MULTIPLE_CHOICE;
        }
    }

    private FlashcardResponseDTO mapToFlashcardResponse(ContentInfo content, Flashcard flashcard) {
        return FlashcardResponseDTO.builder()
                .id(content.getContentId().longValue())
                .createdByUsername(content.getUserCreatedAt().getUsername())
                .frontText(flashcard.getFrontText())
                .backText(flashcard.getBackText())
                .subject(content.getContentSubject())
                .difficulty(content.getContentDifficulty() != null ? content.getContentDifficulty().name() : null)
                .category(content.getContentType())
                .tags(content.getContentTag() != null && !content.getContentTag().isEmpty()
                        ? Arrays.asList(content.getContentTag().split(","))
                        : new ArrayList<>())
                .build();
    }
}