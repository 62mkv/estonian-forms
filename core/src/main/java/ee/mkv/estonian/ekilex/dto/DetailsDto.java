package ee.mkv.estonian.ekilex.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DetailsDto {
    private WordDto word;
    private List<DetailsLexemeDto> lexemes;
}
