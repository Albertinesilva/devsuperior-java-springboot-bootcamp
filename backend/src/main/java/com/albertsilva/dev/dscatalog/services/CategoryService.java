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
import com.albertsilva.dev.dscatalog.services.exceptions.DatabaseException;
import com.albertsilva.dev.dscatalog.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

/**
 * Serviço responsável pelas operações de negócio relacionadas à entidade
 * {@link Category}.
 *
 * <p>
 * Esta classe atua como camada intermediária entre o Controller e o Repository,
 * aplicando regras de negócio, controle transacional e tratamento de exceções.
 * </p>
 *
 * <p>
 * <b>Responsabilidades:</b>
 * </p>
 * <ul>
 * <li>Buscar categorias (simples e paginadas)</li>
 * <li>Criar novas categorias</li>
 * <li>Atualizar categorias existentes</li>
 * <li>Remover categorias</li>
 * <li>Buscar por nome (filtro)</li>
 * </ul>
 */
@Service
public class CategoryService {

  private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

  private final CategoryRepository categoryRepository;
  private final CategoryMapper categoryMapper;

  public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
    this.categoryRepository = categoryRepository;
    this.categoryMapper = categoryMapper;
  }

  /**
   * Retorna uma lista paginada de categorias.
   *
   * <p>
   * Utiliza o recurso de paginação do Spring Data para evitar
   * carregamento excessivo de dados na memória.
   * </p>
   *
   * @param pageable informações de paginação (página, tamanho, ordenação)
   * @return página contendo {@link CategoryResponse}
   */
  @Transactional(readOnly = true)
  public Page<CategoryResponse> findAllPaged(Pageable pageable) {
    logger.debug("Buscando categorias paginadas - page: {}, size: {}", pageable.getPageNumber(),
        pageable.getPageSize());
    return categoryMapper.toResponsePage(categoryRepository.findAll(pageable));
  }

  /**
   * Busca uma categoria pelo seu ID.
   *
   * <p>
   * Caso a categoria não exista, uma exceção
   * {@link ResourceNotFoundException} será lançada.
   * </p>
   *
   * @param id identificador da categoria
   * @return dados da categoria
   * @throws ResourceNotFoundException caso o ID não exista
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
   * O DTO de entrada é convertido para entidade e persistido no banco.
   * </p>
   *
   * @param categoryCreateRequest dados da categoria a ser criada
   * @return categoria criada em formato de resposta
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
   * Atualiza os dados de uma categoria existente.
   *
   * <p>
   * A atualização é parcial: apenas campos não nulos são modificados.
   * </p>
   *
   * <p>
   * Utiliza {@code getReferenceById} para obter uma referência da entidade,
   * evitando uma consulta completa ao banco inicialmente.
   * </p>
   *
   * @param id                    identificador da categoria
   * @param categoryUpdateRequest dados para atualização
   * @return categoria atualizada
   * @throws ResourceNotFoundException caso a categoria não exista
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
   * Remove uma categoria do sistema.
   *
   * <p>
   * Antes de deletar, a existência da categoria é validada através de uma busca
   * no banco de dados. Caso não exista, uma exceção é lançada.
   * </p>
   *
   * <p>
   * Possíveis cenários de erro:
   * </p>
   * <ul>
   * <li>Categoria não encontrada → {@link ResourceNotFoundException}</li>
   * <li>Violação de integridade → tratada globalmente no {@code ControllerAdvice}
   * (ex: {@code DataIntegrityViolationException})</li>
   * </ul>
   *
   * <p>
   * <b>Decisões de implementação:</b>
   * </p>
   * <ul>
   * <li>
   * Não foi utilizado {@code existsById(id)} para evitar uma consulta adicional
   * ao banco,
   * já que {@code findById(id)} já cumpre esse papel de forma mais eficiente.
   * </li>
   * <li>
   * Não foi utilizado {@code @Transactional(propagation = Propagation.SUPPORTS)},
   * pois operações de escrita (DELETE) devem ocorrer dentro de uma transação
   * ativa
   * para garantir consistência e integridade dos dados.
   * </li>
   * <li>
   * Não foi utilizado {@code categoryRepository.flush()}, pois a sincronização
   * com o banco
   * deve ocorrer naturalmente no commit da transação. Forçar o flush pode
   * impactar
   * negativamente a performance e não é uma prática comum em cenários padrão.
   * </li>
   * <li>
   * Não há {@code try/catch} para {@code DataIntegrityViolationException} no
   * método,
   * pois essa exceção pode ocorrer no momento do commit da transação. O
   * tratamento é
   * centralizado no {@code @RestControllerAdvice}, garantindo maior
   * confiabilidade
   * e padronização das respostas da API.
   * </li>
   * </ul>
   *
   * @param id identificador da categoria
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
   * Realiza busca de categorias pelo nome (case insensitive).
   *
   * <p>
   * Permite busca parcial utilizando "contains".
   * </p>
   *
   * @param name     termo de busca
   * @param pageable paginação
   * @return página de categorias encontradas
   */
  @Transactional(readOnly = true)
  public Page<CategoryResponse> searchByName(String name, Pageable pageable) {
    logger.debug("Buscando categorias por nome. termo: {}", name);

    Page<Category> categories = categoryRepository.findByNameContainingIgnoreCase(name, pageable);

    logger.debug("Resultado da busca por nome '{}' - total encontrados: {}", name, categories.getTotalElements());
    return categoryMapper.toResponsePage(categories);
  }
}