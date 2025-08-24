package ee.mkv.estonian.service;

import ee.mkv.estonian.domain.Form;
import ee.mkv.estonian.domain.Lexeme;
import ee.mkv.estonian.repository.LexemeRepository;
import ee.mkv.estonian.repository.PartOfSpeechRepository;
import ee.mkv.estonian.repository.RepresentationRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class LexemeDbService {
    private final LexemeRepository lexemeRepository;
    private final PartOfSpeechRepository partOfSpeechRepository;
    private final RepresentationRepository representationsRepository;

    public LexemeDbService(LexemeRepository lexemeRepository, PartOfSpeechRepository partOfSpeechRepository, RepresentationRepository representationsRepository) {
        this.lexemeRepository = lexemeRepository;
        this.partOfSpeechRepository = partOfSpeechRepository;
        this.representationsRepository = representationsRepository;
    }

    @Transactional
    public List<Lexeme> getLexemes(String lemma, String partOfSpeech) {
        return getLexemes(lemma, partOfSpeech, false)
                .stream()
                .map(lexeme -> {
                    lexeme.getForms().size();
                    lexeme.getForms()
                            .forEach(form -> form.getFormTypeCombination().getFormTypes().forEach(formType -> form.getFormTypeCombination().getFormTypes()));
                    return lexeme;
                })
                .toList();
    }

    private List<Lexeme> getLexemes(String lemma, String partOfSpeech, boolean recursive) {
        // try to find a lexeme in Db for our parameters
        final List<Lexeme> lexemeList = partOfSpeechRepository
                .findByPartOfSpeechName(partOfSpeech)
                .flatMap(pos -> representationsRepository
                        .findByRepresentation(lemma)
                        .map(lemmaEntity -> lexemeRepository.findByLemmaAndPartOfSpeech(lemmaEntity, pos)))
                .orElse(Collections.emptyList());

        lexemeList.forEach(lexeme -> lexeme.getForms().forEach(Form::getFormTypeCombination));

        return lexemeList;
    }

    public void updateWikidataIdOnLexeme(Lexeme lexeme, String id) {
        lexeme.setWikidataId(id);
        lexemeRepository.save(lexeme);
    }

}
