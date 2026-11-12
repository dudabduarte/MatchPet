# MatchPet 🐾

## 1. Sobre o projeto

O **MatchPet** é uma Prova de Conceito (PoC) desenvolvida para facilitar o processo de adoção de animais, aproximando pessoas interessadas em adotar de animais que precisam de um novo lar.

A aplicação permite visualizar animais cadastrados e realizar operações relacionadas ao gerenciamento dos pets. A proposta é utilizar a tecnologia para contribuir com a adoção responsável, incentivar o cuidado com os animais e ajudar a reduzir o abandono.

O projeto foi desenvolvido como parte da **Atividade de Estudo Programada (AEP) 2026.2**, do curso de Engenharia de Software.

## 2. Problema e Objetivo de Desenvolvimento Sustentável (ODS)

O abandono de animais e a dificuldade de encontrar pessoas interessadas em adoções responsáveis são problemas que afetam a sociedade.

O MatchPet busca contribuir para essa realidade por meio de uma plataforma que centraliza informações sobre animais disponíveis para adoção, facilitando o acesso e a divulgação desses animais.

**ODS 15 – Vida Terrestre**

O projeto relaciona-se ao ODS 15 da Organização das Nações Unidas, especialmente à conscientização sobre a proteção da vida animal e à promoção de práticas responsáveis de cuidado e preservação.

## 3. Objetivos do sistema

- Permitir o cadastro e a visualização de animais.
- Armazenar os dados dos animais em um banco de dados NoSQL.
- Manter as informações cadastradas mesmo após o encerramento da aplicação.
- Organizar os dados em diferentes coleções.
- Representar relacionamentos entre usuários, animais e adoções.
- Demonstrar operações de persistência e manipulação de documentos MongoDB.

## 4. Tecnologias utilizadas

| Tecnologia | Aplicação |
|---|---|
| Java 21 | Desenvolvimento do backend |
| Spring Boot 3.5.16 | Estrutura da aplicação e API |
| Spring Data MongoDB | Integração com o banco NoSQL |
| MongoDB Community Server | Armazenamento dos dados |
| HTML | Estrutura das páginas |
| CSS | Estilização da interface |
| JavaScript | Interatividade e comunicação com a API |
| Maven | Gerenciamento de dependências e execução |
| JUnit | Testes automatizados |
| JaCoCo | Medição da cobertura dos testes |
| Git e GitHub | Versionamento do código |

## 5. Arquitetura do projeto

O sistema utiliza uma arquitetura dividida entre frontend, backend e banco de dados.

O frontend é responsável pela interface apresentada ao usuário. O backend, desenvolvido em Java com Spring Boot, disponibiliza os endpoints da API e concentra as regras de negócio. O MongoDB é responsável pela persistência das informações.

Fluxo geral:

```text
Frontend (HTML, CSS e JavaScript)
              |
              v
        API REST (Spring Boot)
              |
              v
          Services
              |
              v
        Repositories
              |
              v
       MongoDB (NoSQL)
```

A organização do backend utiliza classes com responsabilidades separadas, seguindo os princípios da programação orientada a objetos.

## 6. Banco de dados NoSQL

O projeto utiliza o **MongoDB**, um banco de dados orientado a documentos. As informações são armazenadas em documentos BSON, organizados em coleções.

O banco utilizado pela aplicação chama-se `matchpet`.

### 6.1 Coleções

A modelagem da segunda entrega prevê as seguintes coleções:

- `pets`: informações dos animais cadastrados.
- `usuarios`: informações das pessoas cadastradas na plataforma.
- `adocoes`: registros das solicitações ou processos de adoção.

### 6.2 Estrutura dos documentos

Os documentos podem conter campos simples, objetos aninhados e listas de subdocumentos, conforme a necessidade de cada entidade.

Exemplo de documento da coleção `pets`:

```json
{
  "_id": "67c123...",
  "nome": "Luna",
  "especie": "Cachorro",
  "idade": 3,
  "localizacao": {
    "cidade": "Maringá",
    "estado": "PR"
  },
  "caracteristicas": [
    "Dócil",
    "Brincalhona",
    "Vacinada"
  ]
}
```

Exemplo de documento da coleção `usuarios`:

```json
{
  "_id": "67c456...",
  "nome": "Maria",
  "email": "maria@email.com"
}
```

