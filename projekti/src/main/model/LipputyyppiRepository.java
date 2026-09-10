import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface LipputyyppiRepository extends CrudRepository<Lipputyyppi, Long> {
    List<Lipputyyppi> findByNimi(String nimi);

    List<Lipputyyppi> findByTapahtuma(Tapahtuma tapahtuma);
}
