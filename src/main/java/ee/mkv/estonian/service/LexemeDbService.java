package ee.mkv.estonian.service;

import ee.mkv.estonian.domain.*;
import ee.mkv.estonian.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LexemeDbService {
    private final LexemeRepository lexemeRepository;
    private final PartOfSpeechRepository partOfSpeechRepository;
    private final RepresentationsRepository representationsRepository;
    private final ArticleRepository articleRepository;
    private final FormRepository formRepository;

    public LexemeDbService(LexemeRepository lexemeRepository, PartOfSpeechRepository partOfSpeechRepository, RepresentationsRepository representationsRepository, ArticleRepository articleRepository, FormRepository formRepository) {
        this.lexemeRepository = lexemeRepository;
        this.partOfSpeechRepository = partOfSpeechRepository;
        this.representationsRepository = representationsRepository;
        this.articleRepository = articleRepository;
        this.formRepository = formRepository;
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
                .collect(Collectors.toList());
    }

    private List<Lexeme> getLexemes(String lemma, String partOfSpeech, boolean recursive) {
        // try to find a lexeme in Db for our parameters
        final List<Lexeme> lexemeList = partOfSpeechRepository
                .findByPartOfSpeech(partOfSpeech)
                .flatMap(pos -> representationsRepository
                        .findByRepresentation(lemma)
                        .map(lemmaEntity -> lexemeRepository.findByLemmaAndPartOfSpeech(lemmaEntity, pos)))
                .orElse(Collections.emptyList());

        if (lexemeList.isEmpty() && !recursive) {
            log.warn("Not found lexeme, will try to create them");
            return createLexemes(lemma, partOfSpeech);
        }

        lexemeList.forEach(lexeme -> lexeme.getForms().forEach(form -> form.getFormTypeCombination()));

        return lexemeList;
    }

    private List<Lexeme> createLexemes(String lemma, String partOfSpeech) {
        return partOfSpeechRepository
                .findByPartOfSpeech(partOfSpeech)
                .flatMap(pos -> representationsRepository
                        .findByRepresentation(lemma)
                        .map(lemmaEntity -> articleRepository.findByBaseFormAndPartOfSpeech(lemmaEntity, pos)))
                .map(this::createLexemesFromArticles)
                .orElse(Collections.emptyList());
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
                lexeme.setArticles(articleSet);
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
        for (ArticleForm articleForm : article.getForms()) {
            Form form = new Form();
            form.setDeclinationType(articleForm.getDeclinationType());
            form.setFormTypeCombination(articleForm.getFormTypeCombination());
            form.setRepresentation(articleForm.getRepresentation());
            form.setStemLength(articleForm.getStemLength());
            form.setLexeme(lexeme);
            formRepository.save(form);
            forms.add(form);
        }
        return forms;
    }

    public void updateWikidataIdOnLexeme(Lexeme lexeme, String id) {
        lexeme.setWikidataId(id);
        lexemeRepository.save(lexeme);
    }
}
