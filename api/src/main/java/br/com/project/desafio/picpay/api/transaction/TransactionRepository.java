package br.com.project.desafio.picpay.api.transaction;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface TransactionRepository extends ListCrudRepository<Transactional, Long> {

    Object saveAll(Transaction transaction);

    Object save(Transaction transaction);

}
