package ee.mkv.estonian.split;

import ee.mkv.estonian.domain.*;
import ee.mkv.estonian.model.InternalPartOfSpeech;
import ee.mkv.estonian.repository.FormRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static ee.mkv.estonian.split.SplitUtils.getCompoundWord;

@Component
@RequiredArgsConstructor
@Slf4j
public class DerivedAdjectiveFromNounSplitter implements LexemeSplitter {
    private static final Set<String> SUPPORTED_SUFFIXES = Set.of("line", "lane", "lik");

    private final FormRepository formRepository;

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public Optional<CompoundWord> trySplitLexeme(Lexeme lexeme) {
        log.info("Trying to split lexeme {} with {}", lexeme, this.getClass().getSimpleName());
        return processName(lexeme);
    }

    @Override
    public boolean canProcess(Lexeme lexeme) {
        return lexeme.isName() && endsOnSupportedSuffix(lexeme).isPresent();
    }

    private boolean isNoun(PartOfSpeech partOfSpeech) {
        return getInternalPartOfSpeech(partOfSpeech) == InternalPartOfSpeech.NOUN;
    }

    private Optional<CompoundWord> processName(Lexeme lexeme) {
        return endsOnSupportedSuffix(lexeme)
                .flatMap(suffix -> buildCompoundWord(lexeme, suffix));
    }

    private Optional<CompoundWord> buildCompoundWord(Lexeme lexeme, String suffix) {
        log.info("Building compound word for lexeme {} with suffix {}", lexeme, suffix);
        String representation = lexeme.getLemma().getRepresentation();
        String base = representation.substring(0, representation.length() - suffix.length());

        var forms = formRepository.findWhereRepresentationIn(Set.of(base));
        log.info("Looking for forms for base {}, found [{}]", base, forms);
        return forms
                .stream()
                .peek(form -> log.info("Considering form {} for lexeme {}", form, lexeme))
                .filter(form -> isNoun(form.getLexeme().getPartOfSpeech()))
                .peek(form -> log.info("Lexeme {} is Noun", lexeme))
                .filter(SplitUtils::canBeNonLastComponentOfName)
                .peek(form -> log.info("Form {} can be non-last component of name", form))
                .findFirst()
                .map(form -> getCompoundWord(lexeme, form, CompoundRule.DERIVED_FROM_NAME_WITH_SUFFIX));
    }

    private Optional<String> endsOnSupportedSuffix(Lexeme lexeme) {
        var suffixHolder = SUPPORTED_SUFFIXES.stream()
                .filter(suffix -> lexeme.getLemma().getRepresentation().endsWith(suffix))
                .findFirst();
        if (suffixHolder.isEmpty()) {
            log.info("Lexeme {} does not end on any of my suffixes {}", lexeme, SUPPORTED_SUFFIXES);
        } else {
            log.info("Lexeme {} ends on my suffix {}", lexeme, suffixHolder.get());
        }
        return suffixHolder;
    }

    private static InternalPartOfSpeech getInternalPartOfSpeech(PartOfSpeech partOfSpeech) {
        return InternalPartOfSpeech.fromEkiCodes(partOfSpeech.getEkiCodes());
    }
}
