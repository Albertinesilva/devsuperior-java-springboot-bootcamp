# 🧩 Capítulo 01 — Operações CRUD com Spring Boot

<p style="text-align: justify;">
<em>Este capítulo apresenta a construção de uma API backend utilizando <code>Java</code> e <code>Spring Boot</code>, com foco na implementação de operações de <code>CRUD</code> (Create, Read, Update e Delete) para gerenciamento de produtos e categorias.</em>
</p>

---

O projeto **DSCatalog** foi estruturado seguindo boas práticas de desenvolvimento, adotando **arquitetura em camadas** e separação clara de responsabilidades. Além das operações básicas de CRUD, foram implementados conceitos importantes como:

- Uso de **DTOs** para comunicação entre camadas, utilizando **records do Java** para estruturas imutáveis de dados;  
- Mapeamento com classes dedicadas (**Mapper**);  
- Tratamento de **exceções customizadas**;  
- Padronização das respostas da API;  
- Mecanismo global de tratamento de erros, garantindo robustez e rastreabilidade.

A aplicação contempla a organização em camadas: `controller`, `service` e `repository`, além da camada de DTOs que garante maior controle sobre os dados expostos.

---

## 🎯 Objetivos do Capítulo

1. **Implementação de operações CRUD**  
   - Criação, leitura, atualização e exclusão de **produtos** e **categorias** via API REST.  
   - Endpoints bem estruturados: `GET`, `POST`, `PATCH`, `DELETE` com status HTTP adequado.  
   - Serviços que fazem mapeamento **DTO ↔ entidade** usando **records** e **Mapper**.  
   - Validação e tratamento de exceções (`ResourceNotFoundException`, `DatabaseException`) com logs detalhados.

2. **Paginação e filtragem**  
   - Uso de `Pageable` para controlar páginas, tamanho e ordenação.  
   - Consultas case-insensitive e parciais (`findByNameContainingIgnoreCase`) para melhorar experiência do usuário.

3. **Mapeamento de relacionamentos**  
   - Recebendo apenas **IDs de categorias** no request e resolvendo vínculos no backend.  
   - Atualização parcial de categorias, sem sobrescrever dados não enviados.

4. **Ambientes de desenvolvimento e testes**  

| Aspecto                      | Ambiente de Testes (`test`)                       | Ambiente de Desenvolvimento (`dev`)                   |
|-------------------------------|--------------------------------------------------|------------------------------------------------------|
| Banco de dados                | H2 in-memory (efêmero)                           | PostgreSQL local (persistente)                      |
| Console                       | `/h2-console` para inspeção manual              | Console SQL exibindo queries                         |
| Migrations (Flyway)           | Desativado                                      | Ativo (`db/migration/schema` + `db/migration/data`) |
| Logs                          | DEBUG/TRACE, JSON, `logs/test/dscatalog-test.log` | DEBUG/TRACE, JSON, `logs/dev/dscatalog-dev.log`     |
| Banner                        | N/A                                             | Banner personalizado (`banner-dev.txt`)             |
| Objetivo                       | Testes isolados, rápidos e reproduzíveis        | Desenvolvimento realista com dados persistentes     |
| Observações                    | Banco efêmero, reset a cada execução            | Controle de schema, rastreabilidade completa        |

> [!IMPORTANT]
> Essa separação garante testes **isolados, rápidos e reproduzíveis**, enquanto o desenvolvimento ocorre em ambiente realista com dados persistentes e migrations. Reflete boas práticas de engenharia de software.

5. **Documentação da API e código**  
   - **OpenAPI/Swagger** para documentação interativa;  
   - **JavaDocs** explicando responsabilidades de controllers e services.

6. **Boas práticas de código e arquitetura**  
   - Estrutura clara em **Controller, Service e Repository**;  
   - Uso de **logger** para monitoramento e rastreabilidade;  
   - Tratamento transacional adequado (`@Transactional`) para consistência de dados.

---

## 📦 Estrutura do Projeto `DSCatalog`

