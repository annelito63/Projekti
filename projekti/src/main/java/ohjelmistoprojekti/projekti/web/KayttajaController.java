package ohjelmistoprojekti.projekti.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import ohjelmistoprojekti.projekti.model.Kayttaja;
import ohjelmistoprojekti.projekti.model.KayttajaRepository;
import ohjelmistoprojekti.projekti.model.MyyntitapahtumaRepository;
import ohjelmistoprojekti.projekti.model.TarkastusRepository;
import ohjelmistoprojekti.projekti.web.dto.KayttajaDto;

@RestController
public class KayttajaController {

    private final KayttajaRepository kayttajaRepository;
    private final MyyntitapahtumaRepository myyntitapahtumaRepository;
    private final TarkastusRepository tarkastusRepository;

    public KayttajaController(KayttajaRepository kayttajaRepository,
            MyyntitapahtumaRepository myyntitapahtumaRepository,
            TarkastusRepository tarkastusRepository) {
        this.kayttajaRepository = kayttajaRepository;
        this.myyntitapahtumaRepository = myyntitapahtumaRepository;
        this.tarkastusRepository = tarkastusRepository;
    }

    @GetMapping("/kayttajat")
    public List<KayttajaDto> findAllKayttajat() {
        List<KayttajaDto> tulos = new ArrayList<>();
        for (Kayttaja kayttaja : kayttajaRepository.findAll()) {
            tulos.add(muunna(kayttaja));
        }
        return tulos;
    }

    @GetMapping("/kayttajat/{id}")
    public ResponseEntity<KayttajaDto> findKayttaja(@PathVariable Long id) {
        return kayttajaRepository.findById(id)
                .map(kayttaja -> ResponseEntity.ok(muunna(kayttaja)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/kayttajat")
    public ResponseEntity<?> addKayttaja(@Valid @RequestBody KayttajaDto pyynto) {
        if (kayttajaRepository.findBySahkopostiIgnoreCase(pyynto.getSahkoposti()).isPresent()) {
            return konflikti("Sähköposti on jo käytössä");
        }

        Kayttaja uusi = new Kayttaja();
        uusi.setNimi(pyynto.getNimi().trim());
        uusi.setSahkoposti(pyynto.getSahkoposti());
        uusi.setRooli(pyynto.getRooli());

        Kayttaja tallennettu = kayttajaRepository.save(uusi);
        return ResponseEntity.status(HttpStatus.CREATED).body(muunna(tallennettu));
    }

    @PutMapping("/kayttajat/{id}")
    public ResponseEntity<?> updateKayttaja(@PathVariable Long id, @Valid @RequestBody KayttajaDto pyynto) {
        Optional<Kayttaja> olemassa = kayttajaRepository.findById(id);
        if (olemassa.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Sama sähköposti saa jäädä omalle käyttäjälleen, mutta ei olla toisen käytössä
        Optional<Kayttaja> samaSahkoposti = kayttajaRepository.findBySahkopostiIgnoreCase(pyynto.getSahkoposti());
        if (samaSahkoposti.isPresent() && !samaSahkoposti.get().getUserId().equals(id)) {
            return konflikti("Sähköposti on jo käytössä");
        }

        Kayttaja kayttaja = olemassa.get();
        kayttaja.setNimi(pyynto.getNimi().trim());
        kayttaja.setSahkoposti(pyynto.getSahkoposti());
        kayttaja.setRooli(pyynto.getRooli());

        Kayttaja paivitetty = kayttajaRepository.save(kayttaja);
        return ResponseEntity.ok(muunna(paivitetty));
    }

    @DeleteMapping("/kayttajat/{id}")
    public ResponseEntity<?> deleteKayttaja(@PathVariable Long id) {
        Optional<Kayttaja> olemassa = kayttajaRepository.findById(id);
        if (olemassa.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Kayttaja-entiteetin suhteissa on cascade = ALL, joten poisto veisi mukanaan
        // käyttäjän myynnit ja tarkastukset. Estetään se, jotta historia ei katoa.
        Kayttaja kayttaja = olemassa.get();
        if (!myyntitapahtumaRepository.findByKayttaja(kayttaja).isEmpty()
                || !tarkastusRepository.findByTarkastaja(kayttaja).isEmpty()) {
            return konflikti("Käyttäjää ei voi poistaa, koska hänellä on myyntejä tai tarkastuksia");
        }

        kayttajaRepository.delete(kayttaja);
        return ResponseEntity.noContent().build();
    }

    private KayttajaDto muunna(Kayttaja kayttaja) {
        return new KayttajaDto(kayttaja.getUserId(), kayttaja.getNimi(), kayttaja.getSahkoposti(), kayttaja.getSalasana(),
                kayttaja.getRooli());
    }

    private ResponseEntity<Map<String, String>> konflikti(String viesti) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("virhe", viesti));
    }
}