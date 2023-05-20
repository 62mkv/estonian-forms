package ee.mkv.estonian.service.paradigm;

import ee.mkv.estonian.model.FormTypeCombinationEnum;
import ee.mkv.estonian.model.PartOfSpeechEnum;

import java.util.List;
import java.util.Map;

public interface ParadigmRestorer {
    Map<FormTypeCombinationEnum, List<String>> restoreParadigm(String baseForm);

    boolean isMyParadigm(String baseForm, PartOfSpeechEnum partOfSpeech);

    String getInflectionType();
}