📦 `com.albertsilva.dev.dscatalog`  
┣ 📂 `config`  
┃ ┗ 📄 `SpringDocOpenApiConfig.java`  
┣ 📂 `category`  
┃ ┣ 📂 `mapper`  
┃ ┃ ┗ 📄 `CategoryMapper.java`  
┃ ┣ 📂 `request`  
┃ ┃ ┣ 📄 `CategoryCreateRequest.java`  
┃ ┃ ┗ 📄 `CategoryUpdateRequest.java`  
┃ ┗ 📂 `response`  
┃ ┗ 📄 `CategoryResponse.java`  
┣ 📂 `product`  
┃ ┣ 📂 `mapper`  
┃ ┃ ┗ 📄 `ProductMapper.java`  
┃ ┣ 📂 `request`  
┃ ┃ ┣ 📄 `ProductCreateRequest.java`  
┃ ┃ ┗ 📄 `ProductUpdateRequest.java`  
┃ ┗ 📂 `response`  
┃ ┣ 📄 `ProductDetailsResponse.java`  
┃ ┗ 📄 `ProductResponse.java`  
┣ 📂 `entities`  
┃ ┣ 📄 `Category.java`  
┃ ┗ 📄 `Product.java`  
┣ 📂 `repositories`  
┃ ┣ 📄 `CategoryRepository.java`  
┃ ┗ 📄 `ProductRepository.java`  
┣ 📂 `services`  
┃ ┣ 📂 `exceptions`  
┃ ┃ ┣ 📄 `DatabaseException.java`  
┃ ┃ ┗ 📄 `ResourceNotFoundException.java`  
┃ ┣ 📄 `CategoryService.java`  
┃ ┗ 📄 `ProductService.java`  
┣ 📂 `web`  
┃ ┣ 📂 `controllers`  
┃ ┃ ┣ 📄 `CategoryController.java`  
┃ ┃ ┗ 📄 `ProductController.java`  
┃ ┣ 📂 `enums`  
┃ ┗ 📂 `exceptions`  
┃ ┣ 📄 `ControllerExceptionHandler.java`  
┃ ┗ 📄 `StandardError.java`  
┣ 📄 `DscatalogApplication.java`  
┣ 📂 `resources`  
┃ ┣ 📂 `db`  
┃ ┃ ┣ 📂 `migration`  
┃ ┃ ┗ 📂 `data / schema`  
┃ ┣ 📂 `static`  
┃ ┣ 📂 `templates`  
┃ ┣ 📄 `application-dev.properties`  
┃ ┣ 📄 `application-test.properties`  
┃ ┣ 📄 `application.properties`  
┃ ┗ 📄 `import.sql`

---

## 🧱 Arquitetura em Camadas

A aplicação **DSCatalog** segue a arquitetura tradicional **Controller → Service → Repository**, organizada em camadas bem definidas para garantir **manutenção mais fácil, testabilidade e escalabilidade**.

<img src="https://raw.githubusercontent.com/Albertinesilva/devsuperior-java-springboot-bootcamp/chapter-01-crud/backend/src/main/resources/static/assets/imgs/padrao-camadas.png" width="100%">

## Padrão de Camadas

- Consiste em organizar os componentes do sistema em **partes denominadas camadas**.  
- Cada camada possui **responsabilidade específica**.  
- Componentes de uma camada só podem depender de **componentes da mesma camada** ou da camada **mais abaixo**.

## Descrição das Camadas e Responsabilidades

### Controller
- Responde interações do usuário (no caso de API REST, as requisições HTTP).  
- Recebe os dados do front-end, encaminha para o service e retorna respostas padronizadas.  

### Service
- Realiza operações de negócio, cada método deve ter **significado relacionado ao negócio**.  
- Pode executar várias operações dentro de uma transação.  
  *Exemplo:* `registrarPedido` → verificar estoque, salvar pedido, baixar estoque, enviar email.  
- Manipula DTOs, valida regras de negócio e interage com o repository.  

### Repository
- Executa operações **individuais** de acesso ao banco de dados.  
- Responsável pela persistência via **Spring Data JPA**.  

### DTOs (Data Transfer Objects)
- Objetos **simples**, usados apenas para transferência de dados.  
- Não são gerenciados por ORM / banco de dados.  
- Podem conter outros DTOs **aninhados**, mas **nunca devem conter entities**.  
- Usos comuns:
  - Projeção de dados
  - Segurança (não expor dados sensíveis)
  - Economia de tráfego
  - Flexibilidade: diferentes representações dos dados
    - Combobox: `{ id: number, nome: string }`
    - Relatório detalhado: `{ id, nome, salario, email, telefones[] }`  

### Mapper
- Converte entre **entities** do banco e **DTOs**, mantendo separação de responsabilidades.  

### Exception Handler Global
- Captura exceções em toda a aplicação e retorna respostas padronizadas em **JSON**, com mensagens claras e rastreabilidade.  

## Por que usar DTOs?

- Separação clara de responsabilidades:  
  - **Service e Repository:** foco em transações e monitoramento ORM  
  - **Controller:** tráfego simples de dados  
- Segurança, economia de tráfego e flexibilidade na API.  
- Facilita diferentes representações de dados para front-end e relatórios.

> [!IMPORTANT]  
> Essa separação garante **código limpo, testável e escalável**, permitindo que a aplicação evolua sem impactar outras camadas, além de tornar a leitura do código mais intuitiva para recrutadores e profissionais que avaliam a arquitetura do sistema.

