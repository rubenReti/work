# MCP Java Server with Elasticsearch (Spring Boot)

This project implements a Minimal Content Processing (MCP) server using Java Spring Boot, integrated with Elasticsearch for document indexing and search. It supports basic CRUD operations over documents via REST API.

---

## Dockerized Setup

The project runs via `docker-compose`, launching:

- Elasticsearch (v8.13.4)
- Java-based MCP server (Spring Boot 3.2)

---

##  Running the Project

### 1. Build the Java JAR

```bash
cd mcp-java
mvn clean package
```

### 2. Start with Docker Compose

```bash
cd ..
docker compose up --build
```

- MCP Server runs at: [http://localhost:8080](http://localhost:8080)
- Elasticsearch runs at: [http://localhost:9200](http://localhost:9200)

---

##  API Endpoints

### Health Check
```bash
curl http://localhost:8080/ping
```

---

### Create Document

```bash
curl -X POST http://localhost:8080/documents -H "Content-Type: application/json" -d "{"id":"1","title":"Sunset in Amsterdam","content":"Beautiful picture with John."}"
```

---

### Get Document by ID

```bash
curl -X GET http://localhost:8080/documents/1
```

---

### Update Document

```bash
curl -X PUT http://localhost:8080/documents/1 -H "Content-Type: application/json" -d "{"title":"New Title","content":"Updated content."}"
```

---

### Delete Document

```bash
curl -X DELETE http://localhost:8080/documents/1
```

---

### Search Documents

```bash
curl -X GET "http://localhost:8080/documents/search?query=sunset"
```

---

## ️ Project Structure

- `DocumentController.java` – REST endpoints
- `ElasticsearchService.java` – Integration with Elasticsearch
- `DocumentDTO.java` – Data Transfer Object
- `PingController.java` – Health check
- `Dockerfile` – Java MCP service container
- `docker-compose.yml` – Launches ES + Java MCP server

---

##  Features & Requirements Checklist

- [x] Dockerized with `docker-compose`
- [x] Integrated with Elasticsearch 8.13.4
- [x] Full CRUD support (Create, Read, Update, Delete)
- [x] Search functionality
- [x] JSON-based request/response
- [x] HTTP status codes and error handling
- [x] Logs to console

---

##  License

This project is for educational/demo purposes. MIT-style license.
