package br.com.project.desafio.picpay.api.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.pulsar.PulsarProperties.Transaction;
import org.springframework.stereotype.Service;


@Service
public class NotificationService {
  private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);
    private final NotificationProducer notificationProducer;

  public NotificationService(NotificationProducer notificationProducer) {
    this.notificationProducer = notificationProducer;
  }

  public void notify(Transaction transaction) {
    LOGGER.info("notifying transaction {}...", transaction);

    notificationProducer.sendNotification(transaction);
  }

  public void notify(Object newTransaction) {
    // 
    throw new UnsupportedOperationException("Unimplemented method 'notify'");
  }
}
