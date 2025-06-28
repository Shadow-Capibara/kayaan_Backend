package se499.kayaanbackend.content.dto;

import lombok.Data;
import java.util.List;

@Data
public class NoteDto {
    private Long noteId;
    private Long contentId;
    private String noteTitle;
    private List<NoteImageDto> images;
}
