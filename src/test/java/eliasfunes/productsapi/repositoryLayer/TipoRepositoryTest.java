package eliasfunes.productsapi.repositoryLayer;

import eliasfunes.productsapi.models.Tipo;
import eliasfunes.productsapi.repository.TipoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) //TODO: ver como utilizar la db H2 para test o un schema especifico para test
public class TipoRepositoryTest {

    @Autowired
    private TipoRepository tipoRepository;

    @Test
    void insertNewTipo(){

        //TODO: buscar la forma de que la fecha sea autogenerada y no pasar por parametro
        Tipo tipoToInsert = Tipo.builder().nombre("carnico").usuario("test").creadoEn(LocalDateTime.now()).build();
        Tipo tipoSaved = tipoRepository.save(tipoToInsert);

        assertThat(tipoSaved).isNotNull();
        assertThat(tipoSaved.getId()).isGreaterThan(0);
        assertThat(tipoSaved.getNombre()).isNotNull();
        assertThat(tipoSaved.getNombre()).isNotEqualTo("");
        assertThat(tipoSaved.getCreadoEn()).isNotNull();

        //TODO: Faltan assert para el nombre = al nombre pasado y id debe ser 1

    }
}
