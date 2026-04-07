## 🧩 Capítulo 01 — Operações CRUD com Spring Boot

<p align="center">
  <em>Este capítulo apresenta a construção de uma API backend utilizando `Java` e `Spring Boot`, com foco na implementação de operações de `CRUD` (Create, Read, Update e Delete) para gerenciamento de produtos e categorias.</em>
</p>

---

O projeto DSCatalog foi estruturado seguindo boas práticas de desenvolvimento, adotando arquitetura em camadas e separação clara de responsabilidades. Além das operações básicas de CRUD, foram implementados conceitos importantes como uso de DTOs para comunicação entre camadas, utilizando **records do Java** para representar estruturas imutáveis de dados, além de mapeamento com classes dedicadas (Mapper), tratamento de exceções customizadas e padronização das respostas da API.

A aplicação contempla a organização em camadas de `controller`, `service` e `repository`, além de uma camada de DTOs que garante maior controle sobre os dados expostos. Também foi implementado um mecanismo global de tratamento de erros, tornando a API mais robusta e alinhada com práticas profissionais de desenvolvimento backend.
