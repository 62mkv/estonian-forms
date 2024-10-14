package ee.mkv.estonian.ekilex.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DetailsClassifierDto {
    private String name;
    private String code;
    private String value;
    /*
        {
          "name": "POS",
          "code": "adj",
          "value": "adjektiiv, omadussõna"
        }

     */
}
