package ee.mkv.estonian.command;

import ee.mkv.estonian.ekilex.EkiLexRetrievalService;
import ee.mkv.estonian.ekilex.dto.DetailsDto;
import ee.mkv.estonian.ekilex.dto.DetailsLexemeDto;
import ee.mkv.estonian.ekilex.dto.DetailsParadigmDto;
import ee.mkv.estonian.ekilex.dto.FormDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@CommandLine.Command(name = "ekilex")
@Slf4j
public class EkiLexCommand implements Runnable {
    private final EkiLexRetrievalService ekiLexRetrievalService;
    @CommandLine.Option(names = {"-l", "--lemma-list"})
    private String lemma;
    @CommandLine.Option(names = {"-p", "--partOfSpeech"})
    private String partOfSpeech;

    public EkiLexCommand(EkiLexRetrievalService ekiLexRetrievalService) {
        this.ekiLexRetrievalService = ekiLexRetrievalService;
    }

    @Override
    public void run() {
        for (Long id : ekiLexRetrievalService.findWords(lemma)) {
            DetailsDto detailsDto = ekiLexRetrievalService.getDetails(id);

            final Long wordId = detailsDto.getWord().getWordId();
            final List<DetailsParadigmDto> paradigms = wrapSafe(detailsDto.getParadigms());
            final List<DetailsLexemeDto> lexemes = wrapSafe(detailsDto.getLexemes());

            log.info("Word {} has {} paradigms:", wordId, paradigms.size());
            for (DetailsParadigmDto paradigm : paradigms) {
                log.info("\tParadigm id: {}, inflection type {}, forms exist: {}", paradigm.getParadigmId(), paradigm.getInflectionTypeNr(), paradigm.getFormsExist());
                for (FormDto form : wrapSafe(paradigm.getForms())) {
                    log.info("\t\t{}: {}", form.getMorphCode(), form.getValue());
                }
            }

            log.info("Word {} has {} lexemes", wordId, lexemes.size());
            for (DetailsLexemeDto lexeme : lexemes) {
                log.info("\tLexeme id: {}, homonym nr: {}, pos: {}", lexeme.getLexemeId(), lexeme.getHomonymNr(), lexeme.getPos());
            }
        }
    }

    private <T> List<T> wrapSafe(List<T> source) {
        if (Objects.nonNull(source)) {
            return source;
        }

        return new ArrayList<>();
    }
}
