# 🧪 Capítulo 02 — Testes Automatizados no Back-End com Spring Boot

<p style="text-align: justify;">
<em>Este capítulo apresenta a construção de uma estratégia profissional de testes automatizados aplicada ao projeto <strong>DSCatalog</strong>, utilizando <code>Java</code>, <code>Spring Boot 3</code>, <code>JUnit 5</code>, <code>Mockito</code>, <code>MockMvc</code> e boas práticas arquiteturais para garantir qualidade, previsibilidade, segurança evolutiva e manutenção sustentável.</em>
</p>

---

## 📚 Contexto do Projeto

Após a implementação da arquitetura em camadas no Capítulo 01, o projeto evolui para um cenário de engenharia de software orientado à qualidade, incorporando múltiplos níveis de validação automatizada:

* **Testes unitários** (Service Layer)
* **Testes de integração parcial** (Repository Layer)
* **Testes da camada web** (Controller Layer)
* **Validação de persistência com JPA**
* **Testes de paginação, ordenação e busca customizada**
* **Validação de relacionamentos ManyToMany**
* **Mocking e isolamento de dependências**
* **Tratamento e tradução de exceções técnicas para regras de negócio**
* **Factories para fixtures reutilizáveis**
* **Princípios de TDD**
* **Aplicação de SOLID voltada à testabilidade**

---

# 🎯 Objetivos do Capítulo

## 1. Dominar fundamentos de testes automatizados

* Compreender testes unitários, integração e funcionais
* Aplicar isolamento, previsibilidade e independência
* Reduzir regressões e custos de manutenção
* Produzir documentação viva por meio de testes

## 2. Implementar testes robustos com JUnit 5

* Uso de `@Test`, `@Nested`, `@DisplayName`
* Fixtures com `@BeforeEach`
* Assertions semânticas com JUnit + AssertJ
* Organização AAA (Arrange / Act / Assert)

## 3. Aplicar estratégias reais no ecossistema Spring Boot

* `@DataJpaTest` para repositories
* `@WebMvcTest` para controllers
* `MockitoExtension` para services
* MockMvc para validação REST

## 4. Simular dependências com Mockito

* `@Mock`
* `@InjectMocks`
* `@MockitoBean`
* `when`, `doThrow`, `doNothing`
* `verify`
* `ArgumentMatchers`

## 5. Aplicar TDD e SOLID

* Desenvolvimento guiado por testes
* Design desacoplado
* Código orientado a manutenção
* Melhor arquitetura para evolução contínua

---

# 🧠 Fundamentos de Testes Automatizados

## 📌 Tipos de Testes

| Tipo       | Objetivo                            | Escopo                 | Dependências Externas |
| ---------- | ----------------------------------- | ---------------------- | --------------------- |
| Unitário   | Validar unidades isoladas           | Métodos / classes      | Não                   |
| Integração | Validar interação entre componentes | Banco / JPA / contexto | Sim                   |
| Funcional  | Validar comportamento completo      | Fluxos reais           | Sim                   |

---

## 🧪 Testes Unitários

Validam regras de negócio isoladamente, focando em comportamento da classe.

### Aplicado no projeto:

* `CategoryServiceTest`
* `ProductServiceTest`

### Cenários cobertos:

* Insert
* Update
* Delete
* FindById
* FindAllPaged
* SearchByName
* Tradução de exceções:

  * `EntityNotFoundException`
  * `DataIntegrityViolationException`
  * `ResourceNotFoundException`
  * `DatabaseException`

### Benefícios:

* Alta velocidade
* Isolamento total
* Segurança de regra de negócio
* Facilidade de refatoração

---

## 🔗 Testes de Persistência (Repository)

Os testes de repository validam:

* Persistência real
* Auto geração de IDs
* Atualização de entidades
* Exclusão
* Busca customizada
* Paginação
* Ordenação
* Integridade relacional
* Join tables ManyToMany