---

## 🛠️ Tecnologias Utilizadas

O projeto **DSCatalog** foi desenvolvido utilizando um conjunto moderno de tecnologias voltadas para construção de APIs REST robustas, escaláveis e bem estruturadas.

### 📌 Stack Principal

| Categoria            | Tecnologia                                      | Função                                                                 |
|---------------------|--------------------------------------------------|------------------------------------------------------------------------|
| Linguagem           | Java 17                                          | Desenvolvimento backend moderno com recursos atuais da linguagem      |
| Framework           | Spring Boot 3.5.13                               | Estrutura principal da aplicação e gerenciamento de dependências      |
| API REST            | Spring Web                                       | Criação de endpoints HTTP (RESTful APIs)                              |
| Persistência        | Spring Data JPA                                  | Abstração para acesso a dados e integração com ORM                    |
| ORM                 | Hibernate                                        | Mapeamento objeto-relacional (Entity ↔ Tabela)                        |
| Validação           | Spring Boot Validation                           | Validação de dados de entrada (Bean Validation)                       |

---

### 🗄️ Banco de Dados

| Categoria            | Tecnologia        | Função                                                                 |
|---------------------|------------------|------------------------------------------------------------------------|
| Banco Principal     | PostgreSQL        | Banco relacional utilizado no ambiente de desenvolvimento              |
| Banco de Testes     | H2 Database       | Banco em memória para testes rápidos e isolados                        |
| Console DB          | H2 Console        | Interface web para inspeção de dados em ambiente de teste              |

---

### 🔄 Migração e Versionamento de Banco

| Tecnologia  | Função                                                                 |
|-------------|------------------------------------------------------------------------|
| Flyway      | Controle de versão do banco de dados (migrations de schema e dados)   |

---

### 📄 Documentação da API

| Tecnologia                        | Função                                                                 |
|----------------------------------|------------------------------------------------------------------------|
| SpringDoc OpenAPI (Swagger UI)   | Geração automática de documentação interativa da API REST             |
| JavaDocs                         | Documentação técnica do código, descrevendo responsabilidades, métodos e fluxos |
> [!TIP]
> A API conta com documentação automatizada via **Swagger/OpenAPI**, além de **JavaDocs** bem definidos nos controllers e services, facilitando o entendimento da lógica de negócio e manutenção do código.
---

### 🧪 Testes

| Tecnologia                     | Função                                                |
|--------------------------------|--------------------------------------------------------|
| Spring Boot Starter Test       | Testes unitários e de integração                      |

---

### ⚙️ Ferramentas de Desenvolvimento

| Ferramenta              | Função                                                                 |
|------------------------|------------------------------------------------------------------------|
| Spring Boot DevTools   | Hot reload e aumento de produtividade no desenvolvimento              |
| IntelliJ IDEA         | IDE principal para desenvolvimento backend                           |
| VS Code               | Editor auxiliar                                                      |
| Postman               | Teste de endpoints e simulação de requisições HTTP                   |
| pgAdmin               | Administração e gerenciamento do banco PostgreSQL                    |

---

### 📦 Build e Gerenciamento

| Tecnologia        | Função                                                |
|------------------|--------------------------------------------------------|
| Maven            | Gerenciamento de dependências e build do projeto      |
| Maven Compiler   | Compilação com suporte ao Java 17                     |
| Maven Javadoc    | Geração de documentação técnica do código             |

---

### 📊 Observabilidade e Logs

| Tecnologia        | Função                                                                 |
|------------------|------------------------------------------------------------------------|
| Logback (Spring) | Gerenciamento de logs da aplicação                                     |
| SLF4J            | Abstração de logging                                                   |
| JSON Logging     | Logs estruturados para melhor rastreabilidade                         |

---

> [!IMPORTANT]
> A escolha dessas tecnologias segue padrões amplamente adotados no mercado, garantindo **produtividade, manutenibilidade e escalabilidade**, além de alinhar o projeto com práticas profissionais utilizadas em aplicações corporativas.

---
## 🚀 API REST — Endpoints

A API do **DSCatalog** expõe endpoints REST seguindo boas práticas de design, utilizando JSON como formato padrão de comunicação.

---

### 📦 Categorias (`/api/v1/categories`)

| Método | Endpoint                  | Descrição                                 |
|--------|--------------------------|--------------------------------------------|
| POST   | `/categories`            | Cria uma nova categoria                   |
| GET    | `/categories`            | Lista categorias (paginado)               |
| GET    | `/categories/{id}`       | Busca categoria por ID                    |
| GET    | `/categories/search`     | Busca categorias por nome                 |
| PATCH  | `/categories/{id}`       | Atualiza parcialmente uma categoria       |
| DELETE | `/categories/{id}`       | Remove uma categoria                      |

