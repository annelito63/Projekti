package ohjelmistoprojekti.projekti.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface LippuRepository extends CrudRepository<Lippu, Long> {
    Optional<Lippu> findByKoodi(String koodi);

    List<Lippu> findByHinta(double hinta);

}
