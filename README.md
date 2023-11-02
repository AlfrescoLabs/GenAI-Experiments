# Automatic Categorisation of Content using an LLM

## Configuration

To get running with the project then you need to have access to an API for an LLM. Currently the LLMs from the GenAI project and ChatGPT from OpenAI are supported.

Copy `config.ini.sample` to `config.ini` and then fill in the appropriate details. Note that if you only want to use one LLM provider then you can leave the example settings for the other.

## Workings

### GenAI

Instructions on how to get started with the GenAI stack are provided here: https://github.com/docker/genai-stack

Note that to use the GPU on Linux then you need to update the docker-compose file by following the instructions here: https://github.com/docker/genai-stack/issues/62#issuecomment-1773872680

### Creating Categories

The LLM is asked to provide categories for a sample of the documents in the repository and then these are consolidated into a hierarchy. The categories are created in the Repository using the v1 API.

### Categorising Content

The category tree is provided to the LLM along with the content of the document. The LLM is asked to select appropriate categories for the content and then the v1 API is used to assign them.