### Aplicado com:

```java
@DataJpaTest
```

### Destaques do projeto:

### CategoryRepository:

* `findByNameContainingIgnoreCase`
* Ordenação alfabética
* Delete seguro sem exclusão de produtos relacionados

### ProductRepository:

* Persistência com categorias
* Remoção de associações ManyToMany
* Preservação de categorias após exclusão de produto

---

## 🌐 Testes Web (Controllers)

### Ferramentas:

* `@WebMvcTest`
* `MockMvc`
* `ObjectMapper`
* `ControllerExceptionHandler`

### Validações realizadas:

* Status HTTP (`200`, `201`, `204`, `404`)
* JSON payload
* Headers (`Location`)
* Serialização
* Tratamento global de exceções

### Endpoints cobertos:

### CategoryController:

* POST
* GET all
* GET by id
* PATCH
* DELETE

### ProductController:

* POST
* GET all
* GET by id
* PATCH
* DELETE

---

# 📈 Benefícios Estratégicos Obtidos

| Benefício                   | Impacto Real                       |
| --------------------------- | ---------------------------------- |
| Segurança contra regressões | Refatoração confiável              |
| Documentação viva           | Clareza técnica                    |
| Isolamento arquitetural     | Menor acoplamento                  |
| Cobertura multicamadas      | Robustez sistêmica                 |
| Evolução sustentável        | Escalabilidade                     |
| Qualidade profissional      | Preparação para mercado enterprise |

---

> [!IMPORTANT]
> Testes automatizados representam investimento estrutural em confiabilidade, manutenção, escalabilidade e maturidade profissional.

---

# 🔄 TDD — Test Driven Development (Conceito Clássico)

## 📖 Definição

Segundo Kent Beck, TDD é uma metodologia onde o desenvolvimento parte da especificação comportamental antes da implementação.

### Ciclo clássico:

| Etapa    | Descrição                      |
| -------- | ------------------------------ |
| Red      | Escreva um teste que falha     |
| Green    | Implemente o mínimo necessário |
| Refactor | Melhore mantendo segurança     |

---

## 🧭 Aplicação prática no DSCatalog

Embora o projeto também siga abordagem educacional incremental, sua estrutura demonstra princípios clássicos de TDD:

* Requisitos claramente definidos
* Testes para sucesso e falha
* Cobertura de exceções
* Evolução segura
* Refatoração protegida

---

## 🚀 Vantagens do TDD

* Código orientado a requisitos
* Melhor design
* Menor acoplamento
* Cobertura natural elevada
* Segurança evolutiva
* Facilidade de manutenção

---

# 🏛️ SOLID Aplicado à Testabilidade

## 📌 Visão Geral Simplificada

| Princípio | Significado               | Benefício para testes     |
| --------- | ------------------------- | ------------------------- |
| SRP       | Responsabilidade única    | Classes menores           |
| OCP       | Aberto para extensão      | Menos impacto em mudanças |
| LSP       | Substituição segura       | Previsibilidade           |
| ISP       | Interfaces específicas    | Mocks menores             |
| DIP       | Dependência de abstrações | Facilidade de mocking     |

---

## 🔽 DIP — Exemplo Real no Projeto

### Problema ruim:

```java
ProductService depende diretamente de ProductRepositoryImpl
```

### Solução correta:

```java
ProductService depende de ProductRepository (interface)
```

### Resultado:

```java
@Mock
private ProductRepository repository;
```

### Benefícios:

* Isolamento
* Mocking simples
* Testes rápidos
* Menor acoplamento

---

## 🧩 SRP — Exemplo prático

### ProductService:

Responsável apenas por regras de negócio.

### ProductMapper:

Responsável apenas por transformação DTO ↔ Entity.

### Benefício:

Cada componente pode ser testado separadamente.

---

> [!TIP]
> SOLID não melhora apenas design — melhora diretamente velocidade, simplicidade e confiabilidade dos testes.

