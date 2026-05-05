package com.albertsilva.dev.dscatalog.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.albertsilva.dev.dscatalog.dto.category.mapper.CategoryMapper;
import com.albertsilva.dev.dscatalog.dto.category.request.CategoryCreateRequest;
import com.albertsilva.dev.dscatalog.dto.category.request.CategoryUpdateRequest;
import com.albertsilva.dev.dscatalog.dto.category.response.CategoryResponse;
import com.albertsilva.dev.dscatalog.entities.Category;
import com.albertsilva.dev.dscatalog.repositories.CategoryRepository;
import com.albertsilva.dev.dscatalog.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

/**
 * Serviço responsável pelas operações de negócio relacionadas à entidade
 * {@link Category}.
 *
 * <p>
 * Gerencia categorias, centralizando regras de negócio,
 * validações, persistência e tratamento transacional.
 * </p>
 *
 * <p>
 * <b>Responsabilidades:</b>
 * </p>
 * <ul>
 * <li>Operações de CRUD de categorias</li>
 * <li>Paginação e filtros de busca</li>
 * <li>Conversão entre entidades e DTOs</li>
 * <li>Tratamento de exceções de negócio</li>
 * <li>Garantia de integridade e consistência dos dados</li>
 * </ul>
 *
 * @implNote
 *           Atua como camada de serviço (Service Layer), intermediando
 *           Controller, Repository e Mapper dentro da arquitetura Spring Boot.
 *
 * @apiNote
 *          Esta implementação exemplifica conceitos fundamentais de aplicações
 *          corporativas,
 *          como Service Layer, arquitetura em camadas, DTO Pattern,
 *          persistência com JPA, paginação e regras de negócio centralizadas.
 */
@Service
public class CategoryService {

  private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

  private final CategoryRepository categoryRepository;
  private final CategoryMapper categoryMapper;

