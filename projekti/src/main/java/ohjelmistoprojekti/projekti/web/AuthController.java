package ohjelmistoprojekti.projekti.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import ohjelmistoprojekti.projekti.model.Kayttaja;
import ohjelmistoprojekti.projekti.model.KayttajaRepository;
import ohjelmistoprojekti.projekti.model.Rooli;
import ohjelmistoprojekti.projekti.web.dto.KayttajanLuontiPyynto;
import ohjelmistoprojekti.projekti.web.dto.KayttajanVastaus;
import ohjelmistoprojekti.projekti.web.dto.RekisterointiPyynto;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final KayttajaRepository kayttajaRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(KayttajaRepository kayttajaRepository, PasswordEncoder passwordEncoder) {
        this.kayttajaRepository = kayttajaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<KayttajanVastaus> register(@Valid @RequestBody RekisterointiPyynto pyynto) {
        if (kayttajaRepository.existsBySahkopostiIgnoreCase(pyynto.sahkoposti())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sähköpostiosoite on jo käytössä");
        }
        Kayttaja kayttaja = new Kayttaja();
        kayttaja.setNimi(pyynto.nimi());
        kayttaja.setSahkoposti(pyynto.sahkoposti());
        kayttaja.setSalasanaHash(passwordEncoder.encode(pyynto.salasana()));
        kayttaja.setRooli(Rooli.VERKKOKAUPAN_ASIAKAS);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(KayttajanVastaus.from(kayttajaRepository.save(kayttaja)));
    }

    @GetMapping("/me")
    public KayttajanVastaus currentUser(Authentication authentication) {
        return KayttajanVastaus.from(findUser(authentication.getName()));
    }

    @PostMapping("/users")
    @PreAuthorize("hasRole('YLLAPITAJA')")
    public ResponseEntity<KayttajanVastaus> createUser(@Valid @RequestBody KayttajanLuontiPyynto pyynto) {
        if (kayttajaRepository.existsBySahkopostiIgnoreCase(pyynto.sahkoposti())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sähköpostiosoite on jo käytössä");
        }
        Kayttaja kayttaja = new Kayttaja();
        kayttaja.setNimi(pyynto.nimi());
        kayttaja.setSahkoposti(pyynto.sahkoposti());
        kayttaja.setSalasanaHash(passwordEncoder.encode(pyynto.salasana()));
        kayttaja.setRooli(pyynto.rooli());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(KayttajanVastaus.from(kayttajaRepository.save(kayttaja)));
    }

    private Kayttaja findUser(String email) {
        return kayttajaRepository.findBySahkopostiIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }
}
