package ohjelmistoprojekti.projekti.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import ohjelmistoprojekti.projekti.model.Kayttaja;
import ohjelmistoprojekti.projekti.model.KayttajaRepository;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/myynnit", "/myynnit/*")
                                .hasAnyRole("MYYJA", "OVELLA_TARKASTAVA_HENKILO")
                        .requestMatchers(HttpMethod.POST, "/myynnit").hasRole("MYYJA")
                        .requestMatchers(HttpMethod.PUT, "/myynnit/*").hasRole("MYYJA")
                        .requestMatchers(HttpMethod.DELETE, "/myynnit/*").hasRole("MYYJA")
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

        @Bean
    public UserDetailsService userDetailsService(KayttajaRepository kayttajat) {
        return nimi -> {
            Kayttaja kayttaja = kayttajat.findByNimi(nimi)
                    .orElseThrow(() -> new UsernameNotFoundException(nimi));
            return User.withUsername(kayttaja.getNimi())
                    .password(kayttaja.getSalasana())
                    .roles(kayttaja.getRooli().name())
                    .build();
        };
    }

}
