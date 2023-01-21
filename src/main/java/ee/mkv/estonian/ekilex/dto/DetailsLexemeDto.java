package ee.mkv.estonian.ekilex.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DetailsLexemeDto {
    private Long wordId;
    private Long lexemeId;
    private Integer homonymNr;
    private Boolean prefixoid;
    private Boolean suffixoid;
    private List<DetailsClassifierDto> pos;
    /*
    {
      "wordId": 171855,
      "wordValue": "ilus",
      "wordValuePrese": "ilus",
      "wordLang": "est",
      "wordHomonymNr": 1,
      "wordGenderCode": null,
      "wordAspectCode": null,
      "wordDisplayMorphCode": null,
      "wordTypeCodes": null,
      "prefixoid": false,
      "suffixoid": false,
      "foreign": false,
      "lexemeId": 2054536,
      "meaningId": 557984,
      "datasetName": "EKI ühendsõnastik 2020",
      "datasetCode": "sss",
      "level1": 1,
      "level2": 1,
      "levels": "1",
      "lexemeValueStateCode": null,
      "lexemeFrequencyGroupCode": "F0",
      "lexemeFrequencies": null,
      "tags": [
        "süno valmis"
      ],
      "complexity": "ANY",
      "weight": 1.0,
      "wordTypes": null,
      "pos": [
        {
          "name": "POS",
          "code": "adj",
          "value": "adjektiiv, omadussõna"
        }
      ],
      "derivs": null,
      "registers": null,
      "governments": [],
      "grammars": [],
      ..........
      "lexemeOrMeaningClassifiersExist": true,
      "public": true
    },

     */
}
