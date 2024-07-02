package de.uftos.repositories.ucdl.parser;

public class Token {
  public final String text;
  public final UcdlToken ucdlToken;

  public Token(UcdlToken ucdlToken) {
    this(ucdlToken, null);
  }

  public Token(UcdlToken ucdlToken, String text) {
    if (ucdlToken.rememberCharacters && (text == null)) throw new IllegalArgumentException();

    this.ucdlToken = ucdlToken;
    this.text = ucdlToken.rememberCharacters ? text : null;
  }

  @Override
  public String toString() {
    return "<" + ucdlToken + (text != null ? "(" + text + ")" : "") + ">";
  }

}
