package ee.mkv.estonian.ekilex.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DetailsParadigmDto {
    private Long paradigmId;
    private String inflectionTypeNr;
    private List<FormDto> forms;
    private Boolean formsExist;
    /*
     *
     *       "paradigmId": 180542,
      "inflectionTypeNr": "2",
      "forms": [
        {
          "id": 5967199,
          "value": "ilus",
          "mode": "WORD",
          "components": [            "ilus"          ],
          "displayForm": "ilus",
          "vocalForm": null,
          "morphCode": "SgN",
          "morphValue": "ainsuse nimetav",
          "formFrequencies": [            "enc17-formfreq - 601 - 161.8040716"          ]
        },
        {
          "id": 5967200,
          "value": "ilusa",
          "mode": "FORM",
          "components": [            "ilusa"          ],
          "displayForm": "ilusa",
          "vocalForm": null,
          "morphCode": "SgG",
          "morphValue": "ainsuse omastav",
          "formFrequencies": [            "enc17-formfreq - 3153 - 33.2184950"          ]
        }...
       ],
      "formsExist": true

     */
}