---

## 📌 Endpoints — Categorias

Base URL: `/api/v1/categories`

Esta seção documenta todos os endpoints relacionados ao recurso **Categoria**, incluindo exemplos de requisição e resposta.

---

### 📥 Criar Categoria

**POST** `/api/v1/categories`

Cria uma nova categoria no sistema.

#### 🔸 Request Body
```json
{
  "name": "Eletrônicos",
  "description": "Produtos eletrônicos em geral",
  "active": true
}
```
>💡 O campo active é opcional. Caso não seja informado, será definido como false.

### 🔸 Response (201 Created)

```json
{
  "id": 1,
  "name": "Eletrônicos",
  "description": "Produtos eletrônicos em geral",
  "active": true
}
```
### 🔸 Headers
```
Location: /api/v1/categories/1
```

---
### 📄 Listar Categorias (Paginado)

**GET** `/api/v1/categories`

Retorna uma lista paginada de categorias.

#### 🔸 Query Params

| Parâmetro     | Tipo   | Default | Descrição                     |
|--------------|--------|---------|-------------------------------|
| page         | int    | 0       | Número da página              |
| linesPerPage | int    | 12      | Quantidade de registros       |
| orderBy      | string | name    | Campo de ordenação            |
| direction    | string | ASC     | Direção (ASC ou DESC)         |

#### 🔸 Exemplo

```http
GET /api/v1/categories?page=0&linesPerPage=10&orderBy=name&direction=ASC
```

### 🔸 Response (200 OK)

```json
{
  "content": [
    {
      "id": 1,
      "name": "Eletrônicos",
      "description": "Produtos eletrônicos",
      "active": true
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "size": 10,
  "number": 0
}
```
> 🔐 Endpoint protegido (requer autenticação — ROLE ADMIN)

---
### 🔍 Buscar Categoria por ID

**GET** `/api/v1/categories/{id}`

Retorna os dados de uma categoria específica.

### 🔸 Response (200 OK)

```json
{
  "id": 1,
  "name": "Eletrônicos",
  "description": "Produtos eletrônicos",
  "active": true
}
```

### 🔸 Erros possíveis

```json
{
    "timestamp": "2026-04-09T18:42:25.491392800Z",
    "status": 404,
    "error": "Resource not found",
    "message": "Entity not found id: 100",
    "path": "/api/v1/categories/100"
}
```

---
### 🔎 Buscar Categorias por Nome

**GET** `/api/v1/categories/search`

Busca categorias por nome (case insensitive e parcial).

### 🔸 Query Params

| Parâmetro | Tipo   | Descrição      |
| --------- | ------ | -------------- |
| name      | string | Termo de busca |

### 🔸 Exemplo
```http
GET /api/v1/categories/search?name=eletron
```

### 🔸 Response (200 OK)

```json
{
  "content": [
    {
      "id": 1,
      "name": "Eletrônicos",
      "description": "Produtos eletrônicos",
      "active": true
    }
  ]
}
```

---
### ✏️ Atualizar Categoria (Parcial)

**PATCH** `/api/v1/categories/{id}`

Atualiza parcialmente os dados de uma categoria.

### 🔸 Request Body
```json
{
  "name": "Eletrônicos Atualizado",
  "active": false
}
```
> 💡 Apenas campos enviados são atualizados
> 💡 Campos null são ignorados

### 🔸 Response (200 OK)
```json
{
  "id": 1,
  "name": "Eletrônicos Atualizado",
  "description": "Produtos eletrônicos",
  "active": false
}
```

---
### ❌ Remover Categoria

**DELETE** `/api/v1/categories/{id}`

Remove uma categoria do sistema.

### 🔸 Response
- 204 No Content

### 🔸 Erros possíveis
- 404 Not Found
- 400 Bad Request (violação de integridade)

### ⚠️ Padrão de Erro

- Todos os erros seguem um padrão unificado:

```json
{
    "timestamp": "2026-04-09T18:50:14.708743400Z",
    "status": 404,
    "error": "Resource not found",
    "message": "Entity not found id: 100",
    "path": "/api/v1/categories/100"
}
```
```json
{
    "timestamp": "2026-04-09T18:50:44.722862600Z",
    "status": 400,
    "error": "Database error",
    "message": "Cannot delete resource because it has related entities",
    "path": "/api/v1/categories/1"
}
```
> [!IMPORTANT]
> A API segue boas práticas REST, utilizando corretamente os métodos HTTP (POST, GET, PATCH, DELETE), códigos de status e padronização de respostas, garantindo previsibilidade e facilidade de integração.
