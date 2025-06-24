package se499.kayaanbackend.Manual_Generate.Note.dto;


import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoteResponseDTO {
    private Long id;
    private String createdByUsername;
    private String title;
    private String content;
    private String subject;
    private String difficulty;
    private List<String> tags;
}
