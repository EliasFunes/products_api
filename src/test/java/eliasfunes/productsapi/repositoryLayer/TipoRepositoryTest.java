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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Se utiliza testcontainers para generar un contenedor de postgres:14 para lograr
 * un acercamiento a la db de produccion, a mi criterio es lo mejor, acercar los procesos
 * de test a las dependencias y tecnologias utilizadas en produccion, debido a que H2 podria tener limitantes
 * o funcionalidades que no posee postgres que es el motor utilizado por la capa de
 * redundancia en produccion.
 * Con esto se puede inyectar la dependencia con la tecnologia exacta requerida por el repository de forma aislada.
 * */

@Testcontainers
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TipoRepositoryTest {

    @Autowired
    private TipoRepository tipoRepository;

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:14")
            .withDatabaseName("integration-tests-db").withUsername("username").withPassword("password")
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

    @Test
    @Order(1)
    void insertNewTipo(){
        Tipo tipoToInsert = Tipo.builder().nombre("carnico").usuario("test").createdDate(Instant.now()).build();
        Tipo tipoSaved = tipoRepository.save(tipoToInsert);

        assertThat(tipoSaved).isNotNull();

        assertThat(tipoSaved.getId()).isGreaterThan(0);
        assertThat(tipoSaved.getId()).isEqualTo(1L);

        assertThat(tipoSaved.getNombre()).isNotNull();
        assertThat(tipoSaved.getNombre()).isNotEqualTo("");
        assertThat(tipoSaved.getNombre()).isEqualTo(tipoToInsert.getNombre());

        assertThat(tipoSaved.getCreatedDate()).isNotNull();
        assertThat(tipoSaved.getCreatedDate()).isBeforeOrEqualTo(Instant.now());
    }

    @Test
    @Order(2)
    void insertTwice(){
        Tipo tipoToInsert = Tipo.builder().nombre("carnico").usuario("test").createdDate(Instant.now()).build();
        Exception exception = assertThrows(DataIntegrityViolationException.class, () -> tipoRepository.save(tipoToInsert));
        assertTrue(exception.getMessage().contains("duplicate key value violates unique constraint \"tipos_nombre_key\""));
    }

    @Test
    @Order(3)
    void insertNombreNull(){
        Tipo tipoToInsert = Tipo.builder().usuario("test").createdDate(Instant.now()).build();
        Exception exception = assertThrows(DataIntegrityViolationException.class, () -> tipoRepository.save(tipoToInsert));
        assertTrue(exception.getMessage().contains("not-null property references a null"));
        assertTrue(exception.getMessage().contains("nombre"));
    }

    @Test
    @Order(4)
    void insertUsuarioNull(){
        Tipo tipoToInsert = Tipo.builder().nombre("carnico2").createdDate(Instant.now()).build();
        Exception exception = assertThrows(DataIntegrityViolationException.class, () -> tipoRepository.save(tipoToInsert));
        assertTrue(exception.getMessage().contains("not-null property references a null"));
        assertTrue(exception.getMessage().contains("usuario"));
    }

    @Test
    @Order(5)
    void insertCreatedDateNull(){
        Tipo tipoToInsert = Tipo.builder().nombre("carnico2").usuario("test").build();
        Exception exception = assertThrows(DataIntegrityViolationException.class, () -> tipoRepository.save(tipoToInsert));
        assertTrue(exception.getMessage().contains("not-null property references a null"));
        assertTrue(exception.getMessage().contains("createdDate"));
    }
}
