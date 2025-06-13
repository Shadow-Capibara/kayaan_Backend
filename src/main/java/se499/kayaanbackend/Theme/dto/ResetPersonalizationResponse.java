package se499.kayaanbackend.Theme.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPersonalizationResponse {
    private Boolean ok;
    private String message;
}
