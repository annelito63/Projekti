import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface LippuRepository extends CrudRepository<Lippu, Long> {
    List<Lippu> findByNimi(String nimi);
    List<Lippu> findByHinta(double hinta);

}
