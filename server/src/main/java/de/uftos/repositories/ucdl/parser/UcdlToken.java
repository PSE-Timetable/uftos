package de.uftos.repositories.ucdl.parser;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Token which can be parsed from a correct UCDL-file for the definition of a constraint.
 */
public enum UcdlToken {
  END_OF_FILE(null),
  WHITESPACE("\\s+"),
  LPAREN("("),
  RPAREN(")"),
  LBRACE("\\{"),
  RBRACE("\\}"),
  LBRACKET("\\["),
  RBRACKET("\\]"),
  COMMA(","),
  DOT("\\."),
  NUMBER("\\d+", true),
  STRING("\"(([^\"\\\\\\u0000-\\u001F]|(\\\\[\"\\\\/bfnrt])|(\\\\u[0-9a-fA-F]{4}))*)\"", true),

  FOR("for"),
  IF("if"),
  RETURN("return"),

  TRUE("true"),
  FALSE("false"),
  THIS("this"),

  FORALL("forall"),
  EXISTS("exists"),
  EQUALS("="),
  SMALLER("<"),
  GREATER(">"),
  EXCLAMATION("!"),

  IMPLIES("->|implies"),
  OR("\\|\\||or"),
  AND("&&|and"),
  NOT("not"),

  IN("in"),
  OF("of"),

  IS_EMPTY("isEmpty"),
  SIZE("size");

  /**
   * The regular expression matching character sequencing corresponding to this TokenType.
   */
  public final String regexp;

  /**
   * Shall a token of this type remember the characters matched during lexing?
   */
  public final boolean rememberCharacters;

  UcdlToken(String regexp, boolean rememberCharacters) {
    this.regexp = regexp;
    this.rememberCharacters = rememberCharacters;
  }

  UcdlToken(String regexp) {
    this(regexp, false);
  }


  public static final UcdlToken[] NO_END_OF_FILE;
  static {
    NO_END_OF_FILE = Arrays.stream(UcdlToken.values()).filter((token) -> token != END_OF_FILE).toList().toArray(new UcdlToken[]{});
  }

  public static final Pattern TOKENPATTERN;

  static {
    final StringBuilder tokenPattern = new StringBuilder();
    String sep = "";
    for (UcdlToken t : UcdlToken.NO_END_OF_FILE) {
      tokenPattern.append(String.format("%s(?<%s>%s)", sep, t.name(), t.regexp));
      sep = "|";
    }
    TOKENPATTERN = Pattern.compile(tokenPattern.toString());
  }
}
