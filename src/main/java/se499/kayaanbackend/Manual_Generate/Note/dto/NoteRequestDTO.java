package se499.kayaanbackend.Manual_Generate.Note.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteRequestDTO {
    
    private String title;
    private String content;
    private String subject;
    private String difficulty;
    private List<String> tags; // Changed from String to List<String> to match Frontend
}
