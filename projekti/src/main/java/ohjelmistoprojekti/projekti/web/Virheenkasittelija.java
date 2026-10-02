package ohjelmistoprojekti.projekti.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Yhtenäistää virheiden statuskoodit ja -viestit kaikille REST-controllereille.
 * Ilman tätä esim. rikkinäinen JSON tai väärän tyyppinen polkuparametri
 * päätyisi Springin oletus-"whitelabel error"-sivulle.
 */
@RestControllerAdvice
public class Virheenkasittelija {

    // Rikkinäinen tai puuttuva JSON pyynnön bodyssa
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> kasitteleRikkinainenJson(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest()
                .body(virhe("Pyynnön sisältö on virheellinen JSON tai puuttuu kokonaan"));
    }

    // Vaarantyyppinen polkuparametri, esim. /tapahtumat/abc kun odotetaan Longia
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> kasitteleVaaraTyyppi(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.badRequest()
                .body(virhe("Polkuparametri '" + ex.getName() + "' on väärää tyyppiä"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> kasitteleValidointivirhe(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest()
                .body(virhe("Pyynnön pakolliset kentät puuttuvat tai sisältävät virheellisiä arvoja"));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, String>> kasitteleVaaranHttpMetodin(HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(virhe("HTTP-metodia ei tueta tällä resurssipolulla"));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Map<String, String>> kasitteleVaaranSisaltotyypin(HttpMediaTypeNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(virhe("Pyynnön Content-Type ei ole tuettu; käytä application/json"));
    }

    // Kaikki muut odottamattomat virheet -> selkeä 500, ei stack tracea asiakkaalle
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> kasitteleMuutVirheet(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(virhe("Palvelimella tapahtui odottamaton virhe"));
    }

    private Map<String, String> virhe(String viesti) {
        Map<String, String> body = new HashMap<>();
        body.put("virhe", viesti);
        return body;
    }
}