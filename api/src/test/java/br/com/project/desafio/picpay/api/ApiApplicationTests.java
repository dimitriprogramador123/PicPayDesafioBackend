package br.com.project.desafio.picpay.api;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import br.com.project.desafio.picpay.api.authorization.AuthorizerService;
import br.com.project.desafio.picpay.api.transaction.Transaction;
import br.com.project.desafio.picpay.api.transaction.TransactionRepository;
import br.com.project.desafio.picpay.api.transaction.TransactionService;
import br.com.project.desafio.picpay.api.wallet.Wallet;
import br.com.project.desafio.picpay.api.wallet.WalletRepository;
import br.com.project.desafio.picpay.api.wallet.WalletType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@SpringBootTest
class ApiApplicationTests {

	@InjectMocks
	private TransactionService transactionService;

	@Mock
	private TransactionRepository transactionRepository;

	@Mock
	private WalletRepository walletRepository;

	@Mock
	private AuthorizerService authorizerService;

	@Test
	void contextLoads() {
		MockitoAnnotations.openMocks(this);

		// Criar uma transação de exemplo
		Transaction transaction = new Transaction(null, 1L, 2L, new BigDecimal(1000), null);

		// Configurar as instâncias de Wallet
		Wallet payee = new Wallet(1L, null, null, null, null, WalletType.COMUM.getValue(), BigDecimal.ZERO, 1L);
		Wallet payer = new Wallet(2L, null, null, null, null, WalletType.COMUM.getValue(), new BigDecimal(1000), 1L);

		// Configurar mocks
		when(walletRepository.findById(transaction.payee())).thenReturn(Optional.of(payee));
		when(walletRepository.findById(transaction.payer())).thenReturn(Optional.of(payer));
		when(transactionRepository.save(transaction)).thenReturn(transaction);

		// Testar o método create
		Transaction newTransaction = transactionService.create(transaction);

		assertEquals(transaction, newTransaction);
	}
}
