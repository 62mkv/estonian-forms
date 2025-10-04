package ee.mkv.estonian.service;

import ee.mkv.estonian.domain.FormTypeCombination;
import ee.mkv.estonian.model.FormTypeCombinationEnum;
import ee.mkv.estonian.repository.FormTypeCombinationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FormTypeCombinationService {

    private final FormTypeCombinationRepository formTypeCombinationRepository;

    public FormTypeCombination findFormTypeCombination(FormTypeCombinationEnum formTypeCombinationEnum) {
        return formTypeCombinationRepository.findByEkiRepresentation(formTypeCombinationEnum.getEkiRepresentation())
                .orElseThrow(() -> new IllegalStateException("FormTypeCombination not found for ekiRepresentation: " + formTypeCombinationEnum.getEkiRepresentation()));
    }
}
