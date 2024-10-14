package ee.mkv.estonian.ekilex.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParadigmDto {
    private Long wordId;
    private String inflectionType;
    private List<FormDto> forms;
    /*
    "wordId": 261443,
        "example": null,
        "inflectionTypeNr": "23",
        "inflectionType": "23u",
        "secondary": false,
        "forms": [
            {
                "mode": "WORD",
                "morphGroup1": "ainsus",
                "morphGroup2": null,
                "morphGroup3": null,
                "displayLevel": 1,
                "morphCode": "SgN",
                "morphExists": true,
                "value": "õun",
                "valuePrese": "õun",
                "components": [
                    "õun"
                ],
                "displayForm": "`õun",
                "vocalForm": null,
                "audioFile": "h_22211.mp3",
                "orderBy": 2
            },
            {
                "mode": "FORM",
                "morphGroup1": "ainsus",
                "morphGroup2": null,
                "morphGroup3": null,
                "displayLevel": 1,
                "morphCode": "SgG",
                "morphExists": true,
                "value": "õuna",
                "valuePrese": "õuna",
                "components": [
                    "õuna"
                ],
                "displayForm": "õuna",
                "vocalForm": null,
                "audioFile": "h_22212.mp3",
                "orderBy": 3
            }
     */
}
