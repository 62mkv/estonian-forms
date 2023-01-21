package ee.mkv.estonian.service;

import ee.mkv.estonian.domain.*;
import ee.mkv.estonian.dto.FormForLexeme;
import ee.mkv.estonian.repository.ArticleRepository;
import ee.mkv.estonian.repository.FormRepository;
import ee.mkv.estonian.repository.LexemeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LexemeFromArticleService {
    private final ArticleRepository articleRepository;
    private final FormRepository formRepository;
    private final LexemeRepository lexemeRepository;

    public LexemeFromArticleService(ArticleRepository articleRepository, FormRepository formRepository, LexemeRepository lexemeRepository) {
        this.articleRepository = articleRepository;
        this.formRepository = formRepository;
        this.lexemeRepository = lexemeRepository;
    }

    private List<Lexeme> createLexemesFromArticles(List<Article> articles) {
        List<Lexeme> lexemes = new ArrayList<>();
        if (articles.size() == 1) {
            Article article = articles.get(0);
            for (PartOfSpeech partOfSpeech : article.getPartOfSpeech()) {
                Lexeme lexeme = new Lexeme();
                lexeme.setLemma(article.getBaseForm());
                lexeme.setPartOfSpeech(partOfSpeech);
                ;
                lexeme.setForms(createFormsForLexeme(lexeme, article));
                Set<Article> articleSet = new HashSet<>();
                articleSet.add(article);
//                lexeme.setArticles(articleSet);
                lexemeRepository.save(lexeme);
                lexemes.add(lexeme);
            }
        } else {
            log.warn("More than article is found, not sure how to handle this");
            return Collections.emptyList();
        }

        return lexemes;
    }

    private Set<Form> createFormsForLexeme(Lexeme lexeme, Article article) {
        Set<Form> forms = new HashSet<>();
        Map<FormForLexeme, Set<ArticleForm>> formDict = new HashMap<>();

        for (ArticleForm articleForm : article.getForms()) {

            final FormForLexeme key = FormForLexeme.fromArticleForm(articleForm);
            if (formDict.containsKey(key)) {
                formDict.get(key).add(articleForm);
            } else {
                Set<ArticleForm> formsForKey = new HashSet<>();
                formsForKey.add(articleForm);
                formDict.put(key, formsForKey);
            }
        }

        for (FormForLexeme key : formDict.keySet()) {
            Form form = new Form();
            List<ArticleForm> articleForms = new ArrayList<>(formDict.get(key));
            ArticleForm articleForm = articleForms.get(0);
            form.setFormTypeCombination(articleForm.getFormTypeCombination());
            form.setRepresentation(articleForm.getRepresentation());

            String declinationTypes = articleForms.stream()
                    .map(ArticleForm::getDeclinationType)
                    .map(i -> Integer.toString(i))
                    .collect(Collectors.joining(","));

            form.setDeclinationTypes(declinationTypes);
            form.setLexeme(lexeme);
            if (forms.add(form)) {
                formRepository.save(form);
            }
        }
        return forms;
    }

}
