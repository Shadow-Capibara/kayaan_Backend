package se499.kayaanbackend.controller;


import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import se499.kayaanbackend.DTO.QuizRequestDTO;
import se499.kayaanbackend.DTO.QuizResponseDTO;
import se499.kayaanbackend.service.QuizService;

import java.util.List;

@RestController
@RequestMapping("/api/manual/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    // 1. Create a new quiz
    @PostMapping
    public ResponseEntity<QuizResponseDTO> createQuiz(
            @RequestBody QuizRequestDTO requestDto
    ) {
        // Retrieve the logged-in user's username from Spring Security context
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        QuizResponseDTO savedQuiz = quizService.createQuiz(requestDto, username);
        return ResponseEntity.ok(savedQuiz);
    }

    // 2. Get all quizzes created by this user
    @GetMapping
    public ResponseEntity<List<QuizResponseDTO>> getAllQuizzesForUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        List<QuizResponseDTO> quizzes = quizService.getAllQuizzesForUser(username);
        return ResponseEntity.ok(quizzes);
    }

    // 3. Get a single quiz by ID (only if owned by user)
    @GetMapping("/{quizId}")
    public ResponseEntity<QuizResponseDTO> getQuizById(
            @PathVariable Long quizId
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        QuizResponseDTO quiz = quizService.getQuizById(quizId, username);
        return ResponseEntity.ok(quiz);
    }

    // 4. Delete a quiz (only if owned by user)
    @DeleteMapping("/{quizId}")
    public ResponseEntity<Void> deleteQuiz(
            @PathVariable Long quizId
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        quizService.deleteQuiz(quizId, username);
        return ResponseEntity.noContent().build();
    }

    // (Optional) You can add an update endpoint if you want to allow editing an existing quiz
    // @PutMapping("/{quizId}") ...
}
