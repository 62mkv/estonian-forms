package ee.mkv.estonian.ekilex.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FormDto {
    private String morphCode;
    private boolean morphExists;
    private String value;
    /*
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
     */
}
