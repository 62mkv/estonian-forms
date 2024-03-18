package ee.mkv.estonian.split;

import ee.mkv.estonian.domain.Lexeme;
import ee.mkv.estonian.ekilex.EkiLexRetrievalService;
import ee.mkv.estonian.model.InternalPartOfSpeech;
import ee.mkv.estonian.service.LexemeMappingCreationService;
import ee.mkv.estonian.service.lexeme.ImmutableLexemeAdderService;
import ee.mkv.estonian.service.lexeme.LexemeInitializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.Scanner;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommandCoordinator {

    private final ImmutableLexemeAdderService immutableLexemeAdderService;
    private final LexemeInitializer lexemeInitializer;
    private final EkiLexRetrievalService ekiLexRetrievalService;
    private final LexemeMappingCreationService lexemeMappingCreationService;
    private final LexemeRejectionService lexemeRejectionService;

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
                    log.info("Adding prefix");
                    String prefix = readWordFromUserInput();
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
                case ADD_RESTORABLE_NOUN:
                    log.info("Adding restorable noun");
                    String restorable = readWordFromUserInput();
                    lexemeInitializer.initializeLexeme(restorable, InternalPartOfSpeech.NOUN);
                    return true;
                case ADD_RESTORABLE_ADJECTIVE:
                    log.info("Adding restorable adjective");
                    String restorableAdj = readWordFromUserInput();
                    lexemeInitializer.initializeLexeme(restorableAdj, InternalPartOfSpeech.ADJECTIVE);
                    return true;
                case RETRIEVE_FROM_EKILEX:
                    log.info("Retrieving from ekilex");
                    String ekilex = readWordFromUserInput();
                    try {
                        ekiLexRetrievalService.retrieveByLemma(ekilex, true);
                        lexemeMappingCreationService.createMissingMapping(ekilex);
                    } catch (Exception e) {
                        log.error("Error retrieving from EkiLex", e);
                    }
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
                case REJECT_LEXEME_AS_LOANWORD:
                    log.info("Rejecting lexeme");
                    lexemeRejectionService.rejectLexemeAsLoanWord(lastLexeme);
                    return true;
                case BREAK:
                    return false;
            }
        }
    }

    private String readWordFromUserInput() {
        log.info("Enter word: ");
        Scanner in = new Scanner(System.in);
        var s = in.nextLine();
        if (s.isEmpty()) {
            log.error("Empty input");
            throw new IllegalArgumentException("Empty input");
        }
        return s;
    }

    private Option showMenu() {
        // display values of Option numbers between 1 and 4
        // read user input as number between 1 and 4,
        // if not in range, show error message and ask again
        for (Option option : Option.values()) {
            log.info("{}: {}", option.ordinal() + 1, option.name());
        }
        Scanner in = new Scanner(System.in);
        int input = -1;
        boolean validInput = false;
        do {
            try {
                input = in.nextInt();
                validInput = true;
            } catch (NoSuchElementException e) {
                log.error("Invalid input", e);
            }
        } while (!validInput);
        return Option.from(input);
    }

    enum Option {
        ADD_PREFIX,
        ADD_HIDDEN_NOUN,
        ADD_HIDDEN_ADJECTIVE,
        RETRIEVE_FROM_EKILEX,
        LEXEME_FROM_EKILEX,
        ADD_RESTORABLE_NOUN,
        ADD_RESTORABLE_ADJECTIVE,
        REJECT_LEXEME_AS_LOANWORD,
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
