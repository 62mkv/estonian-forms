package ee.mkv.estonian.ekilex.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WordDto {
    private Long wordId;
    private String wordValue;
    private String lang;
    private String wordClass;
    private Integer homonymNr;

/*
            "wordId": 261443,
            "wordValue": "õun",
            "wordValuePrese": "õun",
            "vocalForm": null,
            "homonymNr": 1,
            "lang": "est",
            "wordClass": "noomen",
            "genderCode": null,
            "aspectCode": null,
            "morphCode": null,
            "displayMorphCode": null,
            "wordTypeCodes": null,
            "wordTypes": null,
            "prefixoid": false,
            "suffixoid": false,
            "foreign": false,
            "lexemesArePublic": null,
            "lexemesValueStateCodes": [],
            "lexemesTagNames": [],
            "datasetCodes": [
                "ait",
                "ety",
                "gal",
                "les",
                "sss"
            ],
            "notes": null,
            "relations": null,
            "groups": null,
            "etymology": null,
            "odWordRecommendations": null
 */
}
