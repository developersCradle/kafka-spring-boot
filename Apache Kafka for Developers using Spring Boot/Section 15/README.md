# Section 15: Integration Testing using Embedded Kafka - Kafka Consumer.

Integration Testing using Embedded Kafka - Kafka Consumer.

# What I learned.

# Configure Embedded Kafka for Integration Tests.

- We will be writing **test** for following **Consumer**:

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
@SpringBootTest(classes = LibraryEventsConsumerApplication.class)
@EmbeddedKafka(topics = {
        "library-events",
        "library-events.RETRY",
        "library-events.DLT" },
        partitions = 3)
@TestPropertySource(properties = {
        "spring.kafka.producer.bootstrap-servers = ${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "retryListener.startup=false"})
````

- The **test**, which we are writing will be using the:
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

- We will be spinning up, in memory **Kafka Embedded Kafka**: 

````
@EmbeddedKafka(topics = {"library-events"
        , "library-events.RETRY"
        , "library-events.DLT"
}
````

- We will be spinning up, with the **Test Properties**, we can use this for **override** application properties for the test class.
    - `@TestPropertySource(...)`.

- For the following fields goes inside **...**.
    - `properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}"`.
        - Tells to replace following config line: `spring.kafka.producer.bootstrap-servers` inside `.yml`.
            - With following: `${spring.embedded.kafka.brokers}`.
                - `spring.embedded.kafka.brokers` is:
                    - **Auto-created** by `@EmbeddedKafka`.
                    - **Contains** the `host`:`port` of the **embedded Kafka Server**.
                    - Available **ONLY** in the test environment.
                        - **Used** to override your real Kafka settings during tests.

- Currently, the test looks like following: 

````
@SpringBootTest(classes = LibraryEventsConsumerApplication.class)
@EmbeddedKafka(topics = {
        "library-events",
        "library-events.RETRY",
        "library-events.DLT" },
        partitions = 3)
@TestPropertySource(properties = {
        "spring.kafka.producer.bootstrap-servers = ${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "retryListener.startup=false"})
public class LibraryEventsConsumerIntegrationTest
{
... Test code here ...
}
````

- We can **verify**, with actual `IP`:`port` of the **Embedded Kafka Broker** during your test:

````
@Autowired
Environment env;

@Test
void showBrokers() {
    System.out.println(env.getProperty("spring.embedded.kafka.brokers"));
}
````

- The **Environment** is from: `import org.springframework.core.env.Environment;`.

- You can see the assigned **IP:PORT** during the test, from Logs:

````
127.0.0.1:52523
````

- Logging can be seen from the `GIF` in action.

<div align="center">
    <img src="spring.embedded.kafka.brokers test logging.gif"  alt="Apache Kafka for Developers using Spring Boot" width="700"/>
</div>

> [!TIP]
> **Remember** in Spring prefers using Spring-managed beans in its components. Example of that is using the`@Autowired`.

- We **inject** instance of the `EmbeddedKafkaBroker` into our **test** class for us to use it later. Like in real world cases, this **Kafka Broker** is holding the **Kafka Topics**.
    
````
    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;
````

- For the test we need to configure the **Kafka Producer** and for **Kafka Consumer**:

````
  consumer:
    bootstrap-servers:  localhost:9092,localhost:9093,localhost:9094
    key-deserializer: org.apache.kafka.common.serialization.IntegerDeserializer
    value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
    group-id: library-events-listener-group
  producer:
    bootstrap-servers: localhost:9092,localhost:9093,localhost:9094
    key-serializer: org.apache.kafka.common.serialization.IntegerSerializer
    value-serializer: org.apache.kafka.common.serialization.StringSerializer
````

> We need to provide these **serializers** so that every message `value` or `key` you send is a **converted** appropriately, since **Kafka** is NOT aware of Java types.

- Here, we are providing the **Kafka** the configurations that it will need for the deserialization and serialization.
  - **Producer** serializers:
    - `IntegerSerializer` → convert **Integer** into **bytes**.
    - `StringSerializer` → convert **String** into **bytes**.
  - **Consumer** deserializers:
    - `IntegerDeserializer` → convert **bytes** back into **Integer**.
    - `StringDeserializer` → convert **bytes** back into **String**.

<div align="center">
    <img src="templateClassesInGeneralInSpring.jpeg"  alt="Apache Kafka for Developers using Spring Boot" width="600"/>
</div>

- There is some `Template` examples:
    - `JdbcTemplate` Execute **SQL** queries on a relational database.
    - `RestTemplate` Send HTTP requests to **REST** endpoints.
        - You can see one example of such choice [here](https://github.com/developersCradle/springboot-microservices/tree/main?tab=readme-ov-file#architecture-explanation).
    - `KafkaTemplate` Send messages to **Kafka** topics.
    - `JmsTemplate` Send and receive messages via **JMS**.
    - `RabbitTemplate` Send and receive messages via **RabbitMQ**.

> [!NOTE]
> This is no different in **Kafka** as well see the → `KafkaTemplate`.

````
@Autowired
KafkaTemplate<Integer, String> kafkaTemplate;
````

- The **KafkaTempate** makes sending **Kafka** messages much simpler, see below:
    - The without **KafkaTemplate**, we need to write a lot of **boiler code**:
    ````
    Properties props = new Properties();
    props.put("bootstrap.servers", "localhost:9092");
    props.put("key.serializer", IntegerSerializer.class.getName());
    props.put("value.serializer", StringSerializer.class.getName());

    Producer<Integer, String> producer = new KafkaProducer<>(props);
    producer.send(new ProducerRecord<>("test-topic", 1, "Hello"));
    producer.close();
    ````
    - The with The **KafkaTemplate**:
    ````
    @Autowired
    KafkaTemplate<Integer, String> kafkaTemplate;

    kafkaTemplate.send("test-topic", 1, "Hello Kafka!");
    ````

- Configuring the point to which topic the **message** is sent to! The **template** will be used for sending the messages.

````
spring:
  kafka:
    template:
      default-topic: library-events
````

- Every `@KafkaListener` becomes a **Listener Container**, and this `KafkaListenerEndpointRegistry` keeps **track of them**, as below the example:

````
    @Autowired
    KafkaListenerEndpointRegistry endpointRegistry;
````

- In general, we need this `KafkaListenerEndpointRegistry`, because we need way to:
  - Find a specific listener container.
  - Start it.
  - Stop it.
  - Pause it.
  - Resume it.
  - Check if it's running.
  - Wait for partition assignment.

- In the **tests** this is particular useful, for following reasons:
  - Start before **Kafka is ready**.
  - Start without **partitions**.
  - Run **asynchronously**.
  - **Miss** messages.
  - Cause flaky tests.

- We need to have way to check that **Kafka** is setted correctly before running the test.

````
    @BeforeEach
    void setUp() {
        for (MessageListenerContainer messageListenerContainer : endpointRegistry.getListenerContainers()) {
            ContainerTestUtils.waitForAssignment(messageListenerContainer, embeddedKafkaBroker.getPartitionsPerTopic());
        }
    }
````

- `ContainerTestUtils` is coming from the **Kafka library**.
  - This is for the **Spring Kafka Test Utility** functions.
    - We are using it as following:
      - `ContainerTestUtils.waitForAssignment(messageListenerContainer, embeddedKafkaBroker.getPartitionsPerTopic());`.
        - We need to be able to tell how many partitions will be assigned to **before we can conclude** the is **readiness**.

<div align="center">
    <img src="partitionsPerTopic.PNG"  alt="Apache Kafka for Developers using Spring Boot" width="400"/>
</div>

1. As reminder, in **Topic** there can be many **Partitions**. Here we are just deducting the **readiness** for the test! 

<details>
<summary id="IDE problem" open="false">Full configuration at the end of chapter for <b>Kafka</b> <code>application.yml</code>.</summary>

````
spring:
  profiles:
    active: local
server:
  port: 8081
---

# For the local!

spring:
  config:
    activate:
      on-profile: local
  kafka:
    template:
      default-topic: library-events
    consumer:
      bootstrap-servers:  localhost:9092,localhost:9093,localhost:9094
      key-deserializer: org.apache.kafka.common.serialization.IntegerDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      group-id: library-events-listener-group
    producer:
      bootstrap-servers: localhost:9092,localhost:9093,localhost:9094
      key-serializer: org.apache.kafka.common.serialization.IntegerSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
  jpa:
    database: h2
    database-platform: org.hibernate.dialect.H2Dialect
    generate-ddl: true
  h2:
    console:
      enabled: true
---

# For the non-production!

spring:
  config:
    activate:
      on-profile: nonprod
  kafka:
    consumer:
      bootstrap-servers: nonprod:9092,nonprod:9093,nonprod:9094
      key-deserializer: org.apache.kafka.common.serialization.IntegerDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
````
</schema>
</details>

# Write the Integration test for posting a "NEW" LibraryEvent.

- We are using this **JSON** for publishing Library event:

````
// Given.
String json = " {\"libraryEventId\":null,\"libraryEventType\":\"NEW\",\"book\":{\"bookId\":456,\"bookName\":\"Kafka Using Spring Boot\",\"bookAuthor\":\"Dilip\"}}";
kafkaTemplate.sendDefault(json).get();
````

- We are making **asynchronous** call to Topic with the JSON.
  - With the `.get()` it turns into **blocking call** → **synchronous call**.

````
kafkaTemplate.sendDefault(json).get();
````

- We will be **utilizing** the check [CountDownLatch](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/CountDownLatch.html) link.

> `CountDownLatch` in nutshell →  You set a number, and the latch waits until that number reaches zero.

- The full test:

````
    @Test
    void publishNewLibraryEvent() throws ExecutionException, InterruptedException, JsonProcessingException {
        //given
        String json = " {\"libraryEventId\":null,\"libraryEventType\":\"NEW\",\"book\":{\"bookId\":456,\"bookName\":\"Kafka Using Spring Boot\",\"bookAuthor\":\"Dilip\"}}";
        kafkaTemplate.sendDefault(json).get();

        //when
        CountDownLatch latch = new CountDownLatch(1);
        latch.await(3, TimeUnit.SECONDS);

        //then
        verify(libraryEventsConsumerSpy, times(1)).onMessage(isA(ConsumerRecord.class));
        verify(libraryEventsServiceSpy, times(1)).processLibraryEvent(isA(ConsumerRecord.class));

        List<LibraryEvent> libraryEventList = (List<LibraryEvent>) libraryEventsRepository.findAll();
        assert libraryEventList.size() == 1;
        libraryEventList.forEach(libraryEvent -> {
            assert libraryEvent.getLibraryEventId() != null;
            assertEquals(456, libraryEvent.getBook().getBookId());
        });
    }
````


# Write the Integration test for posting a "UPDATE" LibraryEvent.



# Write the Integration test for posting an invalid UPDATE LibraryEvent.



# Integration Tests for Real Databases using TestContainers.
