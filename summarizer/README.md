# AI Summarizer for Alfresco (PoC)

This project includes a PoC to produce summaries using GenAI for Alfresco documents.

# Description

The project includes following components:

* [genai-stack](genai-stack) folder is using https://github.com/docker/genai-stack project to build a REST endpoint that provides a summary from a PDF file
* [alfresco](alfresco) folder includes a Docker Compose template to deploy a regular Alfresco Community 7.4
* [ai-summarize](ai-summarize) folder includes a Spring Boot Command Line program that gets documents from Alfresco, request the summary to GenAI and updates the document in Alfresco with the summary

```
┌────────────────┐                                   ┌─────────────────┐
│                │ tcp://localhost:61616             │                 │
│                │ (async)                           │                 │ 
│  Alfresco      │─────────────────┐                 │   GenAI         │
│                │                 │                 │                 │
└───────┬────────┘                 │                 └─────────▲───────┘
        │                          │ NODE                      │
        │                          │                           │
        │ FOLDER                   │                           │
        │                 ┌────────v────────┐                  │
        │                 │                 │                  │
        └────────────────►│                 ├──────────────────┘
   http://localhost:8080  │  ai-summarizer  │  http://localhost:8506
   (sync)                 │                 │
                          └─────────────────┘
```

# Configuration

[GenAI Stack](genai-stack) supports language and summary length configuration. Modify environment variables in `.env` file to specify your preferences.

```
SUMMARY_LANGUAGE=Brazilian # Any language name supported by LLM
SUMMARY_SIZE=120 # Number of words for the summary
```

# Deploying

Start GenAI service

```
$ cd genai-stack
$ docker compose up --build --force-recreate
```

Start Alfresco

```
$ cd alfresco
$ docker compose up --build --force-recreate
```

# Running

Once GenAi and Alfresco are up & running, upload some documents to Alfresco `Shared Files` folder and run the `ai-summarize` program to update `description` property with the summary of the document.

```
$ cd ai-summarize
$ java -jar target/ai-summarize-0.0.8.jar
```

Every time a new document is uploaded to Alfresco, the summary will be updated listening to the ActiveMQ messages.