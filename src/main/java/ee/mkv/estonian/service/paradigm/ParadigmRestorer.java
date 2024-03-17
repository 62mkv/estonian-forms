package ee.mkv.estonian.service.paradigm;

import ee.mkv.estonian.model.EkiPartOfSpeech;
import ee.mkv.estonian.model.FormTypeCombinationEnum;

import java.util.List;
import java.util.Map;

public interface ParadigmRestorer {
    Map<FormTypeCombinationEnum, List<String>> restoreParadigm(String baseForm);

    boolean isMyParadigm(String baseForm, EkiPartOfSpeech partOfSpeech);

    String getInflectionType();
}
