package com.elasticmcp.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import java.util.List;
import java.util.stream.Collectors;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;

import com.elasticmcp.dto.DocumentDTO;

import java.io.IOException;

@Service
public class ElasticsearchService {

    private final ElasticsearchClient client;

    public ElasticsearchService() {
        String host = System.getenv().getOrDefault("ELASTICSEARCH_HOST", "localhost");
        //In dev: it connects to localhost - In Docker: we’ll pass ELASTICSEARCH_HOST=elasticsearch (the service name)



        RestClient restClient = RestClient.builder(
            new HttpHost(host, 9200)
        ).build();

        RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        this.client = new ElasticsearchClient(transport);
    }

    
    //POST
    public String indexDocument(DocumentDTO doc) throws IOException, ElasticsearchException {
        IndexRequest<DocumentDTO> request = IndexRequest.of(i -> i
            .index("documents")
            .id(doc.getId())
            .document(doc)
        );

        IndexResponse response = client.index(request);
        return response.result().jsonValue();
    }
    
    
    //GET
    public DocumentDTO getDocumentById(String id) throws IOException {
        return client.get(g -> g
            .index("documents")
            .id(id),
            DocumentDTO.class
        ).source();
    }
    
    
    public String updateDocument(String id, DocumentDTO doc) throws IOException {
        // use ID from param only
        IndexRequest<DocumentDTO> request = IndexRequest.of(i -> i
            .index("documents")
            .id(id) // 👈 safe, explicit
            .document(new DocumentDTO(null, doc.getTitle(), doc.getContent()))
        );

        IndexResponse response = client.index(request);
        return response.result().jsonValue();
    }

    
    public String deleteDocument(String id) throws IOException {
        return client.delete(d -> d
            .index("documents")
            .id(id)
        ).result().jsonValue();
    }




    
    
    public List<DocumentDTO> searchDocuments(String query) throws IOException {
        SearchResponse<DocumentDTO> response = client.search(s -> s
            .index("documents")
            .query(q -> q
                .multiMatch(m -> m
                    .query(query)
                    .fields("title", "content")
                )
            ),
            DocumentDTO.class
        );

        return response.hits().hits()
            .stream()
            .map(Hit::source)
            .collect(Collectors.toList());
    }
}
