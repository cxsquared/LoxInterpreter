package me.codyclaborn.lox;

/**
 * Created by cxsqu on 4/9/2017.
 */
public class RuntimeError extends RuntimeException {
   final Token token;

   RuntimeError(Token token, String message) {
      super(message);
      this.token = token;
   }
}
