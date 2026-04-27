package com.albertsilva.dev.dscatalog.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.albertsilva.dev.dscatalog.dto.product.mapper.ProductMapper;
import com.albertsilva.dev.dscatalog.dto.product.request.ProductCreateRequest;
import com.albertsilva.dev.dscatalog.dto.product.request.ProductUpdateRequest;
import com.albertsilva.dev.dscatalog.dto.product.response.ProductDetailsResponse;
import com.albertsilva.dev.dscatalog.dto.product.response.ProductResponse;
import com.albertsilva.dev.dscatalog.entities.Category;
import com.albertsilva.dev.dscatalog.entities.Product;
import com.albertsilva.dev.dscatalog.repositories.CategoryRepository;
import com.albertsilva.dev.dscatalog.repositories.ProductRepository;
import com.albertsilva.dev.dscatalog.services.exceptions.DatabaseException;
import com.albertsilva.dev.dscatalog.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

/**
 * Serviço responsável pelas operações de negócio relacionadas à entidade
 * {@link Product}.
 *
 * <p>
 * Gerencia produtos e sua relação com categorias, centralizando
 * regras de negócio, validações e associações entre entidades.
 * </p>
 *
 * <p>
 * <b>Responsabilidades:</b>
 * </p>
 * <ul>
 * <li>Operações de CRUD de produtos</li>
 * <li>Gerenciamento de relacionamento entre produtos e categorias</li>
 * <li>Conversão entre entidades e DTOs</li>
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
 *          persistência com JPA e regras de negócio centralizadas.
 */
@Service
public class ProductService {

  private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;
  private final ProductMapper productMapper;

