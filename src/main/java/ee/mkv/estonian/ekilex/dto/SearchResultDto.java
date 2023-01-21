package ee.mkv.estonian.ekilex.dto;

import lombok.Data;

import java.util.List;

@Data
public class SearchResultDto {
    private Long totalCount;
    private List<WordDto> words;
}
