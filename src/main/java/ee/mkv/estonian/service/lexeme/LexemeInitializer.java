package ee.mkv.estonian.service.lexeme;

import ee.mkv.estonian.domain.*;
import ee.mkv.estonian.model.EkiPartOfSpeech;
import ee.mkv.estonian.model.FormTypeCombinationEnum;
import ee.mkv.estonian.model.InternalPartOfSpeech;
import ee.mkv.estonian.repository.FormRepository;
import ee.mkv.estonian.repository.FormTypeCombinationRepository;
import ee.mkv.estonian.repository.LexemeRepository;
import ee.mkv.estonian.repository.PartOfSpeechRepository;
import ee.mkv.estonian.service.paradigm.ParadigmRestorer;
import ee.mkv.estonian.service.representation.RepresentationService;
import ee.mkv.estonian.utils.IterableUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class LexemeInitializer implements InitializingBean {

    private final static Map<FormTypeCombinationEnum, FormTypeCombination> FTC_MAP = new EnumMap<>(FormTypeCombinationEnum.class);
    private final Collection<ParadigmRestorer> paradigmRestorers;
    private final LexemeRepository lexemeRepository;
    private final RepresentationService representationService;
    private final PartOfSpeechRepository partOfSpeechRepository;
    private final FormRepository formRepository;
    private final FormTypeCombinationRepository formTypeCombinationRepository;

    public void initializeLexeme(String lemma, InternalPartOfSpeech internalPartOfSpeech) {
        var ekiPartOfSpeech = EkiPartOfSpeech.fromEkiCodes(internalPartOfSpeech.getEkiCodes())
                .orElseThrow();

        var restorer = paradigmRestorers.stream()
                .filter(r -> r.isMyParadigm(lemma, ekiPartOfSpeech))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No restorer found for " + lemma + internalPartOfSpeech.name()));

        var rep = representationService.findOrCreate(lemma);
        PartOfSpeech partOfSpeech = getByEkiCode(internalPartOfSpeech);
        IterableUtils.getSingleValueIfExists(lemma, lexemeRepository.findByLemmaAndPartOfSpeech(rep, partOfSpeech))
                .ifPresentOrElse(lexeme -> proceedWithExistingLexeme(lexeme, restorer),
                        () -> createLexeme(rep, partOfSpeech, restorer));
    }

    private void createLexeme(Representation rep, PartOfSpeech partOfSpeech, ParadigmRestorer restorer) {
        var lexeme = new Lexeme();
        lexeme.setLemma(rep);
        lexeme.setPartOfSpeech(partOfSpeech);
        lexemeRepository.save(lexeme);
        proceedWithExistingLexeme(lexeme, restorer);
    }

    private void proceedWithExistingLexeme(Lexeme lexeme, ParadigmRestorer paradigmRestorer) {
        var existingForms = formRepository.findByLexeme(lexeme);
        var restoredForms = paradigmRestorer.restoreParadigm(lexeme.getLemma().getRepresentation());
        for (Map.Entry<FormTypeCombinationEnum, List<String>> entry : restoredForms.entrySet()) {
            var formTypeCombinationEnum = entry.getKey();
            for (String form : entry.getValue()) {
                if (!formExists(existingForms, formTypeCombinationEnum, form)) {
                    log.info("Form {}:{} not found, creating", form, formTypeCombinationEnum);
                    // create new form from restored
                    var newForm = new Form();
                    newForm.setLexeme(lexeme);
                    newForm.setFormTypeCombination(FTC_MAP.get(formTypeCombinationEnum));
                    newForm.setRepresentation(representationService.findOrCreate(form));
                    newForm.setDeclinationTypes(paradigmRestorer.getInflectionType());
                    formRepository.save(newForm);
                }
            }
        }
    }

    private boolean formExists(List<Form> existingForms, FormTypeCombinationEnum formTypeCombinationEnum, String needle) {
        for (Form form : existingForms) {
            if (form.getFormTypeCombination().getEkiRepresentation().equals(formTypeCombinationEnum.getEkiRepresentation())
                    && form.getRepresentation().getRepresentation().equals(needle)) {
                return true;
            }
        }
        return false;
    }

    private PartOfSpeech getByEkiCode(InternalPartOfSpeech partOfSpeech) {
        return IterableUtils.getUniqueValue(partOfSpeech.name(), partOfSpeechRepository.findByEkiCode(partOfSpeech.getEkiCodes()));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for (FormTypeCombination ftc : formTypeCombinationRepository.findAll()) {
            try {
                var key = FormTypeCombinationEnum.fromEkiRepresentation(ftc.getEkiRepresentation());
                FTC_MAP.put(key, ftc);
            } catch (IllegalArgumentException ignored) {
            }
        }
    }
}
