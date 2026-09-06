# MatchPet

PoC em Java e MongoDB para cadastrar, consultar, atualizar e excluir pets pela linha de comando.

## Primeira entrega

Uma única coleção, `pets`, com documentos simples e homogêneos: nome, espécie e idade, além do identificador.

Esta etapa contém a estrutura Maven, o modelo Pet, o repositório e a configuração MongoDB.

As próximas etapas acrescentam o serviço CRUD e seus testes, depois a CLI com testes e instruções de demonstração. O menu ainda não está disponível nesta etapa.

## Requisitos

- JDK 21.
- MongoDB Community Server em execução para usar a aplicação.

Conexão padrão: `mongodb://localhost:27017/matchpet`.
Para outra conexão, configure a variável de ambiente `MONGODB_URI`.

## Compilar

Na pasta que contém `pom.xml`, execute no PowerShell:

```powershell
.\mvnw.cmd clean verify
```

Nesta etapa o comando compila e gera o JAR; os testes serão acrescentados nas próximas contribuições.
