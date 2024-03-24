package ee.mkv.estonian.service;

import ee.mkv.estonian.domain.Form;
import ee.mkv.estonian.domain.FormTypeCombination;
import ee.mkv.estonian.domain.Lexeme;
import ee.mkv.estonian.repository.FormRepository;
import ee.mkv.estonian.service.representation.RepresentationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class FormService {

    private final FormRepository formRepository;
    private final RepresentationService representationService;


    @Transactional
    public Form createAndSave(Lexeme lexeme, String representation, FormTypeCombination formTypeCombination, String declinationTypes) {
        var form = new Form();
        form.setLexeme(lexeme);
        form.setRepresentation(representationService.findOrCreate(representation));
        form.setFormTypeCombination(formTypeCombination);
        form.setDeclinationTypes(declinationTypes);
        return formRepository.save(form);
    }
}

