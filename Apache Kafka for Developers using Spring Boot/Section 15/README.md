# Section 15: Integration Testing using Embedded Kafka - Kafka Consumer.

Integration Testing using Embedded Kafka - Kafka Consumer.

# What I learned.

# Configure Embedded Kafka for Integration Tests.

- We will be writing **test** for Consumer. For following **Consumer**


````
package com.learnkafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.learnkafka.service.LibraryEventsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LibraryEventsConsumer {

    @Autowired
    private LibraryEventsService libraryEventsService;

    @KafkaListener(
            topics = {"library-events"}
            , groupId = "library-events-listener-group")
    public void onMessage(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException {

        log.info("ConsumerRecord : {} ", consumerRecord);
        libraryEventsService.processLibraryEvent(consumerRecord);

    }
}
````

- Todo check this, selvitä mistä tutoriaalista tämä pätkä selvitetään:

````
@EmbeddedKafka(topics = {"library-events"
        , "library-events.RETRY"
        , "library-events.DLT"
}
        , partitions = 3)
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}"
        , "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}"
        , "retryListener.startup=false"})
````

- The test, which we are writing will be using the:
    -  `@SpringBootTest(classes = LibraryEventsConsumerApplication.class)`.
        - The `@SpringBootTest()` will be starting as in **production**. This will load the:
            - The full Spring context.
            - All beans.
            - The web environment (unless disabled).
            - The configuration.
            - The auto-configuration.
            - The Spring Boot application class.
        - The `classes = LibraryEventsConsumerApplication.class`.
            - Tells the `“Use THIS class as the starting point of the application.”`.
                - This usually needed if the tests are in **non-standard** folder.

> [!NOTE]
> A **Kafka broker** = single running Kafka instance.

- We will be spinning up, in memory Kafka **Embedded Kafka**: 

````
@EmbeddedKafka(topics = {"library-events"
        , "library-events.RETRY"
        , "library-events.DLT"
}
````

- We will be spinning up, with the **Test Properties**, we can use this for **override** application properties for the test class. 

- `@TestPropertySource(...)`



# Write the Integration test for posting a "NEW" LibraryEvent.


# Write the Integration test for posting a "UPDATE" LibraryEvent.


# Write the Integration test for posting an invalid UPDATE LibraryEvent.


# Integration Tests for Real Databases using TestContainers.
