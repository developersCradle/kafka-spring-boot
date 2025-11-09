# Section 06: Understanding Kafka Components and its Internals - (Theory + Hands On).

Understanding Kafka Components and its Internals - (Theory + Hands On).

# What I learned.

<div align="center">
    <img src="kafkaTopics.PNG"  alt="java advanced" width="700"/>
</div>

1. **Topic** is an Entity, which have a **name** in **Kafka**.
    - Think **Topic** as an table in database like **Entity** Hibernate world.
2. **Topics** mostly live in **Kafka Broker**. Notice the **TopicA** in this example.
    - **Clients** uses the **Topic** name to **produce** and **consume** messages.
3. **Kakfa Consumer** job, is to pull messages from the **Topic**.
    - In this example, **Kafka Consumer** is pulling **Kafka Broker** with the **Topics Name**! 

<div align="center">
    <img src="kafkaTopicsProducer.PNG"  alt="java advanced" width="700"/>
</div>

1. The **Kafka Producer** job often to **produce** messages into the **Topic**.
    - This usually when, external system invokes the **Kafka Producer**.
    
<div align="center">
    <img src="kafkaTopicsMessageInTheTopic.PNG"  alt="java advanced" width="700"/>
</div>

1. Message reaches the **Kafka Topic** first. 

2. Once noticed by the **poll**, it will be **consumed** by the **Consumer**. Notice the **message** is still residing **inside topic** for the **retention** time period.

<div align="center">
    <img src="topicAndPartition.PNG"  alt="java advanced" width="700"/>
</div>

1. The **message** is located inside the **Partition** and this is resident inside **Topic**. 

<div align="center">
    <img src="topicAndPartitionSecond.PNG"  alt="java advanced" width="700"/>
</div>

1. **Each partition** is **ordered**, **immutable sequence of records**. Once the **record** is introduced it cannot be changed at all.
2. Each record has **Offset** on it.
    - **Offset** can be used to keep track of the order.
3. The **Partitions** are **independent** of each other! 
4. Records are stored in `.log` file.
    - This is **distributed** log file!

<div align="center">
    <img src="topicAndPartitionThird.PNG"  alt="java advanced" width="700"/>
</div>

1. **Kafka Producer** produces messages to the **Partitions**. They get appended to the **appropriate** partition line, at the end!
    - Producer has control, to which **Partition** the message goes.

# SetUp a Zookeeper/Kafka Broker in Local.

