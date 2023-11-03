# GenAI Stack

This project is based in https://github.com/docker/genai-stack

# Configure

MacOS and Linux users can use any LLM that's available via Ollama. 

Check the "tags" section under the model page you want to use on https://ollama.ai/library and write the tag for the value of the environment variable `LLM=` in the `.env` file.

All platforms can use GPT-3.5-turbo and GPT-4 (bring your own API keys for OpenAIs models).

**MacOS**
Install [Ollama](https://ollama.ai) on MacOS and start it before running `docker compose up`.

**Linux**
No need to install Ollama manually, it will run in a container as
part of the stack when running with the Linux profile: run `docker compose --profile linux up`.
Make sure to set the `OLLAMA_BASE_URL=http://llm:11434` in the `.env` file when using Ollama docker container.

**Windows**
Not supported by Ollama, so Windows users need to generate a OpenAI API key and configure the stack to use `gpt-3.5` or `gpt-4` in the `.env` file.

# Develop

> [!WARNING]
> There is a performance issue that impacts python applications in the latest release of Docker Desktop. Until a fix is available, please use [version `4.23.0`](https://docs.docker.com/desktop/release-notes/#4230) or earlier.

**To start everything**
```
docker compose up
```
If changes to build scripts has been made, **rebuild**.
```
docker compose up --build
```

**Shutdown**
If health check fails or containers don't start up as expected, shutdown
completely to start up again.
```
docker compose down
```

# Applications

The database can be explored at http://localhost:7474.

## App - Summarizer API

Endpoints: 

  - POST http://localhost:8506/summary

Example cURL command:

```bash
curl --location 'http://localhost:8506/summary' \
--form 'file=@"/tmp/document.pdf"'
```

Exposes the functionality to get a summary as text from a PDF file.