---

# 🧱 Boas Práticas Aplicadas no Projeto

## 📌 Nomenclatura Profissional

### Padrão adotado:

```txt
<ação>Should<resultado>When<cenário>
```

### Exemplos reais:

* `findByIdShouldReturnCategoryWhenIdExists`
* `deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist`
* `updateShouldUpdateProductWhenIdExists`

---

## 📌 Organização AAA

| Etapa   | Objetivo    |
| ------- | ----------- |
| Arrange | Preparação  |
| Act     | Execução    |
| Assert  | Verificação |

### Exemplo:

```java
// Arrange
Mockito.when(repository.findById(id)).thenReturn(Optional.of(product));

// Act
ProductDetailsResponse result = service.findById(id);

// Assert
Assertions.assertNotNull(result);
```

---

## 📌 Estrutura Modular com `@Nested`

### Benefícios:

* Separação por contexto
* Leitura facilitada
* Manutenção superior
* Organização enterprise

---

## 📌 Uso de Fixtures e Factories

### Aplicado com:

* `ProductFactory`
* `CategoryFactory`

### Benefícios:

* Redução de duplicação
* Dados consistentes
* Reuso
* Cenários previsíveis

---

# ⚙️ Estratégias Spring Boot Utilizadas

| Annotation                                  | Finalidade        |
| ------------------------------------------- | ----------------- |
| `@DataJpaTest`                              | Persistência      |
| `@WebMvcTest`                               | Camada web        |
| `@ExtendWith(MockitoExtension.class)`       | Unitário          |
| `@Import(ControllerExceptionHandler.class)` | Tratamento global |
| `@Autowired MockMvc`                        | Simulação HTTP    |

---

# 🎭 Mockito na Prática

## Recursos utilizados:

| Recurso               | Uso                        |
| --------------------- | -------------------------- |
| `@Mock`               | Dependências simuladas     |
| `@InjectMocks`        | Classe testada             |
| `@MockitoBean`        | Mock em contexto web       |
| `when().thenReturn()` | Retornos controlados       |
| `doThrow()`           | Exceções                   |
| `doNothing()`         | Fluxos void                |
| `verify()`            | Verificação comportamental |

---

# 📂 Organização da Estrutura de Testes

```txt
src/test/java
┣ factories
┣ repositories
┣ services
┣ web/controllers
┗ integration (expansível)
```

---

# 📊 Pirâmide de Testes Aplicada

```txt
        Funcionais
       Integração
      Unitários
```

### Interpretação:

* Base forte em unitários
* Integração para persistência
* Web para contratos REST

---

# 🧠 Conclusão — Aprendizado Consolidado

Ao concluir este capítulo, o desenvolvimento do projeto DSCatalog proporcionou evolução técnica significativa nas seguintes competências:

## 🚀 Competências desenvolvidas

### Testes unitários

* Isolamento de regras de negócio
* Mocking avançado
* Tradução de exceções
* Verificação comportamental

### Testes de integração

* Persistência real
* Validação JPA
* Relacionamentos complexos
* Integridade relacional

### Testes web

* Contratos REST
* Status HTTP
* JSON
* Headers
* Exception handling

### Engenharia de software

* TDD
* SOLID
* DIP
* SRP
* Arquitetura testável
* Refatoração segura

---

## 📌 Resultado profissional

O projeto deixa de ser apenas uma API CRUD e passa a representar:

* Um sistema validado profissionalmente
* Arquitetura preparada para evolução
* Base confiável para CI/CD
* Portfólio robusto para mercado backend Java
* Demonstração prática de maturidade em qualidade de software

---

> [!SUCCESS]
> Este capítulo consolidou não apenas conhecimento em testes, mas também uma mentalidade de engenharia profissional: construir software confiável, sustentável, desacoplado e preparado para crescimento contínuo.