- You can check the [Commands](https://github.com/dilipsundarraj1/kafka-for-beginners/blob/master/SetUpKafka.md).

<div align="center">
    <img src="settingUpKafka.PNG"  alt="java advanced" width="700"/>
</div>

- We need:
    - `1.` Zookeeper. Think this as **centralized service**.
    - `2.` Kafka Broker.

- We will spin up the **ZooKeeper** and **Kafka**:
    - Once the **Kafka Broker** is started, then it will be registered to the **ZooKeeper**.

- We can check the instruction from [here](https://github.com/dilipsundarraj1/kafka-for-beginners/blob/master/SetUpKafka.md)

> [!IMPORTANT]
> Remember to place the **Kafka** to local drive to prevent the too long character error!

- We are starting the **ZooKeepper** in our local machine!
    - `./zookeeper-server-start.bat ../../config/zookeeper.properties`.

<div align="center">
    <img src="startingTheZooKeeper.gif"  alt="java advanced" width="700"/>
</div>

- We see that it's running at port `2181`.

- Change the **Broker** configs `server.properties`.

- We need to start the **Broker**.
    - We are starting the **Kafka Broker** with our config files. This is executed in **GitBash Windows**.
        - `./kafka-server-start.bat ../../config/server.properties`.


- I had the following config, `server.properties`.

````
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# see kafka.server.KafkaConfig for additional details and defaults

############################# Server Basics #############################

# The id of the broker. This must be set to a unique integer for each broker.
broker.id=0

############################# Socket Server Settings #############################

# The address the socket server listens on. It will get the value returned from 
# java.net.InetAddress.getCanonicalHostName() if not configured.
#   FORMAT:
#     listeners = listener_name://host_name:port
#   EXAMPLE:
#     listeners = PLAINTEXT://your.host.name:9092
#listeners=PLAINTEXT://:9092

# Hostname and port the broker will advertise to producers and consumers. If not set, 
# it uses the value for "listeners" if configured.  Otherwise, it will use the value
# returned from java.net.InetAddress.getCanonicalHostName().
#advertised.listeners=PLAINTEXT://your.host.name:9092

# Maps listener names to security protocols, the default is for them to be the same. See the config documentation for more details
#listener.security.protocol.map=PLAINTEXT:PLAINTEXT,SSL:SSL,SASL_PLAINTEXT:SASL_PLAINTEXT,SASL_SSL:SASL_SSL

# The number of threads that the server uses for receiving requests from the network and sending responses to the network
num.network.threads=3

# The number of threads that the server uses for processing requests, which may include disk I/O
num.io.threads=8

# The send buffer (SO_SNDBUF) used by the socket server
socket.send.buffer.bytes=102400

# The receive buffer (SO_RCVBUF) used by the socket server
socket.receive.buffer.bytes=102400

# The maximum size of a request that the socket server will accept (protection against OOM)
socket.request.max.bytes=104857600


############################# Log Basics #############################

# A comma separated list of directories under which to store log files
log.dirs=/tmp/kafka-logs

# The default number of log partitions per topic. More partitions allow greater
# parallelism for consumption, but this will also result in more files across
# the brokers.
num.partitions=1

# The number of threads per data directory to be used for log recovery at startup and flushing at shutdown.
# This value is recommended to be increased for installations with data dirs located in RAID array.
num.recovery.threads.per.data.dir=1

############################# Internal Topic Settings  #############################
# The replication factor for the group metadata internal topics "__consumer_offsets" and "__transaction_state"
# For anything other than development testing, a value greater than 1 is recommended for to ensure availability such as 3.
offsets.topic.replication.factor=1
transaction.state.log.replication.factor=1
transaction.state.log.min.isr=1

############################# Log Flush Policy #############################

# Messages are immediately written to the filesystem but by default we only fsync() to sync
# the OS cache lazily. The following configurations control the flush of data to disk.
# There are a few important trade-offs here:
#    1. Durability: Unflushed data may be lost if you are not using replication.
#    2. Latency: Very large flush intervals may lead to latency spikes when the flush does occur as there will be a lot of data to flush.
#    3. Throughput: The flush is generally the most expensive operation, and a small flush interval may lead to excessive seeks.
# The settings below allow one to configure the flush policy to flush data after a period of time or
# every N messages (or both). This can be done globally and overridden on a per-topic basis.

# The number of messages to accept before forcing a flush of data to disk
#log.flush.interval.messages=10000

# The maximum amount of time a message can sit in a log before we force a flush
#log.flush.interval.ms=1000

############################# Log Retention Policy #############################

# The following configurations control the disposal of log segments. The policy can
# be set to delete segments after a period of time, or after a given size has accumulated.
# A segment will be deleted whenever *either* of these criteria are met. Deletion always happens
# from the end of the log.

# The minimum age of a log file to be eligible for deletion due to age
log.retention.hours=168

# A size-based retention policy for logs. Segments are pruned from the log unless the remaining
# segments drop below log.retention.bytes. Functions independently of log.retention.hours.
#log.retention.bytes=1073741824

# The maximum size of a log segment file. When this size is reached a new log segment will be created.
log.segment.bytes=1073741824

# The interval at which log segments are checked to see if they can be deleted according
# to the retention policies
log.retention.check.interval.ms=300000

############################# Zookeeper #############################

# Zookeeper connection string (see zookeeper docs for details).
# This is a comma separated host:port pairs, each corresponding to a zk
# server. e.g. "127.0.0.1:3000,127.0.0.1:3001,127.0.0.1:3002".
# You can also append an optional chroot string to the urls to specify the
# root directory for all kafka znodes.
zookeeper.connect=localhost:2181

# Timeout in ms for connecting to zookeeper
zookeeper.connection.timeout.ms=6000


############################# Group Coordinator Settings #############################

# The following configuration specifies the time, in milliseconds, that the GroupCoordinator will delay the initial consumer rebalance.
# The rebalance will be further delayed by the value of group.initial.rebalance.delay.ms as new members join the group, up to a maximum of max.poll.interval.ms.
# The default value for this is 3 seconds.
# We override this to 0 here as it makes for a better out-of-the-box experience for development and testing.
# However, in production environments the default value of 3 seconds is more suitable as this will help to avoid unnecessary, and potentially expensive, rebalances during application startup.
group.initial.rebalance.delay.ms=0
````



   
    <!-- - Check the different **services** `zoo1` and `kafka`. -->

````
version: '2.1'

services:
  zoo1:
    image: confluentinc/cp-zookeeper:7.3.2
    hostname: zoo1
    container_name: zoo1
    ports:
      - "2181:2181"
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_SERVER_ID: 1
      ZOOKEEPER_SERVERS: zoo1:2888:3888


  kafka1:
    image: confluentinc/cp-kafka:7.3.2
    hostname: kafka1
    container_name: kafka1
    ports:
      - "9092:9092"
      - "29092:29092"
    environment:
      KAFKA_ADVERTISED_LISTENERS: INTERNAL://kafka1:19092,EXTERNAL://${DOCKER_HOST_IP:-127.0.0.1}:9092,DOCKER://host.docker.internal:29092
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: INTERNAL:PLAINTEXT,EXTERNAL:PLAINTEXT,DOCKER:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: INTERNAL
      KAFKA_ZOOKEEPER_CONNECT: "zoo1:2181"
      KAFKA_BROKER_ID: 1
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
    depends_on:
      - zoo1

````

- We will be running the docker images `docker-compose up`.

> [!NOTE]
> Create a **Kafka topic**.

- Create a **Kafka topic** using the **kafka-topics** command:
    - **kafka1:19092** refers to the **KAFKA_ADVERTISED_LISTENERS** in the `docker-compose.yml` file.

- We will be **opening shell** inside **Kafka** container: `docker exec -it kafka1 bash`.

- Then to **Create topic** under the name of the `test-topic`.

````
kafka-topics --bootstrap-server kafka1:19092 \
             --create \
             --topic test-topic \
             --replication-factor 1 --partitions 1
````

> [!NOTE]
> Produce a **Message to the topic**.

> [!NOTE]
> Consume a **Message to the topic**.

> [!NOTE]
> Produce a **Message to the topic**, with **Key** and **Value**.

> [!NOTE]
> Consume a **Message to the topic**, with **Key** and **Value**.

> [!NOTE]
> Consume a **Messages using Consumer Groups**.

> [!NOTE]
> Consume a **Messages With Headers**.


# Set Up a ZooKeeper/Kafka Broker in Local.

# Create Topic, Produce and Consume Messages using the Command Line Interface (CLI).

# Produce and Consume Messages with Key.

# Consumer Offsets.

# Consumer Groups.

# Commit Log and Retention Policy.

# Kafka as a Distributed Streaming System.

# Setting up a Kafka Cluster in Local with 3 Kafka Brokers.

# How Kafka Cluster distributes the Client Requests? - Leader/Follower.

# How Kafka handles Data Loss? - Replication and In-Sync-Replica (ISR).

# Fault Tolerance and Robustness in Kafka.
