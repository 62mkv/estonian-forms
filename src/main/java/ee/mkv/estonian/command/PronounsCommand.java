package ee.mkv.estonian.command;

import ee.mkv.estonian.domain.Article;
import ee.mkv.estonian.domain.PartOfSpeech;
import ee.mkv.estonian.repository.ArticleRepository;
import ee.mkv.estonian.repository.PartOfSpeechRepository;
import ee.mkv.estonian.service.FormFingerprintCalculator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Component
@CommandLine.Command(name = "pronouns")
@Slf4j
class PronounsCommand implements Runnable {

    public static final String PRONOUN = "Pronoun";
    private final ArticleRepository repository;
    private final FormFingerprintCalculator fingerprintCalculator;
    private final PartOfSpeechRepository partOfSpeechRepository;

    public PronounsCommand(ArticleRepository repository, FormFingerprintCalculator fingerprintCalculator, PartOfSpeechRepository partOfSpeechRepository) {
        this.repository = repository;
        this.fingerprintCalculator = fingerprintCalculator;
        this.partOfSpeechRepository = partOfSpeechRepository;
    }

    @Override
    @Transactional
    public void run() {
        log.info("Starting processing command 'pronoun'");
        PartOfSpeech pronoun = partOfSpeechRepository.findByPartOfSpeech(PRONOUN).orElseThrow(() -> new RuntimeException("Pronoun part of speech not found!"));
        List<Article> pronouns = repository.findByPartOfSpeech(pronoun)
                .stream()
                .filter(article -> article.getPartOfSpeech().size() == 1)
                .collect(Collectors.toList());

        LexemeCandidateMap lexemeCandidateMap = new LexemeCandidateMap();
        for (Article article : pronouns) {
            lexemeCandidateMap.add(article);
        }

        for (String fingerprint : lexemeCandidateMap.keys()) {
            final Collection<Article> articles = lexemeCandidateMap.getArticles(fingerprint);
            if (articles.size() > 1) {
                System.out.printf("Fingerprint: %s; articles: %s%n",
                        fingerprint,
                        articles
                                .stream()
                                .map(Article::getId)
                                .map(Object::toString)
                                .collect(Collectors.joining(","))
                );
            }
        }
        log.info("Finished processing command 'pronoun'");
    }

    class LexemeCandidateMap {
        private final Map<String, Collection<Article>> lexemeCandidates = new HashMap<>();

        public void add(Article article) {
            String formFingerprint = fingerprintCalculator.getFormFingerprint(article);

            if (StringUtils.isBlank(formFingerprint)) {
                return;
            }

            if (lexemeCandidates.containsKey(formFingerprint)) {
                lexemeCandidates.get(formFingerprint).add(article);
            } else {
                List<Article> articles = new ArrayList<>();
                articles.add(article);
                lexemeCandidates.put(formFingerprint, articles);
            }
        }

        public Collection<String> keys() {
            return lexemeCandidates.keySet();
        }

        public Collection<Article> getArticles(String fingerprint) {
            return lexemeCandidates.get(fingerprint);
        }
    }
}