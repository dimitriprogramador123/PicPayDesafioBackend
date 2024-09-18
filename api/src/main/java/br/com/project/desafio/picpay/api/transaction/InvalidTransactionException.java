package br.com.project.desafio.picpay.api.transaction;

public class InvalidTransactionException extends RuntimeException {

  public InvalidTransactionException(String message) {
    super(message);
  }

}
