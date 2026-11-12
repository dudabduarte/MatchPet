# API REST do MatchPet

<<<<<<< HEAD
O backend disponibiliza uma API REST em `http://localhost:8080`.

## Pets
=======
O backend agora disponibiliza uma API REST em `http://localhost:8080/api/pets`.

## Rotas
>>>>>>> fddfdd0bbb3541e3e55a421703161c20c15c3a2d

| Método | Rota | Ação |
|---|---|---|
| GET | `/api/pets` | Lista os pets por nome |
| GET | `/api/pets/{id}` | Busca um pet |
| POST | `/api/pets` | Cadastra um pet |
| PUT | `/api/pets/{id}` | Atualiza um pet |
| DELETE | `/api/pets/{id}` | Exclui um pet |

<<<<<<< HEAD
## Adoções

| Método | Rota | Ação |
|---|---|---|
| POST | `/api/adocoes` | Cria uma solicitação de adoção |
| GET | `/api/adocoes` | Lista todas as solicitações |
| GET | `/api/adocoes/{id}` | Busca uma solicitação por ID |
| GET | `/api/adocoes/usuario/{usuarioId}` | Lista solicitações de um usuário |
| GET | `/api/adocoes/pet/{petId}` | Lista solicitações de um pet |
| PATCH | `/api/adocoes/{id}/status` | Aprova ou rejeita uma solicitação pendente |

### Criar solicitação

`POST /api/adocoes`

```json
{
  "usuarioId": "ID_DO_USUARIO",
  "petId": "ID_DO_PET"
}
```

A data é gerada pelo backend e o status inicial é `PENDENTE`.

### Aprovar ou rejeitar

`PATCH /api/adocoes/ID_DA_SOLICITACAO/status`

```json
{
  "status": "APROVADA"
}
```

O campo `status` aceita `APROVADA` ou `REJEITADA`. Apenas solicitações `PENDENTE` podem ser atualizadas.

### Regras de negócio

- O usuário e o pet informados precisam existir.
- Um usuário não pode manter mais de uma solicitação pendente ou aprovada para o mesmo pet.
- Um pet que já tenha uma adoção aprovada não pode receber outra solicitação.
- Um pet não pode ter duas solicitações aprovadas.
- Solicitações rejeitadas permanecem no banco como histórico; uma nova solicitação poderá ser criada.
- Nesta versão acadêmica, a adoção aprovada é utilizada como indicador de que o pet não está mais disponível. O modelo `Pet` ainda não possui um campo próprio de disponibilidade.

### Respostas HTTP esperadas

- `201 Created`: solicitação criada.
- `200 OK`: consulta ou atualização realizada.
- `400 Bad Request`: dados obrigatórios ausentes ou status inválido.
- `404 Not Found`: usuário, pet ou solicitação não encontrado.
- `409 Conflict`: solicitação duplicada ou conflito de adoção.

=======
## Exemplo de cadastro

```json
{
  "name": "Amora",
  "species": "Cachorro",
  "breed": "Vira-lata",
  "age": 2,
  "size": "Médio",
  "sex": "Fêmea",
  "city": "Maringá",
  "state": "PR",
  "image": "https://exemplo.com/amora.jpg",
  "description": "Carinhosa e brincalhona.",
  "shelter": "ONG Exemplo",
  "status": "Pronta para adoção",
  "ageGroup": "adulto",
  "traits": ["Carinhosa", "Vacinada"]
}
```

>>>>>>> fddfdd0bbb3541e3e55a421703161c20c15c3a2d
## Como executar

1. Configure `MONGODB_URI` se o MongoDB não estiver em `mongodb://localhost:27017/matchpet`.
2. Execute `./mvnw spring-boot:run` (Windows: `mvnw.cmd spring-boot:run`).
<<<<<<< HEAD
3. Teste `GET http://localhost:8080/api/pets` ou `GET http://localhost:8080/api/adocoes`.

O modo CLI permanece desativado por padrão para que o processo execute como servidor HTTP.
=======
3. Teste `GET http://localhost:8080/api/pets`.

O modo CLI foi desativado por padrão para que o processo permaneça executando como servidor HTTP. A classe `MatchPetCli` foi mantida no projeto.

A API aceita chamadas de origens locais comuns do Live Server (`localhost:5500`, `127.0.0.1:5500`) e `localhost:3000`. Se o frontend usar outra porta, ajuste a anotação `@CrossOrigin` do `PetController`.

Nesta primeira etapa, o campo `image` recebe uma URL. O upload de arquivos locais será tratado em uma etapa seguinte.
>>>>>>> fddfdd0bbb3541e3e55a421703161c20c15c3a2d
