package pe.edu.vallegrande.app.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Data;


@Data
@Table(name = "moderacion")
public class ModerationResult {

    @Id
    private Integer id;
    
    @Column(value = "OriginalText")
    private String originalText;
    
    @Column(value = "NormalizedText")
    private String normalizedText;
    
    @Column(value = "ConsultText")
    private String consultText;
    
    @Column(value = "Status")
    private char status = 'A';
    
}