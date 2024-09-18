package br.com.project.desafio.picpay.api.transaction;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.project.desafio.picpay.api.authorization.AuthorizerService;
import br.com.project.desafio.picpay.api.notification.NotificationService;
import br.com.project.desafio.picpay.api.wallet.Wallet;
import br.com.project.desafio.picpay.api.wallet.WalletRepository;
import br.com.project.desafio.picpay.api.wallet.WalletType;

@Service
public class TransactionService {
  private static final Logger LOGGER = LoggerFactory.getLogger(TransactionService.class);
  private final TransactionRepository transactionRepository;
  private final WalletRepository walletRepository;
  private final AuthorizerService authorizerService;
  private final NotificationService notificationService;

  public TransactionService(TransactionRepository transactionRepository,
      WalletRepository walletRepository, AuthorizerService authorizerService,
      NotificationService notificationService) {
    this.transactionRepository = transactionRepository;
    this.walletRepository = walletRepository;
    this.authorizerService = authorizerService;
    this.notificationService = notificationService;
  }

  @Transactional
  public Transaction create(Transaction transaction) {
    validate(transaction);

    Transaction newTransaction = (Transaction) transactionRepository.saveAll(transaction);

    var walletPayer = walletRepository.findById(transaction.payer()).get();
    var walletPayee = walletRepository.findById(transaction.payee()).get();
    walletRepository.save(walletPayer.debit(transaction.value()));
    walletRepository.save(walletPayee.credit(transaction.value()));

    authorizerService.authorize(transaction);
    notificationService.notify(newTransaction);

    return newTransaction;
  }

  /*
   * A transaction is valid if:
   * - the payer is a common wallet
   * - the payer has enough balance
   * - the payer is not the payee
   */

  private void validate(Transaction transaction) {
    LOGGER.info("Validating transaction {}...", transaction);

    Wallet payer = walletRepository.findById(transaction.payer())
        .orElseThrow(() -> new InvalidTransactionException("Payer wallet not found - " + transaction));

    validatePayer(payer, transaction);
  }

  private void validatePayer(Wallet payer, Transaction transaction) {
    if (payer.type() != WalletType.COMUM.getValue()) {
      throw new InvalidTransactionException("Invalid payer type - " + transaction);
    }

    if (payer.balance().compareTo(transaction.value()) < 0) {
      throw new InvalidTransactionException("Insufficient balance - " + transaction);
    }

    if (payer.id().equals(transaction.payee())) {
      throw new InvalidTransactionException("Payer cannot be the payee - " + transaction);
    }
  }

  public List<Transactional> list() {
    return transactionRepository.findAll();
  }
}
