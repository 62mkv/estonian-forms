package ee.mkv.estonian.ekilex;

import ee.mkv.estonian.domain.*;
import ee.mkv.estonian.ekilex.dto.*;
import ee.mkv.estonian.error.FormTypeCombinationNotFound;
import ee.mkv.estonian.error.LanguageNotSupportedException;
import ee.mkv.estonian.error.NotImplementedException;
import ee.mkv.estonian.error.PartOfSpeechNotFoundException;
import ee.mkv.estonian.model.PartOfSpeechEnum;
import ee.mkv.estonian.repository.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class EkiLexRetrievalService {

    private final EkiLexClient ekiLexClient;

    private final RepresentationsRepository representationsRepository;
    private final EkilexWordRepository wordRepository;
    private final EkilexLexemeRepository lexemeRepository;
    private final PartOfSpeechRepository partOfSpeechRepository;
    private final EkilexParadigmRepository paradigmRepository;
    private final EkilexFormRepository ekilexFormRepository;
    private final FormTypeCombinationRepository formTypeRepository;

    public EkiLexRetrievalService(EkiLexClient ekiLexClient, RepresentationsRepository representationsRepository, EkilexWordRepository wordRepository, EkilexLexemeRepository lexemeRepository, PartOfSpeechRepository partOfSpeechRepository, EkilexParadigmRepository paradigmRepository, EkilexFormRepository ekilexFormRepository, FormTypeCombinationRepository formTypeRepository) {
        this.ekiLexClient = ekiLexClient;
        this.representationsRepository = representationsRepository;
        this.wordRepository = wordRepository;
        this.lexemeRepository = lexemeRepository;
        this.partOfSpeechRepository = partOfSpeechRepository;
        this.paradigmRepository = paradigmRepository;
        this.ekilexFormRepository = ekilexFormRepository;
        this.formTypeRepository = formTypeRepository;
    }

    public List<EkilexWord> retrieveByLemma(String lemma, boolean forceOverwrite) {
        List<EkilexWord> result = new ArrayList<>();
        for (Long wordId : ekiLexClient.findWords(lemma)) {
            result.add(retrieveById(wordId, forceOverwrite));
        }

        return result;
    }

    public EkilexWord retrieveById(Long wordId, boolean forceOverwrite) {

        if (wordRepository.existsById(wordId)) {
            if (!forceOverwrite) {
                return wordRepository.findById(wordId).get();
            } else {
                throw new NotImplementedException("Force overwrite for existing EkiLex words not implemented yet!");
            }
        }

        DetailsDto detailsDto = ekiLexClient.getDetails(wordId);

        EkilexWord word = getEkilexWord(detailsDto.getWord());

        for (DetailsLexemeDto lexemeDto : detailsDto.getLexemes()) {
            saveEkiLexLexeme(word, lexemeDto);
        }

        for (DetailsParadigmDto paradigmDto : detailsDto.getParadigms()) {
            saveEkiLexParadigm(word, paradigmDto);
        }

        return word;
    }

    private EkilexWord getEkilexWord(WordDto wordDto) {
        if (!wordDto.getLang().equalsIgnoreCase("est")) {
            throw new LanguageNotSupportedException(wordDto.getLang());
        }
        EkilexWord word = new EkilexWord();
        word.setId(wordDto.getWordId());
        word.setBaseForm(getRepresentation(wordDto.getWordValue()));
        wordRepository.save(word);
        return word;
    }

    private void saveEkiLexParadigm(EkilexWord word, DetailsParadigmDto paradigmDto) {
        EkilexParadigm paradigm = new EkilexParadigm();
        paradigm.setWord(word);
        paradigm.setInflectionType(paradigmDto.getInflectionTypeNr());
        paradigmRepository.save(paradigm);
        for (FormDto formDto : paradigmDto.getForms()) {
            saveEkiLexForm(paradigm, formDto);
        }
    }

    private void saveEkiLexForm(EkilexParadigm paradigm, FormDto formDto) {
        EkilexForm form = new EkilexForm();
        form.setEkilexParadigm(paradigm);
        form.setRepresentation(getRepresentation(formDto.getValue()));
        form.setFormTypeCombination(getFormTypeCombination(formDto.getMorphCode()));
        ekilexFormRepository.save(form);
        paradigm.getForms().add(form);
    }

    private FormTypeCombination getFormTypeCombination(String morphCode) {
        return formTypeRepository.findByEkiRepresentation(morphCode)
                .orElseThrow(() -> new FormTypeCombinationNotFound(morphCode));
    }

    private void saveEkiLexLexeme(EkilexWord word, DetailsLexemeDto lexemeDto) {
        EkilexLexeme lexeme = new EkilexLexeme();
        lexeme.setWord(word);
        lexeme.setPos(getPos(lexemeDto.getPos()));
        lexemeRepository.save(lexeme);
    }

    private Set<PartOfSpeech> getPos(List<DetailsClassifierDto> posList) {
        Set<PartOfSpeech> result = new HashSet<>();
        for (DetailsClassifierDto classifierDto : posList) {
            if (classifierDto.getName().equalsIgnoreCase("POS")) {

                PartOfSpeechEnum posEnum = PartOfSpeechEnum.fromEkilexCode(classifierDto.getCode())
                        .orElseThrow(() -> new PartOfSpeechNotFoundException(classifierDto.getCode()));

                PartOfSpeech pos = partOfSpeechRepository.findByPartOfSpeech(posEnum.getRepresentation())
                        .orElseThrow(() -> new PartOfSpeechNotFoundException(posEnum.getRepresentation()));

                result.add(pos);
            }
        }
        return result;
    }

    private Representation getRepresentation(String word) {
        return representationsRepository.findByRepresentation(word)
                .orElseGet(() -> {
                    Representation representation = new Representation();
                    representation.setRepresentation(word);
                    return representationsRepository.save(representation);
                });
    }
}
