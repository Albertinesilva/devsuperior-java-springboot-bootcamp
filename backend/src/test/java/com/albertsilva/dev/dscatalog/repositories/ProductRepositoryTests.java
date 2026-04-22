package com.albertsilva.dev.dscatalog.repositories;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.albertsilva.dev.dscatalog.entities.Category;
import com.albertsilva.dev.dscatalog.entities.Product;
import com.albertsilva.dev.dscatalog.factory.CategoryFactory;
import com.albertsilva.dev.dscatalog.factory.ProductFactory;

@DataJpaTest
public class ProductRepositoryTests {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Test
  @DisplayName("Delete should delete object when id exists")
  public void deleteShouldDeleteObjectWhenIdExists() {
    // Arrange
    Long existingId = productRepository.save(ProductFactory.createProduct()).getId();

    // Act
    productRepository.deleteById(existingId);
    productRepository.flush();

    // Assert
    Assertions.assertThat(productRepository.existsById(existingId)).as("Product should be deleted when id exists")
        .isFalse();

  }

  @Test
  @DisplayName("Delete should remove product and its associations")
  public void deleteShouldDeleteProductAndRemoveAssociationsWhenProductHasCategories() {

    // Arrange
    Category category = categoryRepository.save(CategoryFactory.createCategory());

    Product product = ProductFactory.createProduct();
    product.getCategories().add(category);

    product = productRepository.save(product);
    Long id = product.getId();

    // Act
    productRepository.deleteById(id);
    productRepository.flush();

    // Assert
    Assertions.assertThat(productRepository.existsById(id)).isFalse();
    Assertions.assertThat(countProductCategoryAssociations(id))
        .as("Associations in tb_product_category should be removed after product deletion")
        .isZero();
  }

  @Test
  @DisplayName("Save should persist with auto increment when id is null")
  void saveShouldPersistWithAutoIncrementWhenIdIsNull() {

    // Arrange
    long countBefore = productRepository.count();
    Product product = ProductFactory.createProduct();
    product.setId(null);

    // Act
    product = productRepository.save(product);

    // Assert
    Assertions.assertThat(product.getId()).as("Id should be generated automatically").isNotNull().isPositive();

    Assertions.assertThat(productRepository.count()).as("Repository count should increase after save")
        .isEqualTo(countBefore + 1);
  }

  /**
   * Conta a quantidade de registros na tabela de junção
   * {@code tb_product_category}
   * associados a um determinado {@code productId}.
   *
   * <p>
   * Este método é utilizado em testes de integração com JPA para validar o estado
   * do relacionamento {@code ManyToMany} entre {@code Product} e {@code Category}
   * diretamente no banco de dados.
   * </p>
   *
   * <p>
   * A consulta é executada de forma nativa (SQL), garantindo que o resultado
   * reflita
   * exatamente o estado persistido na base de dados, ignorando o contexto de
   * persistência
   * do Hibernate (cache de primeiro nível).
   * </p>
   *
   * <p>
   * <b>Uso típico:</b>
   * </p>
   * <ul>
   * <li>Verificar se as associações foram corretamente removidas após um
   * delete</li>
   * <li>Validar integridade da tabela de junção em cenários de
   * relacionamento</li>
   * </ul>
   *
   * @param productId identificador do produto a ser verificado
   * @return quantidade de registros na tabela {@code tb_product_category}
   *         relacionados ao produto
   */
  private long countProductCategoryAssociations(Long productId) {
    Number result = (Number) entityManager
        .getEntityManager()
        .createNativeQuery(
            "SELECT COUNT(*) FROM tb_product_category WHERE product_id = :id")
        .setParameter("id", productId)
        .getSingleResult();

    return result.longValue();
  }
}
