package ee.mkv.estonian.service.lexeme;

import ee.mkv.estonian.domain.*;
import ee.mkv.estonian.model.InternalPartOfSpeech;
import ee.mkv.estonian.repository.FormRepository;
import ee.mkv.estonian.repository.FormTypeCombinationRepository;
import ee.mkv.estonian.repository.LexemeRepository;
import ee.mkv.estonian.repository.PartOfSpeechRepository;
import ee.mkv.estonian.service.representation.RepresentationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
@Slf4j
public class ImmutableLexemeAdderService implements InitializingBean {

    private static final Set<InternalPartOfSpeech> SUPPORTED_POS = Set.of(InternalPartOfSpeech.PREFIX,
            InternalPartOfSpeech.NOUN, InternalPartOfSpeech.ADJECTIVE, InternalPartOfSpeech.VERB);
    private static final Map<InternalPartOfSpeech, PartOfSpeech> POS_MAPPING = new EnumMap(InternalPartOfSpeech.class);
    private final LexemeRepository lexemeRepository;
    private final FormRepository formRepository;
    private final RepresentationService representationRepository;
    private final PartOfSpeechRepository partOfSpeechRepository;
    private final FormTypeCombinationRepository formTypeCombinationRepository;
    private FormTypeCombination immutableFtc;
    private FormTypeCombination singularNominative;
    private FormTypeCombination supinRootFtc;

    public void addImmutableLexeme(String lemma, InternalPartOfSpeech partOfSpeech) {
        log.info("Adding immutable lexeme for lemma: {}, part of speech: {}", lemma, partOfSpeech);
        if (!SUPPORTED_POS.contains(partOfSpeech)) {
            throw new UnsupportedOperationException("Unsupported part of speech: " + partOfSpeech);
        }

        var rep = representationRepository.findOrCreate(lemma);
        var lexeme = new Lexeme();
        lexeme.setLemma(rep);
        lexeme.setPartOfSpeech(findPartOfSpeech(partOfSpeech));

        var form = new Form();
        form.setFormTypeCombination(getFtc(partOfSpeech));
        form.setRepresentation(rep);
        form.setLexeme(lexeme);

        lexemeRepository.save(lexeme);
        formRepository.save(form);
    }

    private FormTypeCombination getFtc(InternalPartOfSpeech partOfSpeech) {
        return switch (partOfSpeech) {
            case NOUN -> this.singularNominative;
            case ADJECTIVE -> this.singularNominative;
            case VERB -> this.supinRootFtc;
            case PREFIX -> this.immutableFtc;
            default -> throw new UnsupportedOperationException("Unsupported part of speech: " + partOfSpeech);
        };
    }

    private PartOfSpeech findPartOfSpeech(InternalPartOfSpeech partOfSpeech) {
        return Optional.ofNullable(POS_MAPPING.get(partOfSpeech))
                .orElseThrow(() -> new RuntimeException("Part of speech not found in mapping: " + partOfSpeech.getEkiCodes()));
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        for (PartOfSpeech partOfSpeech : partOfSpeechRepository.findAll()) {
            POS_MAPPING.put(InternalPartOfSpeech.fromEkiCodes(partOfSpeech.getEkiCodes()), partOfSpeech);
        }

        initializeFormTypeCombination(Constants.IMMUTABLE_FORM, ftc -> this.immutableFtc = ftc);
        initializeFormTypeCombination("SgN", ftc -> this.singularNominative = ftc);
        initializeFormTypeCombination("Sup", ftc -> this.supinRootFtc = ftc);
    }

    private void initializeFormTypeCombination(String ekiRepresentation, Consumer<FormTypeCombination> fieldInitializer) {
        formTypeCombinationRepository.findByEkiRepresentation(ekiRepresentation)
                .ifPresentOrElse(
                        fieldInitializer,
                        () -> {
                            throw new RuntimeException("No immutable form type combination found for immutable");
                        }
                );
    }

}
