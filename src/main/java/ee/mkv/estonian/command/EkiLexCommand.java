package ee.mkv.estonian.command;

import ee.mkv.estonian.dto.FormDto;
import ee.mkv.estonian.dto.ParadigmDto;
import ee.mkv.estonian.service.EkiLexRetrievalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

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
        for (Long id : ekiLexRetrievalService.findWords(lemma, partOfSpeech)) {
            log.info("Word id: {}", id);
            for (ParadigmDto paradigm : ekiLexRetrievalService.getParadigmById(id)) {
                for (FormDto form : paradigm.getForms()) {
                    log.info("Inflection type {}, morph. code {}: {}", paradigm.getInflectionType(), form.getMorphCode(), form.getValue());
                }
            }
        }
    }
}
