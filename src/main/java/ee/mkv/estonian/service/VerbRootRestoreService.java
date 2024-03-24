package ee.mkv.estonian.service;

import ee.mkv.estonian.domain.Lexeme;
import ee.mkv.estonian.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static ee.mkv.estonian.domain.Constants.VERB_SUPINE_ROOT;

@Component
@RequiredArgsConstructor
@Slf4j
public class VerbRootRestoreService {

    private final LexemeRepository lexemeRepository;
    private final FormRepository formRepository;
    private final PartOfSpeechRepository partOfSpeechRepository;
    private final FormTypeCombinationRepository formTypeCombinationRepository;
    private final RepresentationRepository representationRepository;
    private final FormService formService;

    public void restoreVerbRoots(String lemma) {
        var verbPoS = partOfSpeechRepository.findByPartOfSpeech("Verb").orElseThrow();
        var verbSupineRoot = formTypeCombinationRepository.findByEkiRepresentation(VERB_SUPINE_ROOT).orElseThrow();
        var representation = representationRepository.findByRepresentation(lemma).orElseThrow();
        List<Lexeme> lexemes = lexemeRepository.findByLemmaAndPartOfSpeech(representation, verbPoS);

        for (Lexeme lexeme : lexemes) {
            log.info("Restoring verb root for {}", lexeme.getLemma().getRepresentation());
            // restore verb root
            final String rep = lexeme.getLemma().getRepresentation();
            String base = rep.substring(0, rep.length() - 2);
            var rootFormExists = formRepository.findByLexeme(lexeme)
                    .stream()
                    .anyMatch(form -> form.getFormTypeCombination().getEkiRepresentation().equals(VERB_SUPINE_ROOT));

            if (!rootFormExists) {
                formService.createAndSave(lexeme, base, verbSupineRoot, null);
            }
        }
    }
}