  /**
   * Constrói o serviço de produtos com suas dependências principais.
   *
   * @param productRepository  repositório de produtos
   * @param categoryRepository repositório de categorias
   * @param productMapper      responsável pela conversão entre DTOs e entidades
   */
  public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository,
      ProductMapper productMapper) {
    this.productRepository = productRepository;
    this.categoryRepository = categoryRepository;
    this.productMapper = productMapper;
  }

  /**
   * Retorna uma lista paginada de produtos.
   *
   * <p>
   * Permite consultar produtos de forma escalável,
   * evitando carregamento excessivo de registros.
   * </p>
   *
   * @param pageable informações de paginação
   * @return página de {@link ProductResponse}
   *
   * @implNote
   *           Utiliza paginação nativa do Spring Data JPA,
   *           melhorando performance e reduzindo consumo de memória.
   *
   * @apiNote
   *          Esta implementação reforça conceitos importantes como:
   *          paginação, escalabilidade e otimização de consultas.
   */
  @Transactional(readOnly = true)
  public Page<ProductResponse> findAllPaged(Pageable pageable) {
    logger.debug("Buscando produtos paginados - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
    Page<Product> products = productRepository.findAll(pageable);
    logger.debug("Total de produtos encontrados: {}", products.getTotalElements());
    return productMapper.toResponsePage(products);
  }

  /**
   * Busca um produto pelo seu identificador.
   *
   * <p>
   * Retorna os detalhes completos do produto,
   * incluindo categorias associadas.
   * </p>
   *
   * @param id identificador do produto
   * @return detalhes completos do produto
   * @throws ResourceNotFoundException caso o produto não exista
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
  public ProductDetailsResponse findById(Long id) {
    logger.debug("Buscando produto por id: {}", id);
    Product entity = productRepository.findById(id)
        .orElseThrow(() -> {
          logger.warn("Produto não encontrado. id: {}", id);
          return new ResourceNotFoundException("Entity not found id: " + id);
        });
    logger.debug("Produto encontrado. id: {}", id);
    return productMapper.toDetailsResponse(entity);
  }

  /**
   * Insere um novo produto no sistema.
   *
   * <p>
   * Além dos dados básicos, realiza o vínculo
   * com categorias utilizando seus respectivos IDs.
   * </p>
   *
   * <p>
   * O frontend envia apenas IDs das categorias,
   * enquanto o backend resolve o relacionamento completo.
   * </p>
   *
   * @param productCreateRequest dados para criação do produto
   * @return produto criado
   *
   * @implNote
   *           Utiliza conversão DTO → Entity e mapeamento controlado
   *           de categorias para garantir integridade relacional.
   *
   * @apiNote
   *          Esta implementação reforça conceitos importantes como:
   *          DTO Pattern, relacionamento ManyToMany e persistência.
   */
  @Transactional
  public ProductResponse insert(ProductCreateRequest productCreateRequest) {
    logger.debug("Inserindo novo produto - dados: {}", productCreateRequest);
    Product entity = productMapper.toEntity(productCreateRequest);
    mapCategories(entity, productCreateRequest.categoryIds());
    entity = productRepository.save(entity);
    logger.info("Produto criado com sucesso. id: {}", entity.getId());
    return productMapper.toResponse(entity);
  }

  /**
   * Atualiza parcialmente um produto existente.
   *
   * <p>
   * Permite modificar atributos básicos e,
   * opcionalmente, substituir as categorias associadas.
   * </p>
   *
   * <p>
   * Quando {@code categoryIds} é informado,
   * as categorias atuais são removidas e substituídas
   * pelas novas categorias fornecidas.
   * </p>
   *
   * @param id  identificador do produto
   * @param dto dados para atualização parcial
   * @return produto atualizado
   * @throws ResourceNotFoundException caso o produto não exista
   *
   * @implNote
   *           Utiliza {@code getReferenceById(id)} para obter uma referência lazy
   *           (proxy) da entidade, evitando consulta imediata ao banco.
   *
   *           <p>
   *           O proxy é carregado apenas quando atributos da entidade
   *           são acessados, reduzindo consultas desnecessárias.
   *           </p>
   *
   * @apiNote
   *          Esta implementação reforça conceitos importantes como:
   *          JPA Proxy, Lazy Loading, Performance e Contexto de Persistência.
   */
  @Transactional
  public ProductResponse update(Long id, ProductUpdateRequest dto) {
    logger.debug("Atualizando produto. id: {}", id);

    try {
      Product entity = productRepository.getReferenceById(id);
      productMapper.updateEntity(dto, entity);

      if (dto.categoryIds() != null) {
        mapCategories(entity, dto.categoryIds());
        logger.debug("Categorias do produto atualizadas. id: {}", id);
      }

      entity = productRepository.save(entity);
      logger.info("Serviço Produto atualizado com sucesso. id: {}", id);
      return productMapper.toResponse(entity);

    } catch (EntityNotFoundException e) {
      logger.warn("Falha ao atualizar produto. Produto não encontrado. id: {}", id);
      throw new ResourceNotFoundException("Entity not found id: " + id);
    }
  }

  /**
   * Remove um produto existente do sistema.
   *
   * <p>
   * Valida previamente a existência da entidade
   * antes da exclusão.
   * </p>
   *
   * @param id identificador do produto
   * @throws ResourceNotFoundException caso o produto não exista
   * @throws DatabaseException         em caso de violação de integridade
   *
   * @implNote
   *           Garante segurança ao validar existência antes do delete
   *           e trata exceções de integridade referencial.
   *
   * @apiNote
   *          Esta implementação reforça conceitos importantes como:
   *          exclusão segura, integridade de dados e tratamento de exceções.
   */
  @Transactional
  public void delete(Long id) {
    logger.debug("Deletando produto. id: {}", id);

    Product entity = productRepository.findById(id)
        .orElseThrow(() -> {
          logger.warn("Falha ao deletar. Produto não encontrado. id: {}", id);
          return new ResourceNotFoundException("Entity not found id: " + id);
        });

    try {
      productRepository.delete(entity);
      logger.info("Produto deletado com sucesso. id: {}", id);

    } catch (DataIntegrityViolationException e) {
      logger.error("Erro de integridade ao deletar produto. id: {}", id);
      throw new DatabaseException("Integrity violation: cannot delete product with related entities");
    }
  }

  /**
   * Realiza o mapeamento entre produto e categorias.
   *
   * <p>
   * Remove categorias antigas e substitui
   * pelas categorias informadas.
   * </p>
   *
   * <p>
   * Valida se todos os IDs recebidos existem
   * antes de concluir a associação.
   * </p>
   *
   * @param entity      produto a ser associado
   * @param categoryIds lista de IDs de categorias
   * @throws ResourceNotFoundException caso alguma categoria não exista
   *
   * @implNote
   *           Utiliza {@code findAllById} para buscar todas as categorias
   *           em lote, evitando múltiplas consultas (N+1 problem).
   *
   *           <p>
   *           Essa abordagem melhora performance
   *           e garante consistência relacional.
   *           </p>
   *
   * @apiNote
   *          Esta implementação reforça conceitos importantes como:
   *          mapeamento de relacionamentos em JPA, performance JPA, N+1 queries e
   *          relacionamentos eficientes.
   */
  private void mapCategories(Product entity, List<Long> categoryIds) {
    entity.getCategories().clear();

    if (categoryIds == null || categoryIds.isEmpty()) {
      logger.debug("Nenhuma categoria fornecida para mapear ao produto. id: {}",
          entity.getId());
      return;
    }

    List<Category> categories = categoryRepository.findAllById(categoryIds);

    if (categories.size() != categoryIds.size()) {
      logger.warn("Uma ou mais categorias não foram encontradas. produtoId: {}",
          entity.getId());
      throw new ResourceNotFoundException("One or more categories not found");
    }

    entity.getCategories().addAll(categories);

    logger.debug("Categorias mapeadas ao produto. produtoId: {}, total: {}",
        entity.getId(), categories.size());
  }

  /**
   * Deixei o método de mapeamento utilizando {@code getReferenceById} comentado
   * para fins de comparação e aprendizado.
   * 
   * Realiza o mapeamento entre produto e categorias utilizando proxies do JPA.
   *
   * <p>
   * <b>Fluxo interno:</b>
   * </p>
   * <ol>
   * <li>Remove todas as categorias atuais do produto</li>
   * <li>Para cada ID informado, obtém uma referência (proxy) da entidade
   * {@link Category}</li>
   * <li>Associa a referência ao produto sem executar consulta imediata</li>
   * </ol>
   *
   * <p>
   * <b>Uso de {@code getReferenceById}:</b>
   * </p>
   * <ul>
   * <li>Não executa {@code SELECT} no momento da chamada</li>
   * <li>Retorna um proxy gerenciado pelo JPA (Hibernate)</li>
   * <li>A consulta ao banco só ocorre se algum atributo da entidade for
   * acessado</li>
   * <li>Melhora a performance em cenários onde apenas o relacionamento é
   * necessário</li>
   * </ul>
   *
   * <p>
   * <b>Importante:</b>
   * </p>
   * <ul>
   * <li>Não valida imediatamente se o ID existe no banco</li>
   * <li>Uma exceção pode ser lançada posteriormente (ex: ao acessar atributos ou
   * no flush)</li>
   * <li>Indicado quando você já confia que os IDs são válidos</li>
   * <li>Evita múltiplas queries (N+1) quando comparado com {@code findById}</li>
   * </ul>
   *
   * <p>
   * <b>Quando usar:</b>
   * </p>
   * <ul>
   * <li>Associação de entidades (ManyToOne, ManyToMany)</li>
   * <li>Cenários onde não é necessário carregar os dados completos</li>
   * </ul>
   *
   * <p>
   * <b>Quando NÃO usar:</b>
   * </p>
   * <ul>
   * <li>Quando é necessário validar a existência do registro</li>
   * <li>Quando será necessário acessar os dados da entidade imediatamente</li>
   * </ul>
   *
   * @param entity      produto
   * @param categoryIds lista de IDs de categorias
   *
   * @implNote
   *           Alternative implementation using JPA proxies retained for
   *           educational purposes.
   *           Demonstrates the use of {@code getReferenceById} for efficient
   *           relationship mapping.
   */
  // private void mapCategories(Product entity, List<Long> categoryIds) {
  // entity.getCategories().clear();

  // if (categoryIds == null || categoryIds.isEmpty()) {
  // logger.debug("Nenhuma categoria fornecida para mapear ao produto. id: {}",
  // entity.getId());
  // return;
  // }

  // for (Long categoryId : categoryIds) {
  // Category category = categoryRepository.getReferenceById(categoryId);
  // entity.getCategories().add(category);
  // logger.debug("Categoria mapeada ao produto. produtoId: {}, categoriaId: {}",
  // entity.getId(), categoryId);
  // }
  // }

}