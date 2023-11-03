FROM langchain/langchain

WORKDIR /app

RUN apt-get update && apt-get install -y \
    build-essential \
    curl \
    software-properties-common \
    && rm -rf /var/lib/apt/lists/*

COPY requirements.txt .

RUN pip install --upgrade -r requirements.txt

COPY summarize.py .
COPY utils.py .
COPY chains.py .

HEALTHCHECK CMD curl --fail http://localhost:8506

ENTRYPOINT [ "uvicorn", "summarize:app", "--host", "0.0.0.0", "--port", "8506" ]
