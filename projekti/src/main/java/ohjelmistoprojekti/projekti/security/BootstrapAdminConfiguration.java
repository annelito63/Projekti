package ohjelmistoprojekti.projekti.security;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import ohjelmistoprojekti.projekti.model.Kayttaja;
import ohjelmistoprojekti.projekti.model.KayttajaRepository;
import ohjelmistoprojekti.projekti.model.Rooli;

@Configuration
public class BootstrapAdminConfiguration {

    @Bean
    CommandLineRunner createBootstrapAdmin(
            KayttajaRepository kayttajaRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.bootstrap-admin.email:}") String email,
            @Value("${app.bootstrap-admin.password:}") String password,
            @Value("${app.bootstrap-admin.name:}") String name) {
        return args -> {
            if (email.isBlank() && password.isBlank()) {
                return;
            }
            if (email.isBlank() || password.isBlank()) {
                throw new IllegalStateException(
                        "BOOTSTRAP_ADMIN_EMAIL ja BOOTSTRAP_ADMIN_PASSWORD on asetettava yhdessä");
            }
            if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
                throw new IllegalStateException(
                        "Bootstrap-ylläpitäjän salasanan enimmäispituus on 72 UTF-8-tavua");
            }

            var existing = kayttajaRepository.findBySahkopostiIgnoreCase(email);
            if (existing.isPresent()) {
                if (existing.get().getRooli() != Rooli.YLLAPITAJA) {
                    throw new IllegalStateException(
                            "Bootstrap-ylläpitäjän sähköpostiosoite kuuluu jo toiselle roolille");
                }
                return;
            }

            Kayttaja admin = new Kayttaja();
            admin.setNimi(name.isBlank() ? email : name);
            admin.setSahkoposti(email);
            admin.setSalasanaHash(passwordEncoder.encode(password));
            admin.setRooli(Rooli.YLLAPITAJA);
            kayttajaRepository.save(admin);
        };
    }
}
