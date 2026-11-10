package ohjelmistoprojekti.projekti.model;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.CrudRepository;

public interface TapahtumaRepository extends CrudRepository<Tapahtuma, Long> {
    List<Tapahtuma> findByNimi(String nimi);

    List<Tapahtuma> findByPaikka(String paikka);

    List<Tapahtuma> findAll();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Tapahtuma t where t.tapahtumaid = :id")
    Optional<Tapahtuma> findByIdForUpdate(@Param("id") Long id);
}
