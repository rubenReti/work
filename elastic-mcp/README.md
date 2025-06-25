# MCP Server with Elasticsearch – Java + Node.js

This project implements a Minimal Content Processing (MCP) server with Elasticsearch integration using **both Java (Spring Boot)** and **Node.js (Express)**. Both implementations support basic CRUD operations on documents via REST API.

---

## Dockerized Setup

The project uses `docker-compose` to launch:

- **Elasticsearch** (v8.13.4)
- **Java MCP Server** (Spring Boot 3.2)
- **Node.js MCP Server** (Node.js 22 Alpine)

All services run inside containers and share a common network.

---

## Running the Project

### 1. Build Java Project

```bash
cd mcp-java
mvn clean package
```

### 2. Build and Launch with Docker Compose

```bash
cd ..
docker-compose build --no-cache
docker-compose up
```

---

## Service Endpoints

### Java MCP Server
- **Base URL**: `http://localhost:8080`

### Node.js MCP Server
- **Base URL**: `http://localhost:3000`

### Elasticsearch
- **URL**: `http://localhost:9200`

---

## Java API Endpoints

### Health Check

```bash
curl http://localhost:8080/ping
```

### Create Document

```bash
curl -X POST http://localhost:8080/documents -H "Content-Type: application/json" -d "{\"id\":\"1\",\"title\":\"Sunset\",\"content\":\"Nice view at the lake.\"}"
```

### Get Document by ID

```bash
curl http://localhost:8080/documents/1
```

### Update Document

```bash
curl -X PUT http://localhost:8080/documents/1 -H "Content-Type: application/json" -d "{\"title\":\"Updated Title\",\"content\":\"Updated content.\"}"
```

### Delete Document

```bash
curl -X DELETE http://localhost:8080/documents/1
```

### Search Documents

```bash
curl "http://localhost:8080/documents/search?q=lake"
```

---

## Node.js API Endpoints

### Health Check

```bash
curl http://localhost:3000/ping
```

### Create Document

```bash
curl -X POST http://localhost:3000/documents -H "Content-Type: application/json" -d "{\"id\":\"1\",\"title\":\"Sunset\",\"content\":\"Nice view at the lake.\"}"
```

### Get Document by ID

```bash
curl http://localhost:3000/documents/1
```

### Update Document

```bash
curl -X PUT http://localhost:3000/documents/1 -H "Content-Type: application/json" -d "{\"title\":\"Updated Title\",\"content\":\"Updated content.\"}"
```

### Delete Document

```bash
curl -X DELETE http://localhost:3000/documents/1
```

### Search Documents

```bash
curl "http://localhost:3000/documents/search?query=lake"
```

### Raw Elasticsearch Query (Node.js debug endpoint)

```bash
curl "http://localhost:3000/documents/raw-es?query=lake"
```

---

## Project Structure

- `mcp-java/` – Java Spring Boot service
  - `DocumentController.java`
  - `ElasticsearchService.java`
  - `DocumentDTO.java`
  - `PingController.java`
  - `Dockerfile`

- `mcp-node/` – Node.js Express service
  - `server.js`
  - `package.json`
  - `Dockerfile`

- `docker-compose.yml` – Launches both services + Elasticsearch

---

## Features & Requirements Checklist 

- [x] Dockerized with `docker-compose`
- [x] Integrated with Elasticsearch 8.13.4
- [x] Java MCP implementation (Spring Boot)
- [x] Node.js MCP implementation (Express)
- [x] Full CRUD operations in both servers
- [x] Search support
- [x] JSON-based communication
- [x] Proper error handling
- [x] Logging and health checks

---

## License

This project is for educational/demo purposes. MIT-style license.
