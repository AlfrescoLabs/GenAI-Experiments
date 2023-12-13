# Docker Compose

Alfresco 7.4 deployment

# Running

Default credentials are `admin`/`admin`

**To start everything**

```
docker compose up
```

**Deploy Custom Content Model in Share**

Open Share UI in your browser http://localhost:8080/share

Use the option `Admin Tools > Model Manager` to impor the model contained in `prompt-model.zip` file. Remember to `Active` the model.

Add aspect `prompt:promptable` to every document you want to use for sending questions to GenAI Stack.

**Shutdown**

```
docker compose down
```

# Applications

* Repository http://localhost:8080/alfresco
* Share UI http://localhost:8080/share