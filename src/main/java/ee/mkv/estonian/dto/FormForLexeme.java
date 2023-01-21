package ee.mkv.estonian.dto;

import ee.mkv.estonian.domain.ArticleForm;
import ee.mkv.estonian.domain.EkilexForm;
import lombok.Data;

@Data
public class FormForLexeme {
    private final String representation;
    private final String features;

    public static FormForLexeme fromArticleForm(ArticleForm articleForm) {
        return new FormForLexeme(
                articleForm.getRepresentation().getRepresentation(),
                articleForm.getFormTypeCombination().getEkiRepresentation()
        );
    }

    public static FormForLexeme fromEkilexForm(EkilexForm ekilexForm) {
        return new FormForLexeme(
                ekilexForm.getRepresentation().getRepresentation(),
                ekilexForm.getFormTypeCombination().getEkiRepresentation()
        );
    }
}
