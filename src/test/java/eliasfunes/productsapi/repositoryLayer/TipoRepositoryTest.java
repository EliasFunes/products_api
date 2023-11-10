package eliasfunes.productsapi.repositoryLayer;

import eliasfunes.productsapi.models.Tipo;
import eliasfunes.productsapi.repository.TipoRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/*
  Se utiliza testcontainers para generar un contenedor de postgres:14 para lograr
  un acercamiento a la db de produccion, a mi criterio es lo mejor, acercar los procesos
  de test a las dependencias y tecnologias utilizadas en produccion, debido a que H2 podria tener limitantes
  o funcionalidades que no posee postgres que es el motor utilizado por la capa de
  redundancia en produccion.
  Con esto se puede inyectar la dependencia con la tecnologia exacta requerida por el repository de forma aislada.
  */


/**
 * No esta diseñado para correr cada test de forma independiente, correr toda la clase ya que algunos metodos test
 * pueden depender de los anteriores, por eso se utiliza el order.
 */

@Testcontainers
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TipoRepositoryTest {

    @Autowired
    private TipoRepository tipoRepository;

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:14")
            .withDatabaseName("unit-tests-db").withUsername("username").withPassword("password")
            /*.withInitScript("test-data.sql")*/ //se puede crear un script sql para inicializar con tablas, se lee el archivo desde la carpeta test/resources
     ;

    static {
        postgreSQLContainer.start();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }


    Tipo defaultInsertOne = Tipo.builder().nombre("carnico").usuario("test").createdDate(Instant.now()).build();
//    Tipo defaultInsertTwo = Tipo.builder().nombre("electronico").usuario("test").createdDate(Instant.now()).build();

    @Test
    @Order(1)
    void findAllNull(){
        List<Tipo> allOne = tipoRepository.findAll();
        assertThat(allOne).isEmpty();
    }

    @Test
    @Order(2)
    void findByIdNull(){
        Optional<Tipo> found = tipoRepository.findById(1L);
        assertFalse(found.isPresent());
    }

    @Test
    @Order(3)
    void insertNewTipo(){

        Tipo tipoSaved = tipoRepository.save(defaultInsertOne);

        assertThat(tipoSaved).isNotNull();

        assertThat(tipoSaved.getId()).isGreaterThan(0);
        assertThat(tipoSaved.getId()).isEqualTo(1L);

        assertThat(tipoSaved.getNombre()).isNotNull();
        assertThat(tipoSaved.getNombre()).isNotEqualTo("");
        assertThat(tipoSaved.getNombre()).isEqualTo(defaultInsertOne.getNombre());

        assertThat(tipoSaved.getCreatedDate()).isNotNull();
        assertThat(tipoSaved.getCreatedDate()).isBeforeOrEqualTo(Instant.now());
    }

    @Test
    @Order(4)
    void insertTwice(){
        Exception exception = assertThrows(DataIntegrityViolationException.class, () -> tipoRepository.save(defaultInsertOne));
        assertTrue(exception.getMessage().contains("duplicate key value violates unique constraint \"tipos_nombre_key\""));
    }

    @Test
    @Order(5)
    void insertNombreNull(){
        Tipo tipoToInsert = Tipo.builder().usuario("test").createdDate(Instant.now()).build();
        Exception exception = assertThrows(DataIntegrityViolationException.class, () -> tipoRepository.save(tipoToInsert));
        assertTrue(exception.getMessage().contains("not-null property references a null"));
        assertTrue(exception.getMessage().contains("nombre"));
    }

    @Test
    @Order(6)
    void insertUsuarioNull(){
        Tipo tipoToInsert = Tipo.builder().nombre("carnico2").createdDate(Instant.now()).build();
        Exception exception = assertThrows(DataIntegrityViolationException.class, () -> tipoRepository.save(tipoToInsert));
        assertTrue(exception.getMessage().contains("not-null property references a null"));
        assertTrue(exception.getMessage().contains("usuario"));
    }

    @Test
    @Order(7)
    void insertCreatedDateNull(){
        Tipo tipoToInsert = Tipo.builder().nombre("carnico2").usuario("test").build();
        Exception exception = assertThrows(DataIntegrityViolationException.class, () -> tipoRepository.save(tipoToInsert));
        assertTrue(exception.getMessage().contains("not-null property references a null"));
        assertTrue(exception.getMessage().contains("createdDate"));
    }

    @Test
    @Order(8)
    void findAllNotNull(){
        List<Tipo> allOne = tipoRepository.findAll();
        assertThat(allOne).isNotNull();
        assertThat(allOne.size()).isEqualTo(1);

        Optional<Tipo> maybeFirst = allOne.stream().findFirst();

        assertTrue(maybeFirst.isPresent());
        assertThat(maybeFirst.get().getId()).isEqualTo(1L);
        assertThat(maybeFirst.get().getNombre()).isEqualTo(defaultInsertOne.getNombre());
        assertThat(maybeFirst.get().getUsuario()).isEqualTo(defaultInsertOne.getUsuario());
        assertThat(maybeFirst.get().getCreatedDate()).isNotNull();


    }

    @Test
    @Order(9)
    void findByIdNotNull(){
        Optional<Tipo> found = tipoRepository.findById(1L);

        assertTrue(found.isPresent());
        assertThat(found.get()).isNotNull();
        assertThat(found.get().getId()).isEqualTo(1L);
        assertThat(found.get().getNombre()).isEqualTo(defaultInsertOne.getNombre());
        assertThat(found.get().getUsuario()).isEqualTo(defaultInsertOne.getUsuario());
        assertThat(found.get().getCreatedDate()).isNotNull();
    }
}
