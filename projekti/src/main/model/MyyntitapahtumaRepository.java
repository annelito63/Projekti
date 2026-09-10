import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface MyyntitapahtumaRepository extends CrudRepository<Myyntitapahtuma, Long> {
    List<Myyntitapahtuma> findByKayttaja(Kayttaja kayttaja);

    List<Myyntitapahtuma> findByTapahtuma(Tapahtuma tapahtuma);
}
