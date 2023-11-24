const express = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');

const app = express();

// Configure the proxy middleware
app.use(
  '/api',
  createProxyMiddleware({
    target: 'http://127.0.0.1:8080', // Replace with your API server's URL
    changeOrigin: true,
  })
);

const port = process.env.PORT || 3001; // Choose a port for the proxy server

app.listen(port, () => {
  console.log(`API proxy server is running on port ${port}`);
});
