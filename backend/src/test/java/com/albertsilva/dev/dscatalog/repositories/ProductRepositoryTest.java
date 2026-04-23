package com.albertsilva.dev.dscatalog.repositories;

import java.util.Optional;

import org.assertj.core.api.Assertions;
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
public class ProductRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Test
  @DisplayName("Delete should delete object when id exists")
  public void deleteShouldDeleteObjectWhenIdExists() {
    Long existingId = productRepository.save(ProductFactory.createProduct()).getId();

    productRepository.deleteById(existingId);
    productRepository.flush();

    Assertions.assertThat(productRepository.existsById(existingId))
        .as("Product should be deleted when id exists")
        .isFalse();
  }

  @Test
  @DisplayName("Delete should remove product and its associations")
  public void deleteShouldDeleteProductAndRemoveAssociationsWhenProductHasCategories() {

    Category category = categoryRepository.save(CategoryFactory.createCategory());

    Product product = ProductFactory.createProduct();
    product.getCategories().add(category);

    product = productRepository.save(product);
    Long id = product.getId();

    productRepository.deleteById(id);
    productRepository.flush();

    Assertions.assertThat(productRepository.existsById(id)).isFalse();

    Assertions.assertThat(countProductCategoryAssociations(id))
        .as("Associations in tb_product_category should be removed after product deletion")
        .isZero();
  }

  @Test
  @DisplayName("Delete should not delete associated categories")
  void deleteShouldNotDeleteCategories() {

    Category category = categoryRepository.save(CategoryFactory.createCategory());

    Product product = ProductFactory.createProduct();
    product.getCategories().add(category);
    product = productRepository.save(product);

    Long productId = product.getId();
    Long categoryId = category.getId();

    productRepository.deleteById(productId);
    productRepository.flush();

    Assertions.assertThat(categoryRepository.existsById(categoryId)).isTrue();
  }

  @Test
  @DisplayName("Delete should decrease repository count")
  void deleteShouldDecreaseCount() {

    Product product = productRepository.save(ProductFactory.createProduct());
    long countBefore = productRepository.count();

    productRepository.deleteById(product.getId());
    productRepository.flush();

    Assertions.assertThat(productRepository.count()).as("Repository count should decrease after delete")
        .isEqualTo(countBefore - 1);
  }

  @Test
  @DisplayName("Save should persist with auto increment when id is null")
  void saveShouldPersistWithAutoIncrementWhenIdIsNull() {

    long countBefore = productRepository.count();

    Product product = ProductFactory.createProduct();
    product.setId(null);

    product = productRepository.save(product);

    Assertions.assertThat(product.getId()).as("Id should be generated automatically")
        .isNotNull()
        .isPositive();

    Assertions.assertThat(productRepository.count()).as("Repository count should increase after save")
        .isEqualTo(countBefore + 1);
  }

  @Test
  @DisplayName("Save should update entity when id exists")
  void saveShouldUpdateEntityWhenIdExists() {

    Product product = productRepository.save(ProductFactory.createProduct());

    product.setName("Updated Name");
    product = productRepository.save(product);

    Assertions.assertThat(product.getName()).isEqualTo("Updated Name");
  }

  @Test
  @DisplayName("Save should persist product with categories")
  void saveShouldPersistProductWithCategories() {

    Category category = categoryRepository.save(CategoryFactory.createCategory());

    Product product = ProductFactory.createProduct();
    product.getCategories().add(category);

    product = productRepository.save(product);

    Assertions.assertThat(product.getCategories()).as("Product should contain associated categories")
        .isNotEmpty();
  }

  @Test
  @DisplayName("FindById should return non empty optional when id exists")
  public void findByIdShouldReturnNonEmptyOptionalWhenIdExists() {

    Product product = productRepository.save(ProductFactory.createProduct());

    Optional<Product> result = productRepository.findById(product.getId());

    Assertions.assertThat(result).as("Product should be found for existing id")
        .isPresent()
        .contains(product);
  }

  @Test
  @DisplayName("FindById should return empty optional when id does not exist")
  public void findByIdShouldReturnEmptyOptionalWhenIdDoesNotExist() {

    Product product = productRepository.save(ProductFactory.createProduct());
    Long nonExistingId = product.getId() + 1000L;

    Optional<Product> result = productRepository.findById(nonExistingId);

    Assertions.assertThat(result).as("Product should not be found for non existing id")
        .isEmpty();
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
    Number result = (Number) entityManager.getEntityManager()
        .createNativeQuery("SELECT COUNT(*) FROM tb_product_category WHERE product_id = :id")
        .setParameter("id", productId).getSingleResult();

    return result.longValue();
  }
}