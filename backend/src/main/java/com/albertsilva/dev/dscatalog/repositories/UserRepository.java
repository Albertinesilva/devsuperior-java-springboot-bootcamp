package com.albertsilva.dev.dscatalog.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.albertsilva.dev.dscatalog.entities.User;

/**
 * Repositório responsável pelo acesso a dados da entidade {@link User}.
 *
 * <p>
 * Esta interface utiliza o Spring Data JPA para fornecer operações
 * de persistência de forma automática, sem necessidade de implementação manual.
 * </p>
 *
 * <p>
 * <b>Funcionalidades fornecidas automaticamente:</b>
 * </p>
 * <ul>
 * <li>Salvar um usuário (save)</li>
 * <li>Buscar por ID (findById)</li>
 * <li>Listar todas (findAll)</li>
 * <li>Deletar (delete)</li>
 * </ul>
 *
 * <p>
 * <b>Importante para júnior:</b>
 * </p>
 * <ul>
 * <li>Você não implementa essa interface</li>
 * <li>O Spring gera tudo em tempo de execução</li>
 * <li>O nome dos métodos define a query automaticamente</li>
 * </ul>
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  /**
   * Busca usuários cujo nome contenha o valor informado (ignorando diferenças
   * entre
   * maiúsculas/minúsculas).
   *
   * <p>
   * <b>Como o Spring interpreta esse método:</b>
   * </p>
   * <ul>
   * <li><b>findBy</b> → operação de busca</li>
   * <li><b>FirstName</b> → campo da entidade</li>
   * <li><b>Containing</b> → busca parcial (LIKE %valor%)</li>
   * <li><b>IgnoreCase</b> → ignora diferenças entre maiúsculas/minúsculas</li>
   * </ul>
   *
   * <p>
   * <b>Exemplo prático:</b>
   * </p>
   * 
   * <pre>
   * Busca por "ele" pode retornar:
   * - "Alexandre"
   * - "Elenice"
   * </pre>
   *
   * <p>
   * <b>Paginação:</b>
   * </p>
   * <ul>
   * <li>O resultado é paginado usando {@link Pageable}</li>
   * <li>Evita retorno de grandes volumes de dados</li>
   * </ul>
   *
   * @param firstName termo para busca no campo "firstName"
   * @param pageable  configurações de paginação (página, tamanho, ordenação)
   * @return página contendo usuários encontrados
   */
  Page<User> findByFirstNameContainingIgnoreCase(String firstName, Pageable pageable);
}
