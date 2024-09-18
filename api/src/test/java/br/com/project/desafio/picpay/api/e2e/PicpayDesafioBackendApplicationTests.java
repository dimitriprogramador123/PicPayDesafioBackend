package br.com.project.desafio.picpay.api.e2e;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.web.reactive.server.WebTestClient;

import br.com.project.desafio.picpay.api.transaction.Transaction;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(partitions = 1, brokerProperties = {
		"listeners=PLAINTEXT://localhost:9092", "port=9092" })
class PicpayDesafioBackendApplicationTests {
	@Autowired
	private WebTestClient webClient;

	@Test


	void testCreateTransactionSuccess() {
		var transaction = new Transaction(null, 1L, 2L, new BigDecimal(1000), null);

		var postResponse = webClient
				.post()
				.uri("/transaction")
				.bodyValue(transaction)
				.exchange()
				.expectStatus().is2xxSuccessful()
				.expectBody(Transaction.class)
				.value(t -> assertNotNull(t.id()))
				.value(t -> assertEquals(transaction.payer(), t.payer()))
				.value(t -> assertEquals(transaction.payee(), t.payee()))
				.value(t -> assertEquals(transaction.value(), t.value()))
				.value(t -> assertNotNull(t.createdAt()))
				.returnResult();

		webClient
				.get()
				.uri("/transaction")
				.exchange()
				.expectStatus().is2xxSuccessful()
				.expectBodyList(Transaction.class)
				.hasSize(1).isEqualTo(List.of(postResponse.getResponseBody()))
				.returnResult();
	}

	@ParameterizedTest
	@MethodSource("providesInvalidTransactions")
	void testCreateTransactionError(Transaction transaction) {
		webClient
				.post()
				.uri("/transaction")
				.bodyValue(transaction)
				.exchange()
				.expectStatus().is4xxClientError();
	}

}
