# PulseWatch https://pulsewatch-itds.onrender.com/

PulseWatch is an API monitoring and AI-powered incident analysis
application built with Java and Spring Boot.

It automatically monitors service availability, detects incidents,
and generates AI-powered summaries of outages and recoveries.

## Features

- Automated API health checks
- HTTP status and response-time monitoring
- Incident detection after three consecutive failures
- Automatic incident recovery detection
- AI-generated incident summaries using Ollama or Groq
- Interactive monitoring dashboard
- Protected failure and recovery simulation controls
- Docker support

## Tech Stack

**Backend:** Java 21, Spring Boot, Maven

**Frontend:** HTML, CSS, JavaScript

**AI:** Ollama (local), Groq (cloud)

**Deployment:** Docker, Render

## How It Works

1. PulseWatch checks the monitored service every five seconds.
2. Three consecutive failures trigger an incident.
3. The selected AI provider generates an incident summary.
4. The dashboard displays monitoring and incident information.
5. Once the service recovers, the incident is resolved and
   its AI summary is updated.

## Running Locally

Requirements:
- Java 21+
- Ollama with the llama3.2:3b model, or a Groq API key

Start the application:

```bash
./mvnw spring-boot:run
```

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Open the dashboard at localhost:8080.

## Environment Variables

| Variable | Description |
|---|---|
| AI_PROVIDER | ollama or groq |
| GROQ_API_KEY | Groq API key |
| DEMO_TOKEN | Secret token protecting demo controls |
| PORT | Application port (default: 8080) |
| DEMO_URL | Optional monitored demo URL |

Never commit API keys or access tokens.

## Deployment

PulseWatch supports Docker deployment.

The initial cloud deployment uses Render for hosting and
Groq for AI-generated incident summaries.

## Current Limitations

- Monitors one demonstration service
- Incident information is stored in memory
- Data disappears when the application restarts
- Free hosting does not provide uninterrupted monitoring

PulseWatch is currently an MVP intended for demonstration
and further development.
