const express = require('express');
const { Client } = require('@elastic/elasticsearch');
const bodyParser = require('body-parser');
const app = express();
const port = 3000;

app.use(bodyParser.json());

const esClient = new Client({ node: 'http://elasticsearch:9200' });
const INDEX = 'documents';

// Create
app.post('/documents', async (req, res) => {
  const { id, title, content } = req.body;
  try {
    await esClient.index({
      index: INDEX,
      id,
      document: { id, title, content }
    });
    await esClient.indices.refresh({ index: INDEX });
    res.json({ message: 'Document created' });
  } catch (error) {
    res.status(500).json({ error: 'Failed to create document', detail: error.message });
  }
});

// Read
app.get('/documents/:id', async (req, res) => {
  try {
    const result = await esClient.get({ index: INDEX, id: req.params.id });
    res.json(result._source);
  } catch (error) {
    if (error.meta?.statusCode === 404) {
      res.status(404).json({ error: 'Document not found' });
    } else {
      res.status(500).json({ error: 'Error fetching document', detail: error.message });
    }
  }
});

// Update
app.put('/documents/:id', async (req, res) => {
  const { title, content } = req.body;
  try {
    await esClient.update({
      index: INDEX,
      id: req.params.id,
      doc: { title, content }
    });
    res.json({ message: 'Document updated' });
  } catch (error) {
    if (error.meta?.statusCode === 404) {
      res.status(404).json({ error: 'Document not found' });
    } else {
      res.status(500).json({ error: 'Error updating document', detail: error.message });
    }
  }
});

// Delete
app.delete('/documents/:id', async (req, res) => {
  try {
    await esClient.delete({ index: INDEX, id: req.params.id });
    res.json({ message: 'Document deleted' });
  } catch (error) {
    if (error.meta?.statusCode === 404) {
      res.status(404).json({ error: 'Document not found' });
    } else {
      res.status(500).json({ error: 'Error deleting document', detail: error.message });
    }
  }
});

// Search
app.get('/documents/search', async (req, res) => {
  const query = req.query.query;
  try {
    const result = await esClient.search({
      index: INDEX,
      query: {
        multi_match: {
          query,
          fields: ['title', 'content']
        }
      }
    });

    const hits = result.hits.hits.map(hit => hit._source);
    if (hits.length === 0) {
      res.status(404).json({ error: 'Document not found' });
    } else {
      res.json(hits);
    }
  } catch (error) {
    res.status(500).json({ error: 'Search error', detail: error.message });
  }
});


app.put('/documents/:id', async (req, res) => {
  const { title, content } = req.body;

  try {
    await esClient.update({
      index: INDEX,
      id: req.params.id,
      doc: { title, content },
    });

    res.json({ message: 'Document updated' });
  } catch (error) {
    if (error.meta?.statusCode === 404) {
      res.status(404).json({ error: 'Document not found' });
    } else {
      res.status(500).json({ error: 'Error updating document', detail: error.message });
    }
  }
});




// Elasticsearch info - debug
app.get('/es-info', async (req, res) => {
  try {
    const info = await esClient.info();
    res.json(info);
  } catch (error) {
    res.status(500).json({ error: 'Elasticsearch info error', detail: error.message });
  }
});

app.listen(port, () => {
  console.log(`MCP server listening on port ${port}`);
});
