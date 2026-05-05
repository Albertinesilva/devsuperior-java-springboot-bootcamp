package com.albertsilva.dev.dscatalog.services;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.albertsilva.dev.dscatalog.dto.user.mapper.UserMapper;
import com.albertsilva.dev.dscatalog.dto.user.request.UserCreateRequest;
import com.albertsilva.dev.dscatalog.dto.user.request.UserUpdateRequest;
import com.albertsilva.dev.dscatalog.dto.user.response.UserDetailsResponse;
import com.albertsilva.dev.dscatalog.dto.user.response.UserResponse;
import com.albertsilva.dev.dscatalog.entities.Role;
import com.albertsilva.dev.dscatalog.entities.User;
import com.albertsilva.dev.dscatalog.repositories.RoleRepository;
import com.albertsilva.dev.dscatalog.repositories.UserRepository;
import com.albertsilva.dev.dscatalog.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

/**
 * Serviço responsável pelas operações de negócio relacionadas à entidade
 * {@link User}.
 *
 * <p>
 * Gerencia usuários, centralizando regras de negócio,
 * validações, persistência e tratamento transacional.
 * </p>
 *
 * <p>
 * <b>Responsabilidades:</b>
 * </p>
 * <ul>
 * <li>Operações de CRUD de usuários</li>
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
public class UserService {

  private static final Logger logger = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final UserMapper userMapper;
  private final BCryptPasswordEncoder passwordEncoder;

  /**
   * Constrói o serviço de usuários com suas dependências principais.
   *
   * @param userRepository  repositório de usuários
   * @param roleRepository  repositório de papéis
   * @param userMapper      responsável pela conversão entre DTOs e entidades
   * @param passwordEncoder codificador de senhas
   */
  public UserService(UserRepository userRepository, RoleRepository roleRepository, UserMapper userMapper,
      BCryptPasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.userMapper = userMapper;
    this.passwordEncoder = passwordEncoder;
  }

  /**
   * Busca paginada de usuários, com opção de filtro por nome.
   *
   * <p>
   * Permite busca parcial e case insensitive,
   * utilizando correspondência por conteúdo textual.
   * </p>
   *
   * @param firstName termo de busca para o nome do usuário
   * @param pageable  informações de paginação
   * @return página de usuários encontrados
   *
   * @implNote
   *           Utiliza consulta derivada do Spring Data JPA:
   *           {@code findByFirstNameContainingIgnoreCase}.
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
  public Page<UserResponse> findAllPaged(String firstName, Pageable pageable) {
    logger.debug("Buscando usuários. filtroNome: {}", firstName);

    Page<User> users;

    if (hasText(firstName)) {
      users = userRepository.findByFirstNameContainingIgnoreCase(firstName.trim(), pageable);
    } else {
      users = userRepository.findAll(pageable);
    }

    logger.debug("Total de usuários encontrados: {}", users.getTotalElements());

    return userMapper.toResponsePage(users);
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
  public UserDetailsResponse findById(Long id) {
    logger.debug("Buscando usuário por id: {}", id);

    User entity = userRepository.findById(id).orElseThrow(() -> {
      logger.warn("Usuário não encontrado. id: {}", id);
      return new ResourceNotFoundException("Entity not found id: " + id);
    });

    logger.debug("Usuário encontrado. id: {}", id);
    return userMapper.toDetailsResponse(entity);
  }

  /**
   * Insere um novo usuário no sistema.
   *
   * <p>
   * Converte o DTO de entrada em entidade
   * e persiste os dados no banco.
   * </p>
   *
   * @param userCreateRequest dados para criação do usuário
   * @return usuário criado
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
  public UserResponse insert(UserCreateRequest request) {
    logger.debug("Inserindo novo usuário - email: {}", request.email());

    Set<Role> roles = findRolesByIdsOrThrow(request.roleIds());

    User entity = userMapper.toEntity(request, roles);
    entity.setPassword(passwordEncoder.encode(request.password()));

    entity = userRepository.save(entity);
    logger.info("Usuário criado com sucesso. id: {}", entity.getId());
    return userMapper.toResponse(entity);
  }

  /**
   * Atualiza parcialmente um usuário existente.
   *
   * <p>
   * Permite modificar apenas campos informados,
   * preservando dados não enviados.
   * </p>
   *
   * @param id                identificador do usuário
   * @param userUpdateRequest dados para atualização parcial
   * @return usuário atualizado
   * @throws ResourceNotFoundException caso o usuário não exista
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
  public UserResponse update(Long id, UserUpdateRequest request) {

    logger.debug("Atualizando usuário. id: {}", id);

    try {
      User entity = userRepository.getReferenceById(id);

      Set<Role> roles = findRolesByIdsOrThrow(request.roleIds());

      userMapper.updateEntity(request, entity, roles);

      if (request.password() != null) {
        entity.setPassword(passwordEncoder.encode(request.password()));
      }

      entity = userRepository.save(entity);

      logger.info("Usuário atualizado com sucesso. id: {}", id);

      return userMapper.toResponse(entity);

    } catch (EntityNotFoundException e) {
      logger.warn("Falha ao atualizar. Usuário não encontrado. id: {}", id);
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
   * <li>Usuário não encontrado →
   * {@link ResourceNotFoundException}</li>
   * <li>Violação de integridade referencial →
   * tratada globalmente via {@code @RestControllerAdvice}</li>
   * </ul>
   *
   * @param id identificador do usuário
   * @throws ResourceNotFoundException caso o usuário não exista
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
    logger.debug("Deletando usuário. id: {}", id);

    User entity = userRepository.findById(id).orElseThrow(() -> {
      logger.warn("Falha ao deletar. Usuário não encontrado. id: {}", id);
      return new ResourceNotFoundException("Entity not found id: " + id);
    });

    userRepository.delete(entity);
    logger.info("Usuário deletado com sucesso. id: {}", id);
  }

  /**
   * Realiza busca paginada de usuários por nome.
   *
   * <p>
   * Permite busca parcial e case insensitive,
   * utilizando correspondência por conteúdo textual.
   * </p>
   *
   * @param name     termo de busca
   * @param pageable informações de paginação
   * @return página de usuários encontrados
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
  public Page<UserResponse> searchByName(String firstName, Pageable pageable) {
    logger.debug("Buscando usuários por nome. termo: {}", firstName);

    Page<User> users = userRepository.findByFirstNameContainingIgnoreCase(firstName, pageable);

    logger.debug("Resultado da busca por nome '{}' - total encontrados: {}", firstName, users.getTotalElements());
    return userMapper.toResponsePage(users);
  }

  /**
   * Busca um conjunto de papéis (roles) por seus identificadores.
   *
   * <p>
   * Valida que todos os IDs fornecidos correspondem a papéis existentes,
   * garantindo integridade referencial antes de associar a um usuário.
   * </p>
   *
   * @param roleIds conjunto de IDs de papéis
   * @return conjunto de entidades {@link Role} correspondentes
   * @throws ResourceNotFoundException caso algum ID não corresponda a um papel
   *                                   existente
   *
   * @implNote
   *           Utiliza {@code findAllById(roleIds)} para consulta eficiente
   *           e validação de existência em lote, evitando múltiplas consultas
   *           individuais.
   *
   * @apiNote
   *          Esta implementação reforça conceitos importantes como:
   *          validação de dados, integridade referencial,
   *          otimização de consultas e tratamento de exceções.
   */
  private Set<Role> findRolesByIdsOrThrow(Set<Long> roleIds) {

    if (roleIds == null) {
      return null;
    }

    Set<Role> roles = new HashSet<>(roleRepository.findAllById(roleIds));

    if (roles.size() != roleIds.size()) {
      throw new ResourceNotFoundException("One or more roles not found for ids: " + roleIds);
    }

    return roles;
  }

  /**
   * Verifica se uma string possui texto (não é nula, vazia ou apenas espaços).
   *
   * @param value string a ser verificada
   * @return {@code true} se a string tiver texto, {@code false} caso contrário
   */
  private boolean hasText(String value) {
    return value != null && !value.trim().isEmpty();
  }
}