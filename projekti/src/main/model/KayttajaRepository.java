import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface KayttajaRepository extends CrudRepository<Kayttaja, Long> {
    List<Kayttaja> findByNimi(String nimi);

    List<Kayttaja> findByRooli(Rooli rooli);
}
