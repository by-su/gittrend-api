# Gittrend

> A high-performance GitHub analytics platform providing real-time repository insights, trend analysis, and developer activity tracking powered by Elasticsearch and Spring WebFlux.

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.25-7F52FF?logo=kotlin)](https://kotlinlang.org)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.8-6DB33F?logo=spring)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk)](https://openjdk.org/)
[![Elasticsearch](https://img.shields.io/badge/Elasticsearch-8.x-005571?logo=elasticsearch)](https://www.elastic.co/)

## Overview

Gittrend is a production-ready backend service that aggregates and analyzes GitHub repository data at scale.
It collects real-time events from GitHub's API, processes them through an event-driven architecture, and provides powerful search and analytics capabilities through Elasticsearch integration.

### Key Features

- **Real-time Event Processing**: Asynchronous GitHub event collection and processing using reactive streams
- **Advanced Repository Search**: Full-text search with Elasticsearch, supporting complex queries and filters
- **Statistical Analytics**: Time-series data aggregation for repository metrics (hourly/daily)
- **Trend Analysis**: Language and topic clustering with automated categorization
- **High Performance**: Reactive programming with Spring WebFlux and optimized caching strategies

## Architecture

### Tech Stack

**Backend Framework**
- Kotlin 1.9.25 with Coroutines
- Spring Boot 3.5.8 (WebFlux for reactive programming)
- Java 21

**Data Layer**
- MySQL (primary data store)
- Elasticsearch 8.x (search and analytics)
- Flyway (schema versioning and migrations)
- Spring Data JPA with Hibernate

**Caching & Performance**
- Caffeine (in-memory cache)
- Multi-level caching strategy with configurable TTL
- Batch processing for database operations

**Infrastructure**
- Docker & Docker Compose
- SSL/TLS encryption for Elasticsearch
- CORS-enabled REST API

**Testing**
- JUnit 5
- MockK (Kotlin mocking framework)
- Reactor Test for reactive streams

### System Design

```
┌─────────────────┐
│  GitHub API     │
└────────┬────────┘
         │
         ▼
┌─────────────────────────────────────────┐
│  Spring WebFlux Controllers             │
│  - Event Collection                     │
│  - Repository Search                    │
│  - Statistics Endpoints                 │
└────────┬────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────┐
│  Event-Driven Processing Layer          │
│  - Event Listeners                      │
│  - Async Processors                     │
│  - Channel-based Communication          │
└────────┬────────────────────────────────┘
         │
    ┌────┴────┐
    ▼         ▼
┌─────────┐ ┌──────────────┐
│  MySQL  │ │ Elasticsearch│
│         │ │   (Search)   │
│ Events  │ │   Documents  │
│ Stats   │ │   Indexing   │
│ Metadata│ │              │
└─────────┘ └──────────────┘
```
## License

This project is developed as a portfolio piece by by-su.
