package ee.mkv.estonian.command.internal;

import com.kakawait.spring.boot.picocli.autoconfigure.ExitStatus;
import com.kakawait.spring.boot.picocli.autoconfigure.HelpAwarePicocliCommand;
import ee.mkv.estonian.domain.EkilexForm;
import ee.mkv.estonian.domain.EkilexParadigm;
import ee.mkv.estonian.domain.FormTypeCombination;
import ee.mkv.estonian.domain.Representation;
import ee.mkv.estonian.model.FormTypeCombinationEnum;
import ee.mkv.estonian.model.PartOfSpeechEnum;
import ee.mkv.estonian.repository.*;
import ee.mkv.estonian.service.paradigm.ParadigmRestorer;
import ee.mkv.estonian.utils.IterableUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@CommandLine.Command(name = "manual-restore-paradigm")
@RequiredArgsConstructor
@Slf4j
public class ManualRestoreParadigmCommand extends HelpAwarePicocliCommand {
    private final EkilexFormRepository ekilexFormRepository;
    private final FormTypeCombinationRepository formTypeCombinationRepository;
    private final List<ParadigmRestorer> paradigmRestorers;
    private final EkilexWordRepository ekilexWordRepository;
    private final EkilexParadigmRepository ekilexParadigmRepository;
    private final RepresentationsRepository representationsRepository;
    @CommandLine.Option(names = "-w")
    private String word;

    @Override
    public ExitStatus call() throws Exception {
        assert word != null;
        log.info("Restoring paradigm for a word: {}", word);
        var paradigmRestorerOptional = paradigmRestorers.stream()
                .filter(r -> r.isMyParadigm(word, PartOfSpeechEnum.NOUN))
                .findFirst();

        if (paradigmRestorerOptional.isEmpty()) {
            log.error("Not a single restorer was found for noun {}", word);
            return ExitStatus.TERMINATION;
        }

        var restorer = paradigmRestorerOptional.get();

        var words = IterableUtils.iterableToList(ekilexWordRepository.findAllByBaseFormRepresentation(word));

        if (words.isEmpty()) {
            log.error("No EkiLex word found for this base form: {}", word);
            return ExitStatus.TERMINATION;
        }

        if (words.size() > 1) {
            log.error("More than one EkiLex word found for this base form: {}", word);
            return ExitStatus.TERMINATION;
        }

        var ekilexWord = words.get(0);

        var paradigms = IterableUtils.iterableToList(ekilexParadigmRepository.findAllByWordId(ekilexWord.getId()));

        if (!paradigms.isEmpty()) {
            log.error("There're already EkiLex paradigms for word id {}", ekilexWord.getId());
            return ExitStatus.TERMINATION;
        }

        final Map<FormTypeCombinationEnum, List<String>> paradigm = restorer.restoreParadigm(word);
        log.info("Here's restored paradigm: {}", paradigm);

        EkilexParadigm newParadigm = new EkilexParadigm();
        newParadigm.setWord(ekilexWord);
        final List<EkilexForm> forms = produceFormsFromRestoredParadigm(paradigm, newParadigm);
        newParadigm.setForms(forms);
        newParadigm.setInflectionType(restorer.getInflectionType());
        log.info("Saved paradigm as: {}", ekilexParadigmRepository.save(newParadigm));
        ekilexFormRepository.saveAll(forms);
        return super.call();
    }

    private List<EkilexForm> produceFormsFromRestoredParadigm(Map<FormTypeCombinationEnum, List<String>> paradigm, EkilexParadigm parent) {
        List<EkilexForm> result = new ArrayList<>(paradigm.size() * 2);
        for (Map.Entry<FormTypeCombinationEnum, List<String>> entry : paradigm.entrySet()) {
            for (String representation : entry.getValue()) {
                result.add(buildEkilexForm(entry.getKey(), representation, parent));
            }
        }
        return result;
    }

    private EkilexForm buildEkilexForm(FormTypeCombinationEnum formTypeCombination, String representation, EkilexParadigm parent) {
        EkilexForm ekilexForm = new EkilexForm();
        ekilexForm.setEkilexParadigm(parent);
        ekilexForm.setRepresentation(buildRepresentation(representation));
        ekilexForm.setFormTypeCombination(buildFtc(formTypeCombination));
        return ekilexForm;
    }

    private FormTypeCombination buildFtc(FormTypeCombinationEnum formTypeCombinationEnum) {
        return formTypeCombinationRepository.findByEkiRepresentation(formTypeCombinationEnum.getEkiRepresentation()).get();
    }

    private Representation buildRepresentation(String word) {
        return representationsRepository.findByRepresentation(word)
                .orElseGet(() -> {
                    Representation representation = new Representation();
                    representation.setRepresentation(word);
                    return representationsRepository.save(representation);
                });
    }

}
