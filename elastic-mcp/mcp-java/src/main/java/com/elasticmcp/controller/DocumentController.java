package com.elasticmcp.controller;

import com.elasticmcp.dto.DocumentDTO;
import com.elasticmcp.service.ElasticsearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    @Autowired
    private ElasticsearchService elasticsearchService;

    @PostMapping
    public String createDocument(@RequestBody DocumentDTO document) {
        try {
            return elasticsearchService.indexDocument(document);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error indexing document: " + e.getMessage();
        }
    }
    
    
    @GetMapping("/{id}")
    public Object getDocumentById(@PathVariable("id") String id) {
        try {
            DocumentDTO doc = elasticsearchService.getDocumentById(id);
            if (doc == null) {
                return "Document not found with id: " + id;
            }
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error retrieving document: " + e.getMessage();
        }
    }
    
    @PutMapping("/{id}")
    public String updateDocument(@PathVariable("id") String id,
                                 @RequestBody DocumentDTO document) {
        try {
            return elasticsearchService.updateDocument(id, document); // 👈 clean
        } catch (Exception e) {
            e.printStackTrace();
            return "Error updating document: " + e.getMessage();
        }
    }


    @DeleteMapping("/{id}")
    public String deleteDocument(@PathVariable("id") String id) {
        try {
            return elasticsearchService.deleteDocument(id);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error deleting document: " + e.getMessage();
        }
    }

    
    
    @GetMapping("/search")
    public Object searchDocuments(@RequestParam("q") String query) {
        try {
            return elasticsearchService.searchDocuments(query);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error searching documents: " + e.getMessage();
        }
    }

    
    
    
}
