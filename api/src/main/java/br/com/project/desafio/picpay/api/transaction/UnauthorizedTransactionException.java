package br.com.project.desafio.picpay.api.transaction;

public class UnauthorizedTransactionException extends RuntimeException {
  public UnauthorizedTransactionException(String message) {
    super(message);
  }
}
