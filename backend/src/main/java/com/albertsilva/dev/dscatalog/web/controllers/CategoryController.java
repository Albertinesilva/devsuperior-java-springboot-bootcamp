package com.albertsilva.dev.dscatalog.web.controllers;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.albertsilva.dev.dscatalog.dto.category.request.CategoryCreateRequest;
import com.albertsilva.dev.dscatalog.dto.category.request.CategoryUpdateRequest;
import com.albertsilva.dev.dscatalog.dto.category.response.CategoryResponse;
import com.albertsilva.dev.dscatalog.services.CategoryService;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller responsável por expor os endpoints REST da entidade Category.
 *
 * <p>
 * Esta classe recebe requisições HTTP, delega o processamento para a camada
 * de serviço ({@link CategoryService}) e retorna respostas padronizadas.
 * </p>
 *
 * <p>
 * <b>Base URL:</b> /api/v1/categories
 * </p>
 *
 * <p>
 * <b>Responsabilidades:</b>
 * </p>
 * <ul>
 * <li>Receber requisições HTTP</li>
 * <li>Validar e mapear parâmetros</li>
 * <li>Delegar regras de negócio para o Service</li>
 * <li>Retornar respostas HTTP apropriadas</li>
 * </ul>
 *
 * <p>
 * <b>Padrões REST utilizados:</b>
 * </p>
 * <ul>
 * <li>POST → criação</li>
 * <li>GET → consulta</li>
 * <li>PATCH → atualização parcial</li>
 * <li>DELETE → remoção</li>
 * </ul>
 */
