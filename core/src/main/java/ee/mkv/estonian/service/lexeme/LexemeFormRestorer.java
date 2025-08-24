package ee.mkv.estonian.service.lexeme;

import ee.mkv.estonian.domain.Form;
import ee.mkv.estonian.domain.FormTypeCombination;
import ee.mkv.estonian.domain.PartOfSpeech;
import ee.mkv.estonian.domain.Representation;
import ee.mkv.estonian.model.EkiPartOfSpeech;
import ee.mkv.estonian.model.FormTypeCombinationEnum;
import ee.mkv.estonian.repository.*;
import ee.mkv.estonian.service.paradigm.ParadigmRestorer;
import ee.mkv.estonian.utils.IterableUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LexemeFormRestorer implements InitializingBean {

    private final FormRepository formRepository;
    private final Collection<ParadigmRestorer> paradigmRestorers;
    private final FormTypeCombinationRepository formTypeCombinationRepository;
    private final RepresentationRepository representationRepository;
    private final LexemeRepository lexemeRepository;
    private final PartOfSpeechRepository partOfSpeechRepository;
    private FormTypeCombination myFormTypeCombination;
    private PartOfSpeech myPartOfSpeech;

    @Transactional
    public void restoreLexemeForms(String word) {
        log.info("Restoring lexeme forms for a word: {}", word);

        var lexeme = IterableUtils.iterableToList(lexemeRepository.findByLemmaRepresentationIn(Set.of(word)))
                .stream()
                .peek(l -> log.info("Found lexeme: {}", l))
                .filter(l -> l.getPartOfSpeech().equals(myPartOfSpeech))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No lexeme found for word " + word));

        var pos = EkiPartOfSpeech.fromEkiCodes(lexeme.getPartOfSpeech().getEkiCodes())
                .orElseThrow(() -> new IllegalStateException("No POS found for word " + lexeme.getLemma()));

        ParadigmRestorer paradigmRestorer = paradigmRestorers.stream()
                .filter(r -> r.isMyParadigm(lexeme.getLemma().getRepresentation(), pos))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No restorer found for word " + lexeme));

        var forms = lexeme.getForms();
        var formCodes = forms.stream().map(f -> f.getFormTypeCombination().getEkiRepresentation()).collect(Collectors.toSet());
        if (formCodes.contains("RSgG")) {
            log.info("Found RSgG form for a word: {}, nothing to restore", lexeme);
        } else {
            var restoredParadigm = paradigmRestorer.restoreParadigm(lexeme.getLemma().getRepresentation());
            var representations = restoredParadigm.get(FormTypeCombinationEnum.SINGULAR_GENITIVE_REDUCED);
            for (var repr : representations) {
                var addedForm = new Form();
                addedForm.setLexeme(lexeme);
                addedForm.setFormTypeCombination(myFormTypeCombination);
                var representation = buildOrFindRepresentation(repr);
                addedForm.setRepresentation(representation);
                addedForm.setDeclinationTypes(paradigmRestorer.getInflectionType());
                formRepository.save(addedForm);
                log.info("Added form: {}", addedForm);
            }
        }

    }

    private Representation buildOrFindRepresentation(String repr) {
        return representationRepository.findByRepresentation(repr)
                .orElseGet(() -> {
                    var representation = new Representation();
                    representation.setRepresentation(repr);
                    return representationRepository.save(representation);
                });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        formTypeCombinationRepository.findByEkiRepresentation("RSgG").ifPresentOrElse(
                ftc -> myFormTypeCombination = ftc,
                () -> {
                    log.error("Form type combination RSgG not found");
                    throw new IllegalStateException("Form type combination RSgG not found");
                }
        );

        partOfSpeechRepository.findByPartOfSpeechName("Adjective").ifPresentOrElse(
                pos -> myPartOfSpeech = pos,
                () -> {
                    log.error("Part of speech N not found");
                    throw new IllegalStateException("Part of speech N not found");
                }
        );
    }
}
