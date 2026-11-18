package com.equationsolver.equations.core;

import com.equationsolver.equations.core.Token.TokenType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenizerTest {

    private final Tokenizer tokenizer = new Tokenizer();

    @Test
    void splitsNumbersVariablesOperatorsAndParentheses() {
        List<Token> tokens = tokenizer.tokenize("3x + (y2 - 1.5)^2");
        List<String> texts = tokens.stream().map(Token::text).toList();
        assertEquals(List.of("3", "x", "+", "(", "y2", "-", "1.5", ")", "^", "2"), texts);
        assertEquals(TokenType.NUMBER, tokens.get(0).type());
        assertEquals(TokenType.VARIABLE, tokens.get(1).type());
        assertEquals(TokenType.VARIABLE, tokens.get(4).type());
    }

    @Test
    void acceptsLeadingDecimalPoint() {
        assertEquals(".5", tokenizer.tokenize(".5").get(0).text());
    }

    @Test
    void recordsOneBasedPositions() {
        assertEquals(6, tokenizer.tokenize("ab + c").get(2).position());
    }

    @Test
    void rejectsBlankInput() {
        assertThrows(InvalidEquationException.class, () -> tokenizer.tokenize("   "));
        assertThrows(InvalidEquationException.class, () -> tokenizer.tokenize(null));
    }

    @Test
    void rejectsUnknownCharacters() {
        InvalidEquationException e = assertThrows(InvalidEquationException.class, () -> tokenizer.tokenize("3x $ 2"));
        assertTrue(e.getMessage().contains("'$'"));
        assertTrue(e.getMessage().contains("position 4"));
        assertThrows(InvalidEquationException.class, () -> tokenizer.tokenize("x = 2"));
    }

    @Test
    void rejectsMalformedNumbers() {
        assertThrows(InvalidEquationException.class, () -> tokenizer.tokenize("1.2.3"));
    }
}
