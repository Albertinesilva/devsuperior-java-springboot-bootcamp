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