Exemplo de documento da coleção `adocoes`:

```json
{
  "_id": "67c789...",
  "usuarioId": "67c456...",
  "petId": "67c123...",
  "status": "Pendente"
}
```

Os campos `usuarioId` e `petId` representam referências aos documentos das coleções `usuarios` e `pets`, respectivamente.

**Observação:** os exemplos demonstram a modelagem proposta. A documentação final deve refletir os campos e as operações efetivamente implementados no código.

## 7. Relacionamento entre as coleções

O relacionamento entre as coleções ocorre por meio dos identificadores dos documentos.

- Um usuário pode estar associado a diferentes registros de adoção.
- Um animal pode estar associado a um registro de adoção.
- Cada registro de adoção identifica o usuário e o animal envolvidos.

Representação simplificada:

```text
USUARIOS
   |
   | usuarioId
   v
ADOCOES
   ^
   | petId
   |
PETS
```

Essa organização permite relacionar documentos de coleções diferentes sem utilizar relacionamentos tradicionais de bancos de dados relacionais.

## 8. Funcionalidades

As funcionalidades do sistema incluem:

- Cadastro de animais.
- Listagem dos animais cadastrados.
- Busca de animais.
- Atualização dos dados dos animais.
- Exclusão de animais.
- Persistência dos dados no MongoDB.
- Comunicação entre frontend e backend por meio de API REST.

As funcionalidades de usuários e adoções devem ser documentadas conforme sua implementação na versão final.

## 9. Estrutura do projeto

A estrutura principal do projeto está organizada da seguinte forma:

```text
MatchPet/
├── docs/
│   └── integracao-api.md
├── matchpet-site/
│   ├── images/
│   ├── index.html
│   ├── script.js
│   └── styles.css
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/matchpet/
│   │   │       ├── controller/
│   │   │       ├── model/
│   │   │       ├── repository/
│   │   │       ├── service/
│   │   │       └── MatchPetApplication.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
├── pom.xml
└── README.md
```

## 10. Requisitos para execução

Para executar o projeto, é necessário ter instalado:

- JDK 21.
- MongoDB Community Server.
- Git (opcional, para clonar o repositório).

O MongoDB Community Server deve estar instalado e em execução antes de iniciar o backend.

### 10.1 Configuração do banco de dados

Por padrão, a aplicação utiliza a seguinte conexão:

```text
mongodb://localhost:27017/matchpet
```

A variável de ambiente `MONGODB_URI` pode ser utilizada para configurar outra conexão.

No PowerShell:

```powershell
$env:MONGODB_URI = "mongodb://localhost:27017/matchpet"
```

### 10.2 Clonar o repositório

```powershell
git clone https://github.com/Livia-Maschietto-Boneti/MatchPet_.git
```

Entre na pasta do projeto:

```powershell
cd MatchPet_
```

### 10.3 Executar o backend

No Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

No Linux ou macOS:

```bash
./mvnw spring-boot:run
```

A API ficará disponível em:

```text
http://localhost:8080
```

O endpoint de animais é:

```text
http://localhost:8080/api/pets
```

### 10.4 Executar o frontend

Abra o arquivo `matchpet-site/index.html` no navegador ou utilize um servidor local para disponibilizar os arquivos da interface.

## 11. Testes automatizados

O projeto utiliza testes automatizados para verificar o comportamento das classes e funcionalidades implementadas.

Os testes são executados por meio do Maven.

No Windows:

```powershell
.\mvnw.cmd clean verify
```

No Linux ou macOS:

```bash
./mvnw clean verify
```

A execução verifica os testes automatizados e gera o relatório de cobertura configurado pelo JaCoCo.

### 11.1 Relatório de cobertura

Após a execução do comando, o relatório HTML do JaCoCo pode ser consultado em:

```text
target/site/jacoco/index.html
```

A cobertura mínima exigida pela AEP é de **70% sobre o código da PoC**.

O percentual obtido deve ser conferido no relatório gerado e informado nesta documentação após a execução dos testes da versão final.

**Cobertura obtida:** 76%.

## 12. Versionamento

O código-fonte do MatchPet está disponível no GitHub:

https://github.com/dudabduarte/MatchPet

O projeto utiliza Git para controle de versão e registro das alterações realizadas durante o desenvolvimento.

