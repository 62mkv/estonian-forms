package ee.mkv.estonian.service.paradigm;

import ee.mkv.estonian.domain.EkilexForm;
import ee.mkv.estonian.domain.EkilexParadigm;
import ee.mkv.estonian.domain.FormTypeCombination;
import ee.mkv.estonian.domain.Representation;
import ee.mkv.estonian.model.FormTypeCombinationEnum;
import ee.mkv.estonian.model.PartOfSpeechEnum;
import ee.mkv.estonian.repository.*;
import ee.mkv.estonian.utils.IterableUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ManualParadigmRestoreService {

    private final EkilexFormRepository ekilexFormRepository;
    private final FormTypeCombinationRepository formTypeCombinationRepository;
    private final List<ParadigmRestorer> paradigmRestorers;
    private final EkilexWordRepository ekilexWordRepository;
    private final EkilexParadigmRepository ekilexParadigmRepository;
    private final RepresentationRepository representationsRepository;

    @Transactional
    public void restoreParadigm(String word) {
        log.info("Restoring paradigm for a word: {}", word);
        var paradigmRestorerOptional = paradigmRestorers.stream()
                .filter(r -> r.isMyParadigm(word, PartOfSpeechEnum.NOUN))
                .findFirst();

        if (paradigmRestorerOptional.isEmpty()) {
            log.error("Not a single restorer was found for noun {}", word);
            throw new IllegalStateException("No restorer found for noun " + word);
        }

        var restorer = paradigmRestorerOptional.get();

        var words = IterableUtils.iterableToList(ekilexWordRepository.findAllByBaseFormRepresentation(word));

        if (words.isEmpty()) {
            log.error("No EkiLex word found for this base form: {}", word);
            throw new IllegalStateException("No EkiLex word found for this base form: " + word);
        }

        if (words.size() > 1) {
            log.error("More than one EkiLex word found for this base form: {}", word);
            throw new IllegalStateException("More than one EkiLex word found for this base form: " + word);
        }

        var ekilexWord = words.get(0);

        var paradigms = IterableUtils.iterableToList(ekilexParadigmRepository.findAllByWordId(ekilexWord.getId()));

        if (!paradigms.isEmpty()) {
            log.warn("There're already EkiLex paradigms for word id {}", ekilexWord.getId());
            if (paradigms.size() > 1) {
                log.error("There's more than 1 EkiLex paradigms for word id {}: {}", ekilexWord.getId(), paradigms);
                throw new IllegalStateException("There's more than 1 EkiLex paradigms for word id " + ekilexWord.getId());
            }

            EkilexParadigm paradigm = paradigms.get(0);
            // TODO refactor to re-use existing paradigm
            var existingForms = paradigm.getForms()
                    .stream()
                    .collect(Collectors.toMap(f -> toEnum(f.getFormTypeCombination()), f -> f.getRepresentation().getRepresentation()));
            final Map<FormTypeCombinationEnum, List<String>> formsForNewParadigm = restorer.restoreParadigm(word);

            for (FormTypeCombinationEnum formTypeCombination : existingForms.keySet()) {
                formsForNewParadigm.remove(formTypeCombination);
            }

            for (Map.Entry<FormTypeCombinationEnum, List<String>> entry : formsForNewParadigm.entrySet()) {
                for (String representation : entry.getValue()) {
                    EkilexForm ekilexForm = new EkilexForm();
                    ekilexForm.setRepresentation(buildRepresentation(representation));
                    ekilexForm.setFormTypeCombination(buildFtc(entry.getKey()));
                    ekilexForm.setEkilexParadigm(paradigm);
                    ekilexFormRepository.save(ekilexForm);
                    log.info("Saved restored form for existing paradigm: {}", ekilexForm);
                }
            }

        } else {

            final Map<FormTypeCombinationEnum, List<String>> formsForNewParadigm = restorer.restoreParadigm(word);

            log.info("Here's restored paradigm: {}", formsForNewParadigm);

            EkilexParadigm newParadigm = new EkilexParadigm();
            newParadigm.setWord(ekilexWord);
            final List<EkilexForm> forms = produceFormsFromRestoredParadigm(formsForNewParadigm);
            forms.forEach(form -> form.setEkilexParadigm(newParadigm));
            newParadigm.setForms(forms);
            newParadigm.setInflectionType(restorer.getInflectionType());
            log.info("Saved paradigm as: {}", ekilexParadigmRepository.save(newParadigm));
            ekilexFormRepository.saveAll(forms);
        }
    }

    private FormTypeCombinationEnum toEnum(FormTypeCombination formTypeCombination) {
        return FormTypeCombinationEnum.fromEkiRepresentation(formTypeCombination.getEkiRepresentation());
    }

    private List<EkilexForm> produceFormsFromRestoredParadigm(Map<FormTypeCombinationEnum, List<String>> paradigm) {
        List<EkilexForm> result = new ArrayList<>(paradigm.size() * 2);
        for (Map.Entry<FormTypeCombinationEnum, List<String>> entry : paradigm.entrySet()) {
            for (String representation : entry.getValue()) {
                result.add(buildEkilexForm(entry.getKey(), representation));
            }
        }
        return result;
    }

    private EkilexForm buildEkilexForm(FormTypeCombinationEnum formTypeCombination, String representation) {
        EkilexForm ekilexForm = new EkilexForm();
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