@Tag(name = "Categorias", description = "Contém todas as operações aos recursos para cadastro, edição e leitura de uma categoria.")
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

  private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

  private final CategoryService categoryService;

  /**
   * Construtor para injeção de dependência do serviço.
   *
   * @param categoryService serviço de categorias
   */
  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  /**
   * Endpoint para criação de uma nova categoria.
   *
   * <p>
   * Recebe um JSON contendo os dados da categoria e retorna o recurso criado.
   * </p>
   *
   * <p>
   * <b>Fluxo:</b>
   * </p>
   * <ol>
   * <li>Recebe o request</li>
   * <li>Delega para o Service</li>
   * <li>Gera a URI do recurso criado</li>
   * <li>Retorna HTTP 201 (Created)</li>
   * </ol>
   *
   * @param categoryCreateRequest dados da categoria
   * @return categoria criada com status 201 e header Location
   */
  @PostMapping
  public ResponseEntity<CategoryResponse> insert(@RequestBody CategoryCreateRequest categoryCreateRequest) {
    logger.debug("Recebendo requisição para criar categoria: {}", categoryCreateRequest);

    CategoryResponse categoryResponse = categoryService.insert(categoryCreateRequest);

    URI uri = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(categoryResponse.id())
        .toUri();

    logger.info("Categoria criada com sucesso. id: {}", categoryResponse.id());
    return ResponseEntity.created(uri).body(categoryResponse);
  }

  /**
   * Endpoint para listar categorias com paginação.
   *
   * <p>
   * <b>Parâmetros suportados:</b>
   * </p>
   * <ul>
   * <li>page → número da página</li>
   * <li>linesPerPage → quantidade de registros por página</li>
   * <li>orderBy → campo para ordenação</li>
   * <li>direction → direção (ASC ou DESC)</li>
   * </ul>
   *
   * <p>
   * <b>Exemplo:</b>
   * </p>
   * 
   * <pre>
   * GET /api/v1/categories?page=0&linesPerPage=10&orderBy=name&direction=ASC
   * </pre>
   *
   * @return lista paginada de categorias
   */
  @GetMapping
  public ResponseEntity<Page<CategoryResponse>> findAll(
      @RequestParam(value = "page", defaultValue = "0") Integer page,
      @RequestParam(value = "linesPerPage", defaultValue = "12") Integer linesPerPage,
      @RequestParam(value = "orderBy", defaultValue = "name") String orderBy,
      @RequestParam(value = "direction", defaultValue = "ASC") String direction) {

    logger.debug("Buscando categorias paginadas - page: {}, size: {}, orderBy: {}, direction: {}",
        page, linesPerPage, orderBy, direction);

    Pageable pageable = PageRequest.of(page, linesPerPage, Direction.fromString(direction), orderBy);
    Page<CategoryResponse> response = categoryService.findAllPaged(pageable);

    logger.debug("Categorias retornadas: {}", response.getTotalElements());
    return ResponseEntity.ok(response);
  }

  /**
   * Endpoint para buscar uma categoria pelo ID.
   *
   * <p>
   * Retorna HTTP 200 caso encontre, ou 404 caso não exista.
   * </p>
   *
   * @param id identificador da categoria
   * @return categoria encontrada
   */
  @GetMapping(value = "/{id}")
  public ResponseEntity<CategoryResponse> findById(@PathVariable Long id) {
    logger.debug("Buscando categoria por id: {}", id);

    CategoryResponse response = categoryService.findById(id);

    logger.debug("Categoria encontrada: id={}", id);
    return ResponseEntity.ok(response);
  }

  /**
   * Endpoint para busca de categorias por nome.
   *
   * <p>
   * Realiza busca parcial (contém) e ignora maiúsculas/minúsculas.
   * </p>
   *
   * <p>
   * <b>Exemplo:</b>
   * </p>
   * 
   * <pre>
   * GET /api/v1/categories/search?name=eletron
   * </pre>
   *
   * @param name     termo de busca
   * @param pageable paginação automática do Spring
   * @return lista paginada de categorias filtradas
   */
  @GetMapping("/search")
  public ResponseEntity<Page<CategoryResponse>> search(@RequestParam String name, Pageable pageable) {
    logger.debug("Buscando categorias por nome: {}", name);

    Page<CategoryResponse> response = categoryService.searchByName(name, pageable);

    logger.debug("Categorias encontradas: {}", response.getTotalElements());
    return ResponseEntity.ok(response);
  }

  /**
   * Endpoint para atualização parcial de uma categoria.
   *
   * <p>
   * Utiliza o método PATCH, permitindo atualizar apenas os campos enviados.
   * </p>
   *
   * <p>
   * <b>Importante:</b>
   * </p>
   * <ul>
   * <li>Campos nulos NÃO são atualizados</li>
   * <li>Apenas campos informados são modificados</li>
   * </ul>
   *
   * @param id                    identificador da categoria
   * @param categoryUpdateRequest dados para atualização
   * @return categoria atualizada
   */
  @PatchMapping(value = "/{id}")
  public ResponseEntity<CategoryResponse> update(@PathVariable Long id,
      @RequestBody CategoryUpdateRequest categoryUpdateRequest) {

    logger.debug("Atualizando categoria id={} com dados: {}", id, categoryUpdateRequest);

    CategoryResponse response = categoryService.update(id, categoryUpdateRequest);

    logger.info("Categoria atualizada com sucesso. id={}", id);
    return ResponseEntity.ok(response);
  }

  /**
   * Endpoint para remoção de uma categoria.
   *
   * <p>
   * Retorna HTTP 204 (No Content) em caso de sucesso.
   * </p>
   *
   * <p>
   * <b>Possíveis erros:</b>
   * </p>
   * <ul>
   * <li>404 → categoria não encontrada</li>
   * <li>400 → violação de integridade</li>
   * </ul>
   *
   * @param id identificador da categoria
   * @return resposta sem conteúdo
   */
  @DeleteMapping(value = "/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    logger.debug("Deletando categoria id={}", id);

    categoryService.delete(id);

    logger.info("Categoria deletada com sucesso. id={}", id);
    return ResponseEntity.noContent().build();
  }
}