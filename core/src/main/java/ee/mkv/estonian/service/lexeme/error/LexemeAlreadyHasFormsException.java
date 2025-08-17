package ee.mkv.estonian.service.lexeme.error;

import ee.mkv.estonian.domain.Lexeme;

public class LexemeAlreadyHasFormsException extends RuntimeException {
    public LexemeAlreadyHasFormsException(Long lexemeId) {
        super("Lexeme with id " + lexemeId + " already has forms");
    }

    public LexemeAlreadyHasFormsException(Lexeme lexeme) {
        super("Lexeme [" + lexeme.toString() + "] already has forms");
    }
}
