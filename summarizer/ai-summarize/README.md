# AI Summarizer

Spring Boot Command Line application that reads documents from an Alfresco folder in PDF format, gets the summary from AI and updates `cm:description` property of the document with the summary. The Alfresco Repository property to populate with the summary (`cm:description` by default) can be configured using `content.service.summary.property` property in [configuration file](src/main/resources/application.properties). This app also sets a tag with the LLM used.

# Configuration

File [application.properties](src/main/resources/application.properties) includes configuration to connect with Alfresco Repository, target folder and GenAI endpoint. These parameters can be used also as command line arguments.

```
# Alfresco Server
content.service.security.basicAuth.username=admin
content.service.security.basicAuth.password=admin

content.service.url=http://localhost:8080
content.service.path=/alfresco/api/-default-/public/alfresco/versions/1

# ALFRESCO REPOSITORY
# Target folder to fire summarizing process
content.service.folder=/app:company_home/app:shared
# Node property to fill with summary from GenAI Stack
content.service.summary.property=cm:description

# ActiveMQ Server
spring.activemq.brokerUrl=tcp://localhost:61616
spring.jms.cache.enabled=false
alfresco.events.enableSpringIntegration=false
alfresco.events.enableHandlers=true

# GenAI Stack
genai.summary.url=http://localhost:8506/summary
# GenAI Request timeout in seconds
genai.request.timeout=300
```

# Building

Use default Maven command

```
mvn clean package
```

# Running

This program can be run from command line:

```
$ java -jar target/ai-summarize-0.0.8.jar
Started AiSummarizeApplication in 1.143 seconds (process running for 1.417)
Summarizing document 日本.pdf (089b75ba-9f1b-49a1-ac72-0b19ab249b4b)
PDF rendition for document 日本.pdf was not available, it has been requested
```

If PDF rendition is not available at the moment, the program needs to be run again after a while.

```
$ java -jar target/ai-summarize-0.0.8.jar
Started AiSummarizeApplication in 1.026 seconds (process running for 1.284)
Summarizing document 日本.pdf (089b75ba-9f1b-49a1-ac72-0b19ab249b4b)
Document 日本.pdf has been updated with summary and tag
```

After performing the initial action (summarize the documents inside the folder), the program will be listening to `CREATE` events from Alfresco Repository (ActiveMQ). Every new document in the repository will be *summarized* automatically by using the messaging API.