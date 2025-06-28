package se499.kayaanbackend.content.dto;

import lombok.Data;
import java.util.List;

@Data
public class FlashcardDto {
    private Long flashcardId;
    private Long contentId;
    private String title;
    private List<FlashcardImageDto> images;
}