**Versão da segunda entrega:** https://github.com/dudabduarte/MatchPet

## 13. Metodologia de trabalho

O desenvolvimento foi organizado de maneira incremental, com implementação, testes e ajustes realizados ao longo das etapas do projeto, utilizando a metodologia Kanban.

As atividades devem ser acompanhadas por meio de um quadro de tarefas, permitindo visualizar:

- Atividades pendentes.
- Atividades em andamento.
- Atividades concluídas.

## 14. Resultados esperados

O MatchPet busca demonstrar a aplicação prática de conceitos de Engenharia de Software, programação orientada a objetos e persistência de dados em bancos NoSQL.

Com a evolução da PoC, espera-se apresentar uma aplicação funcional, com múltiplas coleções, documentos complexos, relacionamentos entre entidades, testes automatizados e documentação suficiente para instalação, execução e avaliação do projeto.

## 15. Metodologia utilizada
# Quadro de Tarefas – Kanban | MatchPet

**Projeto:** MatchPet  
**Disciplina:** Engenharia de Software  
**Atividade:** AEP 2026.2 – Segunda Entrega  
**Metodologia:** Kanban

## 16. Concluído

| ID | Tarefa | Descrição | Status |
|---|---|---|---|
| K01 | Implementar múltiplas coleções | Organização das coleções `usuarios`, `pets` e `adocoes`. | Concluído |
| K02 | Implementar relacionamentos | Relacionamento entre usuários, animais e adoções por identificadores. | Concluído |
| K03 | Implementar documentos complexos | Utilização de objetos aninhados e listas de subdocumentos. | Concluído |
| K04 | Desenvolver funcionalidades de usuários | Implementação do gerenciamento de usuários. | Concluído |
| K05 | Desenvolver funcionalidades de adoção | Implementação do gerenciamento das solicitações de adoção. | Concluído |
| K06 | Validar a persistência | Verificação do armazenamento dos dados após reiniciar a aplicação. | Concluído |
| K07 | Ampliar os testes automatizados | Implementação de testes para as funcionalidades do sistema. | Concluído |
| K08 | Atingir cobertura mínima de 70% | Verificação da cobertura dos testes por meio do JaCoCo. | Concluído |
| K09 | Atualizar o README | Documentação das tecnologias, arquitetura, banco de dados e execução. | Concluído |
| K10 | Organizar a documentação técnica | Organização dos documentos necessários para avaliação. | Concluído |
| K11 | Organizar o quadro de tarefas | Registro e acompanhamento das atividades do projeto. | Concluído |
| K12 | Preparar o vídeo de demonstração | Preparação da apresentação da versão final da PoC. | Concluído |
| K13 | Identificar a versão final | Identificação da versão correspondente à segunda entrega no GitHub. | Concluído |
| K14 | Configurar o MongoDB local | Configuração do ambiente MongoDB Community Server. | Concluído |
| K15 | Validar a conexão com o backend | Comunicação entre Spring Boot e MongoDB. | Concluído |
| K16 | Validar a integração frontend e backend | Testes de comunicação entre a interface e a API. | Concluído |
| K17 | Criar o repositório GitHub | Criação e organização do repositório do projeto. | Concluído |
| K18 | Desenvolver a estrutura inicial do backend | Implementação da aplicação Java com Spring Boot. | Concluído |
| K19 | Implementar o modelo Pet | Criação da estrutura de dados dos animais. | Concluído |
| K20 | Implementar PetRepository | Integração com Spring Data MongoDB. | Concluído |
| K21 | Implementar PetService | Organização das operações e regras de negócio. | Concluído |
| K22 | Implementar PetController | Disponibilização dos endpoints REST. | Concluído |
| K23 | Integrar frontend e API | Comunicação entre JavaScript e backend. | Concluído |
| K24 | Implementar operações CRUD de pets | Cadastro, listagem, busca, atualização e exclusão. | Concluído |
| K25 | Implementar testes automatizados | Criação dos testes unitários do projeto. | Concluído |
| K26 | Versionar as alterações | Registro e envio das alterações para o GitHub. | Concluído |

## 17. Resultado do quadro

Todas as atividades estão registradas na coluna **Concluído**, representando o encerramento das tarefas planejadas para o desenvolvimento do MatchPet na AEP 2026.2.

**Status geral do projeto:** Concluído.
