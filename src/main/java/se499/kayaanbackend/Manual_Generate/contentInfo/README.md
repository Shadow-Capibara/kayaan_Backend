# Content Management System Redesign

## Overview
This document describes the redesigned content management system that centralizes common content attributes and reduces data redundancy.

## Architecture

### Core Entities

#### 1. ContentInfo (Central Entity)
- **Purpose**: Stores common attributes shared across all content types
- **Key Fields**:
  - `contentId`: Primary key
  - `title`: Content title
  - `subject`: Subject area
  - `difficulty`: EASY, MEDIUM, HARD
  - `contentType`: NOTE, FLASHCARD, QUIZ
  - `tags`: List of tags
  - `createdBy`: User who created the content
  - `isActive`: Soft delete flag

#### 2. Content-Specific Entities

##### Note
- **Purpose**: Stores note-specific data
- **Key Fields**:
  - `noteText`: The actual note content
  - `imageUrl`: Optional image URL
  - `contentInfo`: Reference to ContentInfo

##### Flashcard
- **Purpose**: Stores flashcard-specific data
- **Key Fields**:
  - `frontText`: Question side
  - `backText`: Answer side
  - `frontImageUrl`: Optional front image
  - `backImageUrl`: Optional back image
  - `contentInfo`: Reference to ContentInfo

##### Quiz
- **Purpose**: Stores quiz-specific data
- **Key Fields**:
  - `questions`: List of QuizQuestion entities
  - `contentInfo`: Reference to ContentInfo

##### QuizQuestion
- **Purpose**: Stores individual quiz questions
- **Key Fields**:
  - `questionText`: The question
  - `type`: MULTIPLE_CHOICE, TRUE_FALSE, OPEN_ENDED
  - `correctAnswer`: Correct answer text
  - `orderIndex`: Question order
  - `choices`: List of QuizQuestionChoice entities

##### QuizQuestionChoice
- **Purpose**: Stores individual choices for multiple choice questions
- **Key Fields**:
  - `choiceText`: The choice text
  - `isCorrect`: Whether this choice is correct
  - `orderIndex`: Choice order

## Service Layer

### ContentServiceImpl
- **Purpose**: Centralized service for all content operations
- **Key Features**:
  - Unified creation logic for all content types
  - Consistent update and delete operations
  - Proper relationship management
  - Soft delete implementation

### Key Methods

#### Creation Methods
```java
NoteResponseDTO createNote(NoteRequestDTO dto, String username)
FlashcardResponseDTO createFlashcard(FlashcardRequestDTO dto, String username)
QuizResponseDTO createQuiz(QuizRequestDTO dto, String username)
```

#### Helper Methods
```java
private ContentInfo createContentInfo(String title, String subject, String difficulty, 
                                    List<String> tags, ContentInfo.ContentType contentType, User user)
private QuizQuestion createQuizQuestion(Quiz quiz, QuizQuestionRequestDTO questionDto, int orderIndex)
```

## Benefits of New Design

1. **Reduced Data Redundancy**: Common fields (title, subject, difficulty, tags) are stored once in ContentInfo
2. **Centralized Logic**: All content creation logic is in ContentServiceImpl
3. **Consistent Operations**: Same patterns for CRUD operations across all content types
4. **Better Relationships**: Proper foreign key relationships between entities
5. **Soft Delete**: Content is marked as inactive rather than physically deleted
6. **Type Safety**: Enum-based content types and difficulties

## Database Schema

### Tables
- `content_info`: Central content information
- `content_tags`: Content tags (many-to-many)
- `notes`: Note-specific data
- `flashcards`: Flashcard-specific data
- `quizzes`: Quiz-specific data
- `quiz_questions`: Quiz questions
- `quiz_question_choices`: Question choices

### Relationships
- ContentInfo ↔ Note (One-to-One)
- ContentInfo ↔ Flashcard (One-to-One)
- ContentInfo ↔ Quiz (One-to-One)
- Quiz ↔ QuizQuestion (One-to-Many)
- QuizQuestion ↔ QuizQuestionChoice (One-to-Many)

## Usage Examples

### Creating a Note
```java
NoteRequestDTO dto = new NoteRequestDTO();
dto.setTitle("Java Basics");
dto.setSubject("Programming");
dto.setDifficulty("MEDIUM");
dto.setTags(Arrays.asList("java", "programming"));
dto.setContent("Java is an object-oriented programming language...");

NoteResponseDTO response = contentService.createNote(dto, "username");
```

### Creating a Quiz
```java
QuizRequestDTO dto = new QuizRequestDTO();
dto.setTitle("Java Quiz");
dto.setQuestions(Arrays.asList(
    QuizQuestionRequestDTO.builder()
        .questionText("What is Java?")
        .type(QuizQuestionRequestDTO.QuestionType.MULTIPLE_CHOICE)
        .choices(Arrays.asList("Programming Language", "Coffee", "Island"))
        .correctAnswer("Programming Language")
        .build()
));

QuizResponseDTO response = contentService.createQuiz(dto, "username");
```

## Migration Notes

When migrating from the old system:
1. Update all entity references to use the new structure
2. Update repository method calls
3. Update service method signatures (Integer → Long for IDs)
4. Update controller endpoints to use the new service methods
5. Create database migration scripts for schema changes
