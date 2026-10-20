package ohjelmistoprojekti.projekti.web;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import ohjelmistoprojekti.projekti.model.Kayttaja;
import ohjelmistoprojekti.projekti.model.KayttajaRepository;
import ohjelmistoprojekti.projekti.model.Lipputyyppi;
import ohjelmistoprojekti.projekti.model.LipputyyppiRepository;
import ohjelmistoprojekti.projekti.model.Myyntirivi;
import ohjelmistoprojekti.projekti.model.Myyntitapahtuma;
import ohjelmistoprojekti.projekti.model.MyyntitapahtumaRepository;
import ohjelmistoprojekti.projekti.model.Tapahtuma;
import ohjelmistoprojekti.projekti.model.TapahtumaRepository;
import ohjelmistoprojekti.projekti.web.dto.MyyntiriviLuontiDto;
import ohjelmistoprojekti.projekti.web.dto.MyyntitapahtumaLuontiDto;
import jakarta.validation.Valid;

@RestController
public class MyyntitapahtumaController {

    private final MyyntitapahtumaRepository myyntitapahtumaRepository;
    private final TapahtumaRepository tapahtumaRepository;
    private final KayttajaRepository kayttajaRepository;
    private final LipputyyppiRepository lipputyyppiRepository;

    public MyyntitapahtumaController(MyyntitapahtumaRepository myyntitapahtumaRepository,
            TapahtumaRepository tapahtumaRepository,
            KayttajaRepository kayttajaRepository,
            LipputyyppiRepository lipputyyppiRepository) {
        this.myyntitapahtumaRepository = myyntitapahtumaRepository;
        this.tapahtumaRepository = tapahtumaRepository;
        this.kayttajaRepository = kayttajaRepository;
        this.lipputyyppiRepository = lipputyyppiRepository;
    }

    @GetMapping("/myynnit")
    public Iterable<Myyntitapahtuma> findAllMyynnit(Authentication authentication) {
        if (hasAnyRole(authentication, "YLLAPITAJA", "MYYJA")) {
            return myyntitapahtumaRepository.findAll();
        }
        requireRole(authentication, "VERKKOKAUPAN_ASIAKAS");
        return myyntitapahtumaRepository.findByKayttaja(currentUser(authentication));
    }

    @GetMapping("/myynnit/{id}")
    public ResponseEntity<Myyntitapahtuma> findMyynti(@PathVariable Long id, Authentication authentication) {
        Optional<Myyntitapahtuma> myynti = myyntitapahtumaRepository.findById(id);
        if (myynti.isPresent() && !hasAnyRole(authentication, "YLLAPITAJA", "MYYJA")) {
            requireRole(authentication, "VERKKOKAUPAN_ASIAKAS");
            if (myynti.get().getKayttaja() == null
                    || !myynti.get().getKayttaja().getUserId().equals(currentUser(authentication).getUserId())) {
                return ResponseEntity.notFound().build();
            }
        }
        return myynti.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/myynnit")
    @PreAuthorize("hasAnyRole('YLLAPITAJA', 'MYYJA', 'VERKKOKAUPAN_ASIAKAS')")
    public ResponseEntity<?> addMyynti(
            @Valid @RequestBody MyyntitapahtumaLuontiDto pyynto,
            Authentication authentication) {

        if (pyynto.getTapahtumaId() == null) {
            return ResponseEntity.badRequest().body("tapahtumaId on pakollinen");
        }
        boolean asiakas = isRole(authentication, "VERKKOKAUPAN_ASIAKAS");
        if (!asiakas && pyynto.getKayttajaId() == null) {
            return ResponseEntity.badRequest().body("kayttajaId on pakollinen");
        }
        if (pyynto.getRivit() == null || pyynto.getRivit().isEmpty()) {
            return ResponseEntity.badRequest().body("Myynnissä on oltava vähintään yksi rivi");
        }

        Optional<Tapahtuma> tapahtumaOpt = tapahtumaRepository.findById(pyynto.getTapahtumaId());
        if (tapahtumaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Tapahtumaa id:llä " + pyynto.getTapahtumaId() + " ei löytynyt");
        }

        Kayttaja myynninKayttaja;
        if (asiakas) {
            myynninKayttaja = currentUser(authentication);
        } else {
            Optional<Kayttaja> kayttajaOpt = kayttajaRepository.findById(pyynto.getKayttajaId());
            if (kayttajaOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Käyttäjää id:llä " + pyynto.getKayttajaId() + " ei löytynyt");
            }
            myynninKayttaja = kayttajaOpt.get();
        }

        for (MyyntiriviLuontiDto rivi : pyynto.getRivit()) {
            if (rivi.getLipputyyppiId() == null) {
                return ResponseEntity.badRequest().body("Jokaisella rivillä on oltava lipputyyppiId");
            }
            if (rivi.getMaara() <= 0) {
                return ResponseEntity.badRequest().body("Rivin määrän on oltava suurempi kuin 0");
            }
            if (lipputyyppiRepository.findById(rivi.getLipputyyppiId()).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Lipputyyppiä id:llä " + rivi.getLipputyyppiId() + " ei löytynyt");
            }
        }

        Myyntitapahtuma myynti = new Myyntitapahtuma();
        myynti.setTapahtuma(tapahtumaOpt.get());
        myynti.setKayttaja(myynninKayttaja);
        myynti.setMyyntiaika(LocalDateTime.now());

        List<Myyntirivi> rivit = new ArrayList<>();
        double kokonaissumma = 0.0;

        for (MyyntiriviLuontiDto riviDto : pyynto.getRivit()) {
            Lipputyyppi lipputyyppi = lipputyyppiRepository.findById(riviDto.getLipputyyppiId()).get();

            double yksikkohinta = lipputyyppi.getHinta();
            double rivisumma = yksikkohinta * riviDto.getMaara();
            kokonaissumma += rivisumma;

            Myyntirivi rivi = new Myyntirivi();
            rivi.setMyynti(myynti);
            rivi.setLipputyyppi(lipputyyppi);
            rivi.setMaara(riviDto.getMaara());
            rivi.setYksikkohinta(yksikkohinta);
            rivi.setRivisumma(rivisumma);

            rivit.add(rivi);
        }

        myynti.setMyyntirivit(rivit);
        myynti.setKokonaissumma(kokonaissumma);

        Myyntitapahtuma tallennettu = myyntitapahtumaRepository.save(myynti);

        return ResponseEntity.status(HttpStatus.CREATED).body(tallennettu);
    }

    private Kayttaja currentUser(Authentication authentication) {
        return kayttajaRepository.findBySahkopostiIgnoreCase(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

    private void requireRole(Authentication authentication, String role) {
        if (!isRole(authentication, role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    private boolean hasAnyRole(Authentication authentication, String... roles) {
        for (String role : roles) {
            if (isRole(authentication, role)) {
                return true;
            }
        }
        return false;
    }

    private boolean isRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
    }
}