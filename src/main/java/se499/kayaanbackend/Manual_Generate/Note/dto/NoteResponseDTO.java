package se499.kayaanbackend.Manual_Generate.Note.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteResponseDTO {
    
    private Long id;
    private String createdByUsername;
    private String title;
    private String content;
    private String subject;
    private String difficulty;
    private String tags;

}
