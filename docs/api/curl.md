<div style="display:flex; align-items:center; width:100%; gap:12px;">
  <img src="../assets/images/avenir-esr-logo_medium.jpg" alt="Avenir ESR Logo" height="48" />
  <div style="flex:1; text-align:center;">
    <h1 style="margin:0;">ePortFolio ESR</h1>
  </div>
  <div style="width:48px;"></div>
</div>

---

## API curl examples

Common requests you can run against the security microservice.

Note: update the host and port to match your environment.

## Login

```bash
 curl -X POST "http://localhost:12000/oidc/login" \
 -H "Content-Type: application/json" \
 -d '{"login":"aabaida","password":"Azerty123"}'

Response: 
eyJhbGciOiJSUzUxMiIsInR5cCI6IkpXVCIsImtpZCI6ImNhcy1YRlhsaEZabCIsIm9yZy5hcGVyZW8uY2FzLnNlcnZpY2VzLlJlZ2lzdGVyZWRTZXJ2aWNlIjoiNDAwMCJ9.eyJhdWQiOiJBUElNQ2xpZW50SWQiLCJzdWIiOiJhYWJhaWRhIiwiaXNzIjoiaHR0cHM6Ly9sb2NhbGhvc3QvY2FzL29pZGMiLCJnaXZlbl9uYW1lIjoiREFOIiwiZXhwIjoxNzY5NjQ1MDY5LCJpYXQiOjE3Njk2MTYyNjksImZhbWlseV9uYW1lIjoiQUFCQUlEQSIsImp0aSI6IkFULTctaUtnb1oycE43OGZmUnVxV1VzcGNKdXYtc2N6UFpkZzQiLCJlbWFpbCI6ImFrc2lyaW5AZXRhYi5mciJ9.TVMNcym5lpU1TQh0crRyA5zJjQtoal_c1jCGw5CbGBmO8A8DQBwjRF2W5VLJNeh0JX86NmHvyomSgVezeLA6GStzuQm7k8w-R9QOosnedJMMAy9AB_NoCpmMqb-gHz16gPrqah1FMXv8kwrAzPG5pnm5LyIi4oxitfm5fdCHVAz8Mu2deytU6duXyS6pOwWzBNQpYJXEJbIFVhzFKThNJmuIdz7VTtxPQW4-2zpcv0yBo3F815sUIEnLCT2i6FnMVhp4oA_jWv9KYZQffqsP6MBgvK_C06Bo_9D3j6p2YF4XeA9J72qMnxD35y-UA5iuCCCbGurF3RMQGj3sin4vW
```
