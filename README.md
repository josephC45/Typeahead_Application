# Multithreaded & Reactive Typeahead Application

## Overview
The Typeahead Application is a scalable and efficient system that provides real-time suggestions as users type. It utilizes a Trie data structure to store and query popular prefixes, supporting continuous updates and processing of typed input. The application is built using Spring Boot, Apache Kafka, Spark, and HBase with Docker for containerization.

## Features
- Real-Time Suggestions: Provides suggestions as users type, utilizing a WebSocket connection for low-latency responses and Reactive/Multithreading programming.
- Popular Prefix Aggregation: Updates the Trie every 24hrs based on the most popular prefixes in HBase, aggregating counts via Spark.
- Scalable Architecture: Leverages microservices architecture with separate services for typeahead suggestions and stream processing.
- Distributed Processing: Uses Kafka for message passing and Spark for processing typed input in micro-batches.
- Dockerized Deployment: Containerized services for easy deployment and scaling.

## Architecture
The application follows a microservices architecture, with the following components:

### Typeahead Service:
Built with Spring Boot and integrates a Trie data structure.
Exposes a WebSocket endpoint to provide real-time suggestions.
Uses Kafka to send typed prefixes to the Spark Streaming Service.

### Kafka:
Acts as a messaging queue between the Typeahead Service and Spark Streaming Service.
Ensures reliable delivery of typed prefixes for processing.

### Spark Streaming Service:
Consumes typed prefixes from Kafka and processes them using micro-batches.
Aggregates counts and updates HBase with the most popular prefixes.
Runs as a separate microservice for scalability.

### Docker:
All components are containerized to simplify deployment and scaling.
Supports running multiple instances of each service to handle increased load.

## Technologies Used
- Spring/Spring Boot: Backend framework for building REST APIs, Websockets and Reactive/Multithreading.
- Apache Kafka: Message broker for reliable message passing.
- Apache Spark: Stream processing for aggregating typed prefixes.
- Trie Data Structure: Efficient data structure for prefix searching.
- HBase: Storage for popular prefix data.
- Docker: Containerization for scalable deployment.
- JUnit/Mockito: Testing framework for unit and integration tests.
- Python: Script used for establishing websocket connection via CLI.

## License
This project uses the following open-source libraries:

- **Spring Boot**: Apache License 2.0 (Spring Boot): https://www.apache.org/licenses/LICENSE-2.0

- **Spring Kafka**: Licensed under the [Apache License 2.0](https://github.com/spring-projects/spring-kafka/blob/main/LICENSE).
