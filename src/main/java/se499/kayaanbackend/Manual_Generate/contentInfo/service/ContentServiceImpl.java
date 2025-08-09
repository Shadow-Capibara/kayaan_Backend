package se499.kayaanbackend.Manual_Generate.contentInfo.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se499.kayaanbackend.Manual_Generate.Flashcard.dto.FlashcardRequestDTO;
import se499.kayaanbackend.Manual_Generate.Flashcard.dto.FlashcardResponseDTO;
import se499.kayaanbackend.Manual_Generate.Flashcard.entity.Flashcard;
import se499.kayaanbackend.Manual_Generate.Flashcard.repository.FlashcardRepository;
import se499.kayaanbackend.Manual_Generate.Note.dto.NoteRequestDTO;
import se499.kayaanbackend.Manual_Generate.Note.dto.NoteResponseDTO;
import se499.kayaanbackend.Manual_Generate.Note.entity.Note;
import se499.kayaanbackend.Manual_Generate.Note.repository.NoteRepository;
import se499.kayaanbackend.Manual_Generate.Quiz.dto.QuizQuestionRequestDTO;
import se499.kayaanbackend.Manual_Generate.Quiz.dto.QuizRequestDTO;
import se499.kayaanbackend.Manual_Generate.Quiz.dto.QuizResponseDTO;
import se499.kayaanbackend.Manual_Generate.Quiz.entity.Quiz;
import se499.kayaanbackend.Manual_Generate.Quiz.entity.QuizQuestion;
import se499.kayaanbackend.Manual_Generate.Quiz.entity.QuizQuestionChoice;
import se499.kayaanbackend.Manual_Generate.Quiz.repository.QuizQuestionChoiceRepository;
import se499.kayaanbackend.Manual_Generate.Quiz.repository.QuizQuestionRepository;
import se499.kayaanbackend.Manual_Generate.Quiz.repository.QuizRepository;
import se499.kayaanbackend.Manual_Generate.contentInfo.entity.ContentInfo;
import se499.kayaanbackend.Manual_Generate.contentInfo.repository.ContentInfoRepository;
import se499.kayaanbackend.security.entity.User;
import se499.kayaanbackend.security.entity.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ContentServiceImpl implements ContentService {

    private final ContentInfoRepository contentRepository;
    private final NoteRepository noteRepository;
    private final QuizRepository quizRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizQuestionChoiceRepository quizQuestionChoiceRepository;
    private final FlashcardRepository flashcardRepository;
    private final UserRepository userRepository;

    // === NOTE OPERATIONS ===
    @Override
    public NoteResponseDTO createNote(NoteRequestDTO dto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create ContentInfo
        ContentInfo contentInfo = createContentInfo(dto.getTitle(), dto.getSubject(), 
                dto.getDifficulty(), dto.getTags(), ContentInfo.ContentType.NOTE, user);

        // Create Note
        Note note = Note.builder()
                .contentInfo(contentInfo)
                .noteText(dto.getContent())
                .imageUrl(dto.getImageUrl())
                .sharedGroups(new ArrayList<>())
                .build();

        note = noteRepository.save(note);

        return mapToNoteResponse(contentInfo, note);
    }

    @Override
    public NoteResponseDTO updateNote(Long contentId, NoteRequestDTO dto, String username) {
        ContentInfo contentInfo = contentRepository.findByContentIdAndCreatedByUsername(contentId, username)
                .orElseThrow(() -> new RuntimeException("Note not found or unauthorized"));

        // Update ContentInfo
        updateContentInfo(contentInfo, dto.getTitle(), dto.getSubject(), dto.getDifficulty(), dto.getTags());

        // Update Note
        Note note = noteRepository.findByContentInfo_ContentId(contentId)
                .orElseThrow(() -> new RuntimeException("Note data not found"));

        note.setNoteText(dto.getContent());
        note.setImageUrl(dto.getImageUrl());
        note = noteRepository.save(note);

        return mapToNoteResponse(contentInfo, note);
    }

    @Override
    public void deleteNote(Long contentId, String username) {
        ContentInfo contentInfo = contentRepository.findByContentIdAndCreatedByUsername(contentId, username)
                .orElseThrow(() -> new RuntimeException("Note not found or unauthorized"));

        contentInfo.setIsActive(false);
        contentRepository.save(contentInfo);
    }

    @Override
    @Transactional(readOnly = true)
    public NoteResponseDTO getNoteById(Long contentId, String username) {
        ContentInfo contentInfo = contentRepository.findByContentIdAndCreatedByUsername(contentId, username)
                .orElseThrow(() -> new RuntimeException("Note not found or unauthorized"));

        Note note = noteRepository.findByContentInfo_ContentId(contentId)
                .orElseThrow(() -> new RuntimeException("Note data not found"));

        return mapToNoteResponse(contentInfo, note);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoteResponseDTO> getAllNotes(String username) {
        List<ContentInfo> contentInfos = contentRepository.findByCreatedByUsernameAndContentType(username, ContentInfo.ContentType.NOTE);
        
        return contentInfos.stream()
                .map(contentInfo -> {
                    Note note = noteRepository.findByContentInfo_ContentId(contentInfo.getContentId())
                            .orElse(null);
                    return note != null ? mapToNoteResponse(contentInfo, note) : null;
                })
                .filter(response -> response != null)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoteResponseDTO> getNotesBySubject(String username, String subject) {
        List<ContentInfo> contentInfos = contentRepository.findByUsernameTypeAndSubject(username, ContentInfo.ContentType.NOTE, subject);
        
        return contentInfos.stream()
                .map(contentInfo -> {
                    Note note = noteRepository.findByContentInfo_ContentId(contentInfo.getContentId())
                            .orElse(null);
                    return note != null ? mapToNoteResponse(contentInfo, note) : null;
                })
                .filter(response -> response != null)
                .collect(Collectors.toList());
    }

    // === FLASHCARD OPERATIONS ===
    @Override
    public FlashcardResponseDTO createFlashcard(FlashcardRequestDTO dto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create ContentInfo
        ContentInfo contentInfo = createContentInfo(dto.getFrontText(), dto.getSubject(), 
                dto.getDifficulty(), dto.getTags(), ContentInfo.ContentType.FLASHCARD, user);

        // Create Flashcard
        Flashcard flashcard = Flashcard.builder()
                .contentInfo(contentInfo)
                .frontText(dto.getFrontText())
                .backText(dto.getBackText())
                .frontImageUrl(dto.getFrontImageUrl())
                .backImageUrl(dto.getBackImageUrl())
                .sharedGroups(new ArrayList<>())
                .build();

        flashcard = flashcardRepository.save(flashcard);

        return mapToFlashcardResponse(contentInfo, flashcard);
    }

    @Override
    public FlashcardResponseDTO updateFlashcard(Long contentId, FlashcardRequestDTO dto, String username) {
        ContentInfo contentInfo = contentRepository.findByContentIdAndCreatedByUsername(contentId, username)
                .orElseThrow(() -> new RuntimeException("Flashcard not found or unauthorized"));

        // Update ContentInfo
        updateContentInfo(contentInfo, dto.getFrontText(), dto.getSubject(), dto.getDifficulty(), dto.getTags());

        // Update Flashcard
        Flashcard flashcard = flashcardRepository.findByContentInfo_ContentId(contentId)
                .orElseThrow(() -> new RuntimeException("Flashcard data not found"));

        flashcard.setFrontText(dto.getFrontText());
        flashcard.setBackText(dto.getBackText());
        flashcard.setFrontImageUrl(dto.getFrontImageUrl());
        flashcard.setBackImageUrl(dto.getBackImageUrl());
        flashcard = flashcardRepository.save(flashcard);

        return mapToFlashcardResponse(contentInfo, flashcard);
    }

    @Override
    public void deleteFlashcard(Long contentId, String username) {
        ContentInfo contentInfo = contentRepository.findByContentIdAndCreatedByUsername(contentId, username)
                .orElseThrow(() -> new RuntimeException("Flashcard not found or unauthorized"));

        contentInfo.setIsActive(false);
        contentRepository.save(contentInfo);
    }

    @Override
    @Transactional(readOnly = true)
    public FlashcardResponseDTO getFlashcardById(Long contentId, String username) {
        ContentInfo contentInfo = contentRepository.findByContentIdAndCreatedByUsername(contentId, username)
                .orElseThrow(() -> new RuntimeException("Flashcard not found or unauthorized"));

        Flashcard flashcard = flashcardRepository.findByContentInfo_ContentId(contentId)
                .orElseThrow(() -> new RuntimeException("Flashcard data not found"));

        return mapToFlashcardResponse(contentInfo, flashcard);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlashcardResponseDTO> getAllFlashcards(String username) {
        List<ContentInfo> contentInfos = contentRepository.findByCreatedByUsernameAndContentType(username, ContentInfo.ContentType.FLASHCARD);
        
        return contentInfos.stream()
                .map(contentInfo -> {
                    Flashcard flashcard = flashcardRepository.findByContentInfo_ContentId(contentInfo.getContentId())
                            .orElse(null);
                    return flashcard != null ? mapToFlashcardResponse(contentInfo, flashcard) : null;
                })
                .filter(response -> response != null)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlashcardResponseDTO> getFlashcardsBySubject(String username, String subject) {
        List<ContentInfo> contentInfos = contentRepository.findByUsernameTypeAndSubject(username, ContentInfo.ContentType.FLASHCARD, subject);
        
        return contentInfos.stream()
                .map(contentInfo -> {
                    Flashcard flashcard = flashcardRepository.findByContentInfo_ContentId(contentInfo.getContentId())
                            .orElse(null);
                    return flashcard != null ? mapToFlashcardResponse(contentInfo, flashcard) : null;
                })
                .filter(response -> response != null)
                .collect(Collectors.toList());
    }

    // === QUIZ OPERATIONS ===
    @Override
    public QuizResponseDTO createQuiz(QuizRequestDTO dto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create ContentInfo
        ContentInfo contentInfo = createContentInfo(dto.getTitle(), "General", 
                "MEDIUM", new ArrayList<>(), ContentInfo.ContentType.QUIZ, user);

        // Create Quiz
        Quiz quiz = Quiz.builder()
                .contentInfo(contentInfo)
                .questions(new ArrayList<>())
                .sharedGroups(new ArrayList<>())
                .build();

        quiz = quizRepository.save(quiz);

        // Create QuizQuestions and QuizQuestionChoices
        if (dto.getQuestions() != null) {
            for (int i = 0; i < dto.getQuestions().size(); i++) {
                QuizQuestionRequestDTO questionDto = dto.getQuestions().get(i);
                QuizQuestion question = createQuizQuestion(quiz, questionDto, i);
                quiz.getQuestions().add(question);
            }
        }

        return mapToQuizResponse(contentInfo, quiz);
    }

    @Override
    public QuizResponseDTO updateQuiz(Long contentId, QuizRequestDTO dto, String username) {
        ContentInfo contentInfo = contentRepository.findByContentIdAndCreatedByUsername(contentId, username)
                .orElseThrow(() -> new RuntimeException("Quiz not found or unauthorized"));

        // Update ContentInfo
        updateContentInfo(contentInfo, dto.getTitle(), "General", "MEDIUM", new ArrayList<>());

        // Update Quiz
        Quiz quiz = quizRepository.findByContentInfo_ContentId(contentId)
                .orElseThrow(() -> new RuntimeException("Quiz data not found"));

        // Clear existing questions and choices
        quiz.getQuestions().forEach(question -> {
            quizQuestionChoiceRepository.deleteAll(question.getChoices());
        });
        quizQuestionRepository.deleteAll(quiz.getQuestions());
        quiz.getQuestions().clear();

        // Create new questions
        if (dto.getQuestions() != null) {
            for (int i = 0; i < dto.getQuestions().size(); i++) {
                QuizQuestionRequestDTO questionDto = dto.getQuestions().get(i);
                QuizQuestion question = createQuizQuestion(quiz, questionDto, i);
                quiz.getQuestions().add(question);
            }
        }

        return mapToQuizResponse(contentInfo, quiz);
    }

    @Override
    public void deleteQuiz(Long contentId, String username) {
        ContentInfo contentInfo = contentRepository.findByContentIdAndCreatedByUsername(contentId, username)
                .orElseThrow(() -> new RuntimeException("Quiz not found or unauthorized"));

        contentInfo.setIsActive(false);
        contentRepository.save(contentInfo);
    }

    @Override
    @Transactional(readOnly = true)
    public QuizResponseDTO getQuizById(Long contentId, String username) {
        ContentInfo contentInfo = contentRepository.findByContentIdAndCreatedByUsername(contentId, username)
                .orElseThrow(() -> new RuntimeException("Quiz not found or unauthorized"));

        Quiz quiz = quizRepository.findByContentInfo_ContentId(contentId)
                .orElseThrow(() -> new RuntimeException("Quiz data not found"));

        return mapToQuizResponse(contentInfo, quiz);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizResponseDTO> getAllQuizzes(String username) {
        List<ContentInfo> contentInfos = contentRepository.findByCreatedByUsernameAndContentType(username, ContentInfo.ContentType.QUIZ);
        
        return contentInfos.stream()
                .map(contentInfo -> {
                    Quiz quiz = quizRepository.findByContentInfo_ContentId(contentInfo.getContentId())
                            .orElse(null);
                    return quiz != null ? mapToQuizResponse(contentInfo, quiz) : null;
                })
                .filter(response -> response != null)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizResponseDTO> getQuizzesBySubject(String username, String subject) {
        List<ContentInfo> contentInfos = contentRepository.findByUsernameTypeAndSubject(username, ContentInfo.ContentType.QUIZ, subject);
        
        return contentInfos.stream()
                .map(contentInfo -> {
                    Quiz quiz = quizRepository.findByContentInfo_ContentId(contentInfo.getContentId())
                            .orElse(null);
                    return quiz != null ? mapToQuizResponse(contentInfo, quiz) : null;
                })
                .filter(response -> response != null)
                .collect(Collectors.toList());
    }

    // === HELPER METHODS ===
    private ContentInfo createContentInfo(String title, String subject, String difficulty, 
                                        List<String> tags, ContentInfo.ContentType contentType, User user) {
        ContentInfo contentInfo = ContentInfo.builder()
                .title(title != null ? title : "Untitled")
                .subject(subject != null ? subject : "General")
                .difficulty(mapDifficulty(difficulty))
                .contentType(contentType)
                .tags(tags != null ? tags : new ArrayList<>())
                .createdBy(user)
                .isActive(true)
                .build();

        return contentRepository.save(contentInfo);
    }

    private void updateContentInfo(ContentInfo contentInfo, String title, String subject, 
                                 String difficulty, List<String> tags) {
        contentInfo.setTitle(title != null ? title : contentInfo.getTitle());
        contentInfo.setSubject(subject != null ? subject : contentInfo.getSubject());
        contentInfo.setDifficulty(mapDifficulty(difficulty));
        contentInfo.setTags(tags != null ? tags : contentInfo.getTags());
        contentRepository.save(contentInfo);
    }

    private QuizQuestion createQuizQuestion(Quiz quiz, QuizQuestionRequestDTO questionDto, int orderIndex) {
        QuizQuestion question = QuizQuestion.builder()
                .quiz(quiz)
                .questionText(questionDto.getQuestionText())
                .type(mapQuestionType(questionDto.getType()))
                .correctAnswer(questionDto.getCorrectAnswer())
                .orderIndex(orderIndex)
                .choices(new ArrayList<>())
                .build();

        question = quizQuestionRepository.save(question);

        // Create choices for multiple choice questions
        if (questionDto.getType() == QuizQuestionRequestDTO.QuestionType.MULTIPLE_CHOICE && 
            questionDto.getChoices() != null) {
            for (int i = 0; i < questionDto.getChoices().size(); i++) {
                String choiceText = questionDto.getChoices().get(i);
                boolean isCorrect = choiceText.equals(questionDto.getCorrectAnswer());
                
                QuizQuestionChoice choice = QuizQuestionChoice.builder()
                        .question(question)
                        .choiceText(choiceText)
                        .isCorrect(isCorrect)
                        .orderIndex(i)
                        .build();
                
                choice = quizQuestionChoiceRepository.save(choice);
                question.getChoices().add(choice);
            }
        }

        return question;
    }

    private ContentInfo.ContentDifficulty mapDifficulty(String difficulty) {
        if (difficulty == null) return ContentInfo.ContentDifficulty.MEDIUM;
        
        switch (difficulty.toUpperCase()) {
            case "EASY": return ContentInfo.ContentDifficulty.EASY;
            case "HARD": return ContentInfo.ContentDifficulty.HARD;
            default: return ContentInfo.ContentDifficulty.MEDIUM;
        }
    }

    private QuizQuestion.QuestionType mapQuestionType(QuizQuestionRequestDTO.QuestionType type) {
        if (type == null) return QuizQuestion.QuestionType.MULTIPLE_CHOICE;
        
        switch (type) {
            case TRUE_FALSE: return QuizQuestion.QuestionType.TRUE_FALSE;
            case OPEN_ENDED: return QuizQuestion.QuestionType.OPEN_ENDED;
            default: return QuizQuestion.QuestionType.MULTIPLE_CHOICE;
        }
    }

    private QuizQuestionRequestDTO.QuestionType mapToResponseQuestionType(QuizQuestion.QuestionType type) {
        switch (type) {
            case TRUE_FALSE: return QuizQuestionRequestDTO.QuestionType.TRUE_FALSE;
            case OPEN_ENDED: return QuizQuestionRequestDTO.QuestionType.OPEN_ENDED;
            default: return QuizQuestionRequestDTO.QuestionType.MULTIPLE_CHOICE;
        }
    }

    // === MAPPING METHODS ===
    private NoteResponseDTO mapToNoteResponse(ContentInfo contentInfo, Note note) {
        return NoteResponseDTO.builder()
                .id(contentInfo.getContentId())
                .createdByUsername(contentInfo.getCreatedBy().getUsername())
                .title(contentInfo.getTitle())
                .content(note.getNoteText())
                .subject(contentInfo.getSubject())
                .difficulty(contentInfo.getDifficulty().name())
                .category("Note")
                .imageUrl(note.getImageUrl())
                .tags(contentInfo.getTags())
                .groupIds(new ArrayList<>())
                .build();
    }

    private FlashcardResponseDTO mapToFlashcardResponse(ContentInfo contentInfo, Flashcard flashcard) {
        return FlashcardResponseDTO.builder()
                .id(contentInfo.getContentId())
                .createdByUsername(contentInfo.getCreatedBy().getUsername())
                .frontText(flashcard.getFrontText())
                .backText(flashcard.getBackText())
                .subject(contentInfo.getSubject())
                .difficulty(contentInfo.getDifficulty().name())
                .category("Flashcard")
                .frontImageUrl(flashcard.getFrontImageUrl())
                .backImageUrl(flashcard.getBackImageUrl())
                .tags(contentInfo.getTags())
                .groupIds(new ArrayList<>())
                .build();
    }

    private QuizResponseDTO mapToQuizResponse(ContentInfo contentInfo, Quiz quiz) {
        List<QuizResponseDTO.QuestionResponse> questionResponses = quiz.getQuestions().stream()
                .map(this::mapToQuestionResponse)
                .collect(Collectors.toList());

        return QuizResponseDTO.builder()
                .id(contentInfo.getContentId())
                .title(contentInfo.getTitle())
                .createdByUsername(contentInfo.getCreatedBy().getUsername())
                .category("Quiz")
                .groupIds(new ArrayList<>())
                .questions(questionResponses)
                .build();
    }

    private QuizResponseDTO.QuestionResponse mapToQuestionResponse(QuizQuestion question) {
        List<String> choices = question.getChoices().stream()
                .map(QuizQuestionChoice::getChoiceText)
                .collect(Collectors.toList());

        return QuizResponseDTO.QuestionResponse.builder()
                .id(question.getId())
                .questionText(question.getQuestionText())
                .type(mapToResponseQuestionType(question.getType()))
                .choices(choices)
                .correctAnswer(question.getCorrectAnswer())
                .subject("General")
                .difficulty("MEDIUM")
                .tags(new ArrayList<>())
                .build();
    }
}