  /**
   * Constrói o serviço de categorias com suas dependências principais.
   *
   * @param categoryRepository repositório de categorias
   * @param categoryMapper     responsável pela conversão entre DTOs e entidades
   */
  public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
    this.categoryRepository = categoryRepository;
    this.categoryMapper = categoryMapper;
  }

  /**
   * Retorna uma lista paginada de categorias.
   *
   * <p>
   * Permite consultar categorias de forma escalável,
   * evitando carregamento excessivo de registros.
   * </p>
   *
   * @param pageable informações de paginação
   * @return página de {@link CategoryResponse}
   *
   * @implNote
   *           Utiliza paginação nativa do Spring Data JPA,
   *           reduzindo consumo de memória e melhorando performance.
   *
   * @apiNote
   *          Esta implementação reforça conceitos importantes como:
   *          paginação, escalabilidade e otimização de consultas.
   */
  @Transactional(readOnly = true)
  public Page<CategoryResponse> findAllPaged(Pageable pageable) {
    logger.debug("Buscando categorias paginadas - page: {}, size: {}", pageable.getPageNumber(),
        pageable.getPageSize());
    return categoryMapper.toResponsePage(categoryRepository.findAll(pageable));
  }

  /**
   * Busca uma categoria pelo seu identificador.
   *
   * <p>
   * Retorna os dados completos da categoria,
   * garantindo validação segura da existência do registro.
   * </p>
   *
   * @param id identificador da categoria
   * @return dados da categoria
   * @throws ResourceNotFoundException caso a categoria não exista
   *
   * @implNote
   *           Utiliza {@code findById(id)}, realizando consulta imediata no
   *           banco.
   *
   * @apiNote
   *          Esta implementação reforça conceitos importantes como:
   *          Optional, tratamento de exceções e busca segura de entidades.
   */
  @Transactional(readOnly = true)
  public CategoryResponse findById(Long id) {
    logger.debug("Buscando categoria por id: {}", id);

    Category entity = categoryRepository.findById(id)
        .orElseThrow(() -> {
          logger.warn("Categoria não encontrada. id: {}", id);
          return new ResourceNotFoundException("Entity not found id: " + id);
        });

    logger.debug("Categoria encontrada. id: {}", id);
    return categoryMapper.toResponse(entity);
  }

  /**
   * Insere uma nova categoria no sistema.
   *
   * <p>
   * Converte o DTO de entrada em entidade
   * e persiste os dados no banco.
   * </p>
   *
   * @param categoryCreateRequest dados para criação da categoria
   * @return categoria criada
   *
   * @implNote
   *           Utiliza conversão DTO → Entity,
   *           garantindo separação entre camada de apresentação e persistência.
   *
   * @apiNote
   *          Esta implementação reforça conceitos importantes como:
   *          DTO Pattern, persistência e criação de entidades em APIs RESTful.
   */
  @Transactional
  public CategoryResponse insert(CategoryCreateRequest categoryCreateRequest) {
    logger.debug("Inserindo nova categoria - dados: {}", categoryCreateRequest);

    Category entity = categoryMapper.toEntity(categoryCreateRequest);
    entity = categoryRepository.save(entity);

    logger.info("Categoria criada com sucesso. id: {}", entity.getId());
    return categoryMapper.toResponse(entity);
  }

  /**
   * Atualiza parcialmente uma categoria existente.
   *
   * <p>
   * Permite modificar apenas campos informados,
   * preservando dados não enviados.
   * </p>
   *
   * @param id                    identificador da categoria
   * @param categoryUpdateRequest dados para atualização parcial
   * @return categoria atualizada
   * @throws ResourceNotFoundException caso a categoria não exista
   *
   * @implNote
   *           Utiliza {@code getReferenceById(id)} para obter uma referência lazy
   *           (proxy) da entidade, evitando consulta imediata ao banco.
   *
   *           <p>
   *           O proxy será inicializado somente quando atributos forem acessados.
   *           </p>
   *
   * @apiNote
   *          Esta implementação reforça conceitos importantes como:
   *          JPA Proxy, Lazy Loading, atualização parcial e Contexto de
   *          Persistência.
   */
  @Transactional
  public CategoryResponse update(Long id, CategoryUpdateRequest categoryUpdateRequest) {
    logger.debug("Atualizando categoria. id: {}", id);

    try {
      Category entity = categoryRepository.getReferenceById(id);
      categoryMapper.updateEntity(categoryUpdateRequest, entity);
      entity = categoryRepository.save(entity);

      logger.info("Categoria atualizada com sucesso. id: {}", id);
      return categoryMapper.toResponse(entity);

    } catch (EntityNotFoundException e) {
      logger.warn("Falha ao atualizar. Categoria não encontrada. id: {}", id);
      throw new ResourceNotFoundException("Entity not found id: " + id);
    }
  }

  /**
   * Remove uma categoria existente do sistema.
   *
   * <p>
   * Valida previamente a existência da entidade
   * antes da exclusão.
   * </p>
   *
   * <p>
   * Possíveis cenários de erro:
   * </p>
   * <ul>
   * <li>Categoria não encontrada →
   * {@link ResourceNotFoundException}</li>
   * <li>Violação de integridade referencial →
   * tratada globalmente via {@code @RestControllerAdvice}</li>
   * </ul>
   *
   * @param id identificador da categoria
   * @throws ResourceNotFoundException caso a categoria não exista
   *
   * @implNote
   *           Utiliza {@code findById(id)} para validar existência
   *           e carregar a entidade em uma única consulta,
   *           evitando redundância de operações como {@code existsById(id)}.
   *
   *           <p>
   *           Não utiliza {@code flush()} manual,
   *           permitindo sincronização natural com o banco
   *           durante o commit da transação.
   *           </p>
   *
   *           <p>
   *           Não utiliza {@code Propagation.SUPPORTS},
   *           pois operações de escrita devem ocorrer
   *           dentro de transação ativa para garantir
   *           consistência e integridade dos dados.
   *           </p>
   *
   *           <p>
   *           O tratamento de exceções como
   *           {@code DataIntegrityViolationException}
   *           permanece centralizado globalmente,
   *           garantindo padronização e confiabilidade
   *           nas respostas da API.
   *           </p>
   *
   * @apiNote
   *          Esta implementação reforça conceitos importantes como:
   *          exclusão segura, integridade de dados,
   *          controle transacional, otimização de consultas
   *          e tratamento centralizado de exceções.
   */
  @Transactional
  public void delete(Long id) {
    logger.debug("Deletando categoria. id: {}", id);

    Category entity = categoryRepository.findById(id)
        .orElseThrow(() -> {
          logger.warn("Falha ao deletar. Categoria não encontrada. id: {}", id);
          return new ResourceNotFoundException("Entity not found id: " + id);
        });

    categoryRepository.delete(entity);
    logger.info("Categoria deletada com sucesso. id: {}", id);
  }

  /**
   * Realiza busca paginada de categorias por nome.
   *
   * <p>
   * Permite busca parcial e case insensitive,
   * utilizando correspondência por conteúdo textual.
   * </p>
   *
   * @param name     termo de busca
   * @param pageable informações de paginação
   * @return página de categorias encontradas
   *
   * @implNote
   *           Utiliza consulta derivada do Spring Data JPA:
   *           {@code findByNameContainingIgnoreCase}.
   *
   *           <p>
   *           Essa abordagem reduz necessidade
   *           de implementação manual de queries.
   *           </p>
   *
   * @apiNote
   *          Esta implementação reforça conceitos importantes como:
   *          consultas derivadas, filtros dinâmicos,
   *          paginação e busca textual eficiente.
   */
  @Transactional(readOnly = true)
  public Page<CategoryResponse> searchByName(String name, Pageable pageable) {
    logger.debug("Buscando categorias por nome. termo: {}", name);

    Page<Category> categories = categoryRepository.findByNameContainingIgnoreCase(name, pageable);

    logger.debug("Resultado da busca por nome '{}' - total encontrados: {}", name, categories.getTotalElements());
    return categoryMapper.toResponsePage(categories);
  }
}