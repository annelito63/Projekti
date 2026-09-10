package ohjelmistoprojekti.projekti.model;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface TarkastusRepository extends CrudRepository<Tarkastus, Long> {
    List<Tarkastus> findByLippu(Lippu lippu);

    List<Tarkastus> findByTarkastaja(Kayttaja tarkastaja);
}
