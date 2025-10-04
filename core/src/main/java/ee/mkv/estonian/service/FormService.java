package ee.mkv.estonian.service;

import ee.mkv.estonian.domain.Form;
import ee.mkv.estonian.domain.FormTypeCombination;
import ee.mkv.estonian.domain.Lexeme;
import ee.mkv.estonian.model.FormTypeCombinationEnum;
import ee.mkv.estonian.repository.FormRepository;
import ee.mkv.estonian.repository.LexemeRepository;
import ee.mkv.estonian.service.representation.RepresentationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FormService {

    private final FormRepository formRepository;
    private final RepresentationService representationService;
    private final LexemeRepository lexemeRepository;
    private final FormTypeCombinationService formTypeCombinationService;

    public Form createAndSave(Lexeme lexeme, String representation, FormTypeCombination formTypeCombination, String declinationTypes) {
        var form = new Form();
        form.setLexeme(lexeme);
        form.setRepresentation(representationService.findOrCreate(representation));
        form.setFormTypeCombination(formTypeCombination);
        form.setDeclinationTypes(declinationTypes);
        return formRepository.save(form);
    }

    public void addReducedForm(String lemma, String reducedForm, FormTypeCombinationEnum formTypeCombinationEnum) {
        var representation = representationService.findOrCreate(lemma);
        var lexemes = lexemeRepository.findByLemma(representation);
        if (lexemes.size() == 1) {
            var lexeme = lexemes.get(0);
            var formTypeCombination = formTypeCombinationService.findFormTypeCombination(formTypeCombinationEnum);
            var form = new Form();
            var formRepresentation = representationService.findOrCreate(reducedForm);
            form.setLexeme(lexeme);
            form.setRepresentation(formRepresentation);
            form.setFormTypeCombination(formTypeCombination);
            formRepository.save(form);
        } else {
            throw new IllegalStateException("Expected exactly one lexeme for lemma " + lemma + ", but found " + lexemes.size());
        }
    }
}

