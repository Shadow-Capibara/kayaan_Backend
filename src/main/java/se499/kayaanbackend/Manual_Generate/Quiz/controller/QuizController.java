package se499.kayaanbackend.Manual_Generate.Quiz.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import se499.kayaanbackend.Manual_Generate.Quiz.dto.QuizRequestDTO;
import se499.kayaanbackend.Manual_Generate.Quiz.dto.QuizResponseDTO;
import se499.kayaanbackend.Manual_Generate.Quiz.service.QuizService;
import se499.kayaanbackend.Manual_Generate.contentInfo.service.ContentServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/api/manual/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    private final ContentServiceImpl contentServiceImpl;

    @PostMapping
    public ResponseEntity<QuizResponseDTO> createQuiz(@RequestBody QuizRequestDTO requestDto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        QuizResponseDTO savedQuiz = contentServiceImpl.createQuiz(requestDto, username);
        return ResponseEntity.ok(savedQuiz);
    }

    @GetMapping
    public ResponseEntity<List<QuizResponseDTO>> getAllQuizzesForUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<QuizResponseDTO> quizzes = contentServiceImpl.getAllQuizzes(username);
        return ResponseEntity.ok(quizzes);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<QuizResponseDTO>> filterQuizzes(
            @RequestParam(required = false) String subject) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (subject != null) {
            return ResponseEntity.ok(contentServiceImpl.getQuizzesBySubject(username, subject));
        }
        return ResponseEntity.ok(contentServiceImpl.getAllQuizzes(username));
    }

    @GetMapping("/{quizId}")
    public ResponseEntity<QuizResponseDTO> getQuizById(@PathVariable Integer quizId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        QuizResponseDTO quiz = contentServiceImpl.getQuizById(quizId, username);
        return ResponseEntity.ok(quiz);
    }

    @PutMapping("/{quizId}")
    public ResponseEntity<QuizResponseDTO> updateQuiz(
            @PathVariable Integer quizId,
            @RequestBody QuizRequestDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        QuizResponseDTO updated = contentServiceImpl.updateQuiz(quizId, dto, username);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{quizId}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Integer quizId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        contentServiceImpl.deleteQuiz(quizId, username);
        return ResponseEntity.noContent().build();
    }

//    // 1. Create a new quiz
//    @PostMapping
//    public ResponseEntity<QuizResponseDTO> createQuiz(
//            @RequestBody QuizRequestDTO requestDto
//    ) {
//        // Retrieve the logged-in user's username from Spring Security context
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String username = auth.getName();
//
//        QuizResponseDTO savedQuiz = quizService.createQuiz(requestDto, username);
//        return ResponseEntity.ok(savedQuiz);
//    }
//
//    // 2. Get all quizzes created by this user
//    @GetMapping
//    public ResponseEntity<List<QuizResponseDTO>> getAllQuizzesForUser() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String username = auth.getName();
//
//        List<QuizResponseDTO> quizzes = quizService.getAllQuizzesForUser(username);
//        return ResponseEntity.ok(quizzes);
//    }
//
//    @GetMapping("/filter")
//    public ResponseEntity<List<QuizResponseDTO>> filterQuizzes(@RequestParam(required = false) String category,
//                                                               @RequestParam(required = false) String subject) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String username = auth.getName();
//        if (category != null) {
//            return ResponseEntity.ok(quizService.getQuizzesByCategory(username, category));
//        }
//        if (subject != null) {
//            return ResponseEntity.ok(quizService.getQuizzesBySubject(username, subject));
//        }
//        return ResponseEntity.ok(quizService.getAllQuizzesForUser(username));
//    }
//
//    // 3. Get a single quiz by ID (only if owned by user)
//    @GetMapping("/{quizId}")
//    public ResponseEntity<QuizResponseDTO> getQuizById(
//            @PathVariable Long quizId
//    ) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String username = auth.getName();
//
//        QuizResponseDTO quiz = quizService.getQuizById(quizId, username);
//        return ResponseEntity.ok(quiz);
//    }
//
//    // 4. Delete a quiz (only if owned by user)
//    @DeleteMapping("/{quizId}")
//    public ResponseEntity<Void> deleteQuiz(
//            @PathVariable Long quizId
//    ) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String username = auth.getName();
//
//        quizService.deleteQuiz(quizId, username);
//        return ResponseEntity.noContent().build();
//    }
//
//    @PutMapping("/{quizId}")
//    public ResponseEntity<QuizResponseDTO> updateQuiz(@PathVariable Long quizId,
//                                                      @RequestBody QuizRequestDTO dto) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String username = auth.getName();
//        QuizResponseDTO updated = quizService.updateQuiz(quizId, dto, username);
//        return ResponseEntity.ok(updated);
//    }
}
