package de.uftos.repositories.ucdl.parser;

import java.util.regex.Matcher;

public class Lexer {
  Token current;
  private final Matcher tokenMatcher;

  public Lexer(String input) {
    if(input == null) throw new IllegalArgumentException();

    tokenMatcher = UcdlToken.TOKENPATTERN.matcher(input);
    lex();
  }

  public boolean lex() {
    if (tokenMatcher == null) throw new IllegalStateException();

    do {
      final int previouslyMatchedCharacterIndex = (current == null) ? 0 : tokenMatcher.end();

      if (tokenMatcher.find()) {
        // Abort if tokenMatcher had to skip characters in order to find the next valid token
        if (tokenMatcher.start() > previouslyMatchedCharacterIndex) throw new IllegalArgumentException();

        // The characters matched correspond to exactly one new Token.
        // Generate this, possibly annotating it with the matched character sequence
        boolean matchingTokenFound = false;
        for (UcdlToken t : UcdlToken.NO_END_OF_FILE) {
          boolean tokenMatches = tokenMatcher.group(t.name()) != null;

          // The token, as defined in TokenType, are "mutually exclusive":
          assert !(matchingTokenFound && tokenMatches);

          matchingTokenFound |= tokenMatches;

          if (tokenMatches) {
            current = new Token(t, tokenMatcher.group(t.name()));
          }
        }
        assert matchingTokenFound;

      } else {
        // Abort if no more tokens could be found, but the input has not been fully read yet.
        if (previouslyMatchedCharacterIndex < tokenMatcher.regionEnd()) throw new IllegalArgumentException();

        current = new Token(UcdlToken.END_OF_FILE);
        return false;
      }
    } while (current.ucdlToken == UcdlToken.WHITESPACE);

    return true;
  }
}
