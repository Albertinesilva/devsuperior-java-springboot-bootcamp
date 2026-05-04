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
import com.albertsilva.dev.dscatalog.dto.user.request.UserCreateRequest;
import com.albertsilva.dev.dscatalog.dto.user.response.UserResponse;
import com.albertsilva.dev.dscatalog.services.CategoryService;
import com.albertsilva.dev.dscatalog.services.UserService;
import com.albertsilva.dev.dscatalog.web.exceptions.advice.ProblemDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller responsável por expor os endpoints REST da entidade User.
 *
 * <p>
 * Esta classe recebe requisições HTTP, delega o processamento para a camada
 * de serviço ({@link UserService}) e retorna respostas padronizadas.
 * </p>
 *
 * <p>
 * <b>Base URL:</b> /api/v1/users
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
@Tag(name = "Usuários", description = "Contém todas as operações aos recursos para cadastro, edição e leitura de um usuário.")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

  private static final Logger logger = LoggerFactory.getLogger(UserController.class);

  private final UserService userService;

  /**
   * Construtor para injeção de dependência do serviço.
   *
   * @param userService serviço de usuários
   */
  public UserController(UserService userService) {
    this.userService = userService;
  }

  /**
   * Endpoint para criação de um novo usuário.
   *
   * <p>
   * Recebe um JSON contendo os dados do usuário e retorna o recurso criado.
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
   * @param userCreateRequest dados do usuário
   * @return usuário criado com status 201 e header Location
   */
  @Operation(summary = "Cria um novo usuário", description = "Recurso para criar um novo usuário no sistema.", responses = {
      @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
      @ApiResponse(responseCode = "400", description = "Dados inválidos ou campos obrigatórios ausentes", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetails.class))),
      @ApiResponse(responseCode = "409", description = "Usuário já existente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetails.class)))
  })
  @PostMapping
  public ResponseEntity<UserResponse> insert(@RequestBody UserCreateRequest userCreateRequest) {

    logger.debug("Recebendo requisição para criar usuário: {}", userCreateRequest);
    UserResponse response = userService.insert(userCreateRequest);
    URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(response.id()).toUri();

    logger.info("Usuário criado com sucesso. id: {}", response.id());
    return ResponseEntity.created(uri).body(response);
  }

  /**
   * Endpoint para listar usuários com paginação.
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
   * GET /api/v1/users?page=0&linesPerPage=10&direction=ASC&orderBy=firstName
   * </pre>
   *
   * @return lista paginada de usuários
   */
  // @Operation(summary = "Lista todos os usuários com paginação", description =
  // "Exige Bearer Token. Acesso restrito a ADMIN.", security =
  // @SecurityRequirement(name = "security"), responses = {
  // @ApiResponse(responseCode = "200", description = "Lista paginada de
  // usuários", content = @Content(mediaType = "application/json", array =
  // @ArraySchema(schema = @Schema(implementation = UserResponse.class)))),
  // @ApiResponse(responseCode = "403", description = "Usuário sem permissão para
  // acessar este recurso", content = @Content(mediaType = "application/json",
  // schema = @Schema(implementation = ProblemDetails.class)))
  // })
  // @GetMapping
  // public ResponseEntity<Page<UserResponse>> findAll(
  // @RequestParam(value = "page", defaultValue = "0") Integer page,
  // @RequestParam(value = "linesPerPage", defaultValue = "12") Integer
  // linesPerPage,
  // @RequestParam(value = "direction", defaultValue = "ASC") String direction,
  // @RequestParam(value = "orderBy", defaultValue = "firstName") String orderBy)
  // {

  // logger.debug("Buscando usuários paginados - page: {}, size: {}, orderBy: {},
  // direction: {}",
  // page, linesPerPage, orderBy, direction);

  // Pageable pageable = PageRequest.of(page, linesPerPage,
  // Direction.fromString(direction), orderBy);
  // Page<UserResponse> response = userService.findAllPaged(pageable);

  // logger.debug("Usuários retornados: {}", response.getTotalElements());
  // return ResponseEntity.ok(response);
  // }

  /**
   * Endpoint para buscar um usuário pelo ID.
   *
   * <p>
   * Retorna HTTP 200 caso encontre, ou 404 caso não exista.
   * </p>
   *
   * @param id identificador do usuário
   * @return usuário encontrado
   */
  // @Operation(summary = "Busca um usuário pelo ID", description = "Recurso para
  // obter detalhes de um usuário específico pelo seu ID.", responses = {
  // @ApiResponse(responseCode = "200", description = "Usuário encontrado com
  // sucesso", content = @Content(mediaType = "application/json", schema =
  // @Schema(implementation = UserResponse.class))),
  // @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
  // content = @Content(mediaType = "application/json", schema =
  // @Schema(implementation = ProblemDetails.class)))
  // })
  // @GetMapping(value = "/{id}")
  // public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
  // logger.debug("Buscando usuário por id: {}", id);

  // UserResponse response = userService.findById(id);

  // logger.debug("Usuário encontrado: id={}", id);
  // return ResponseEntity.ok(response);
  // }

  /**
   * Endpoint para busca de usuários por nome.
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
   * GET /api/v1/users/search?name=eletron
   * </pre>
   *
   * @param name     termo de busca
   * @param pageable paginação automática do Spring
   * @return lista paginada de usuários filtrados
   */
  // @Operation(summary = "Busca usuários por nome", description = "Permite busca
  // parcial de usuários pelo nome. Case insensitive.", responses = {
  // @ApiResponse(responseCode = "200", description = "Lista paginada de usuários
  // filtrados", content = @Content(mediaType = "application/json", array =
  // @ArraySchema(schema = @Schema(implementation = UserResponse.class)))),
  // @ApiResponse(responseCode = "400", description = "Parâmetro de busca
  // inválido", content = @Content(mediaType = "application/json", schema =
  // @Schema(implementation = ProblemDetails.class)))
  // })
  // @GetMapping("/search")
  // public ResponseEntity<Page<UserResponse>> search(@RequestParam String name,
  // Pageable pageable) {
  // logger.debug("Buscando usuários por nome: {}", name);

  // Page<UserResponse> response = userService.searchByName(name, pageable);

  // logger.debug("Usuários encontrados: {}", response.getTotalElements());
  // return ResponseEntity.ok(response);
  // }

  /**
   * Endpoint para atualização parcial de um usuário.
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
   * @param id                identificador do usuário
   * @param userUpdateRequest dados para atualização
   * @return usuário atualizado
   */
  // @Operation(summary = "Atualiza um usuário", description = "Atualização
  // parcial dos dados do usuário. Apenas campos enviados são alterados.",
  // responses = {
  // @ApiResponse(responseCode = "200", description = "Usuário atualizado com
  // sucesso", content = @Content(mediaType = "application/json", schema =
  // @Schema(implementation = UserResponse.class))),
  // @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
  // content = @Content(mediaType = "application/json", schema =
  // @Schema(implementation = ProblemDetails.class))),
  // @ApiResponse(responseCode = "400", description = "Dados inválidos", content =
  // @Content(mediaType = "application/json", schema = @Schema(implementation =
  // ProblemDetails.class)))
  // })
  // @PatchMapping(value = "/{id}")
  // public ResponseEntity<UserResponse> update(@PathVariable Long id,
  // @RequestBody UserUpdateRequest userUpdateRequest) {

  // logger.debug("Atualizando usuário id={} com dados: {}", id,
  // userUpdateRequest);

  // UserResponse response = userService.update(id, userUpdateRequest);

  // logger.info("Usuário atualizado com sucesso. id={}", id);
  // return ResponseEntity.ok(response);
  // }

  /**
   * Endpoint para remoção de um usuário.
   *
   * <p>
   * Retorna HTTP 204 (No Content) em caso de sucesso.
   * </p>
   *
   * <p>
   * <b>Possíveis erros:</b>
   * </p>
   * <ul>
   * <li>404 → usuário não encontrado</li>
   * <li>409 → violação de integridade</li>
   * </ul>
   *
   * @param id identificador do usuário
   * @return resposta sem conteúdo
   */
  // @Operation(summary = "Remove um usuário", description = "Exclui um usuário
  // pelo ID. Retorna erro se houver integridade referencial.", responses = {
  // @ApiResponse(responseCode = "204", description = "Usuário deletado com
  // sucesso"),
  // @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
  // content = @Content(mediaType = "application/json", schema =
  // @Schema(implementation = ProblemDetails.class))),
  // @ApiResponse(responseCode = "409", description = "Violação de integridade -
  // existem entidades relacionadas", content = @Content(mediaType =
  // "application/json", schema = @Schema(implementation = ProblemDetails.class)))
  // })
  // @DeleteMapping(value = "/{id}")
  // public ResponseEntity<Void> delete(@PathVariable Long id) {
  // logger.debug("Deletando usuário id={}", id);

  // userService.delete(id);

  // logger.info("Usuário deletado com sucesso. id={}", id);
  // return ResponseEntity.noContent().build();
  // }
}