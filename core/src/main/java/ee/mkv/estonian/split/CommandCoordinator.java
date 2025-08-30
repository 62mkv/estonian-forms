package ee.mkv.estonian.split;

import ee.mkv.estonian.domain.Lexeme;
import ee.mkv.estonian.ekilex.EkiLexRetrievalService;
import ee.mkv.estonian.model.InternalPartOfSpeech;
import ee.mkv.estonian.service.LexemeMappingCreationService;
import ee.mkv.estonian.service.UserInputProvider;
import ee.mkv.estonian.service.VerbRootRestoreService;
import ee.mkv.estonian.service.lexeme.ImmutableLexemeAdderService;
import ee.mkv.estonian.service.lexeme.LexemeInitializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommandCoordinator {

    private final ImmutableLexemeAdderService immutableLexemeAdderService;
    private final LexemeInitializer lexemeInitializer;
    private final EkiLexRetrievalService ekiLexRetrievalService;
    private final LexemeMappingCreationService lexemeMappingCreationService;
    private final LexemeRejectionService lexemeRejectionService;
    private final VerbRootRestoreService verbRootRestoreService;
    private final UserInputProvider userInputProvider;

    /**
     * Will run user commands until user chooses to leave
     *
     * @return true, if OK to continue, false, if needs exit
     */
    public boolean runCommand(Lexeme lastLexeme) {
        var chosenOption = showMenu();
        while (true) {
            switch (chosenOption) {
                case ADD_PREFIX:
                    log.info("Adding prefix (first, read from EkiLex)");
                    String prefix = readWordFromUserInput();
                    readFromEkilex(prefix);
                    immutableLexemeAdderService.addImmutableLexeme(prefix, InternalPartOfSpeech.PREFIX);
                    return true;
                case ADD_HIDDEN_NOUN:
                    log.info("Adding hidden noun");
                    String hidden = readWordFromUserInput();
                    immutableLexemeAdderService.addImmutableLexeme(hidden, InternalPartOfSpeech.NOUN);
                    return true;
                case ADD_HIDDEN_ADJECTIVE:
                    log.info("Adding hidden adjective");
                    String hiddenAdjective = readWordFromUserInput();
                    immutableLexemeAdderService.addImmutableLexeme(hiddenAdjective, InternalPartOfSpeech.ADJECTIVE);
                    return true;
                case ADD_HIDDEN_VERB:
                    log.info("Adding hidden verb");
                    String hiddenVerb = readWordFromUserInput();
                    immutableLexemeAdderService.addImmutableLexeme(hiddenVerb, InternalPartOfSpeech.VERB);
                    return true;
                case ADD_RESTORABLE_NOUN:
                    log.info("Adding restorable noun (first, read from EkiLex)");
                    String restorable = readWordFromUserInput();
                    readFromEkilex(restorable);
                    lexemeInitializer.initializeLexeme(restorable, InternalPartOfSpeech.NOUN);
                    return true;
                case ADD_RESTORABLE_ADJECTIVE:
                    log.info("Adding restorable adjective (first, read from EkiLex)");
                    String restorableAdj = readWordFromUserInput();
                    readFromEkilex(restorableAdj);
                    lexemeInitializer.initializeLexeme(restorableAdj, InternalPartOfSpeech.ADJECTIVE);
                    return true;
                case RETRIEVE_FROM_EKILEX:
                    log.info("Retrieving from ekilex");
                    readFromEkilex(readWordFromUserInput());
                    return true;
                case LEXEME_FROM_EKILEX:
                    log.info("Lexeme from ekilex");
                    String ekilexLexeme = readWordFromUserInput();
                    try {
                        lexemeMappingCreationService.createMissingMapping(ekilexLexeme);
                    } catch (Exception e) {
                        log.error("Error converting from EkiLex", e);
                    }
                    return true;
                case ADD_ROOT_FOR_VERB:
                    log.info("Adding root for verb");
                    String verbRoot = readWordFromUserInput();
                    verbRootRestoreService.restoreVerbRoots(verbRoot);
                    return true;
                case REJECT_LEXEME_AS_LOANWORD:
                    log.info("Rejecting lexeme");
                    lexemeRejectionService.rejectLexemeAsLoanWord(lastLexeme);
                    return true;
                case CONTINUE:
                    log.info("Continuing with next lexeme");
                    return true;
                case BREAK:
                    return false;
            }
        }
    }

    private void readFromEkilex(String ekilex) {
        try {
            ekiLexRetrievalService.retrieveByLemma(ekilex, true);
            lexemeMappingCreationService.createMissingMapping(ekilex);
        } catch (Exception e) {
            log.error("Error retrieving from EkiLex", e);
        }
    }

    // produce array based on Option.values()
    private static String[] getOptions() {
        return Arrays.stream(Option.values())
                .map(Enum::name)
                .toArray(String[]::new);
    }

    private String readWordFromUserInput() {
        log.info("Enter word: ");
        return userInputProvider.getFreeFormInput();
    }

    private Option showMenu() {
        // display values of Option numbers between 1 and 4
        // read user input as number between 1 and 4,
        // if not in range, show error message and ask again
        for (Option option : Option.values()) {
            log.info("{}: {}", option.ordinal() + 1, option.name());
        }
        var options = getOptions();
        int input = userInputProvider.getUserChoice(options);
        return Option.from(input);
    }

    enum Option {
        ADD_PREFIX,
        ADD_HIDDEN_NOUN,
        ADD_HIDDEN_ADJECTIVE,
        ADD_HIDDEN_VERB,
        RETRIEVE_FROM_EKILEX,
        LEXEME_FROM_EKILEX,
        ADD_RESTORABLE_NOUN,
        ADD_RESTORABLE_ADJECTIVE,
        REJECT_LEXEME_AS_LOANWORD,
        ADD_ROOT_FOR_VERB,
        CONTINUE,
        BREAK;

        public static Option from(int i) {
            for (Option option : Option.values()) {
                if (option.ordinal() + 1 == i) {
                    return option;
                }
            }
            return null;
        }
    }
}
