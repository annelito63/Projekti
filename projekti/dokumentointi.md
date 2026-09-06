# Dokumentointi

## Projektin lähtötilanne

Projekti on Spring Boot -sovellus, josta rakennetaan lipunmyyntijärjestelmä. Ensimmäiseksi toteutettiin toiminto, jolla ylläpitäjä voi luoda uuden tapahtuman. Tapahtumalle voidaan myöhemmin liittää lipputyyppejä ja niiden hintoja.


## Toteutettu toiminnallisuus 06.09.2026

### Tapahtuman tiedot

Tapahtumasta tallennetaan seuraavat tiedot:

- yksilöllinen tunniste
- nimi
- kaupunki
- paikka
- kuvaus
- tapahtuman kapasiteetti eli myytävien lippujen enimmäismäärä

Tapahtuman malli on toteutettu `Event`-recordina. Record sopii tähän, koska tapahtuman tiedot palautetaan sellaisena kokonaisuutena, eikä sen tietoja muuteta luomisen jälkeen.

### Tapahtuman luominen

Uusi tapahtuma luodaan HTTP POST -pyynnöllä osoitteeseen:

```text
POST /api/events
```

Pyynnön mukana lähetetään JSON-muotoinen tapahtuma:

```json
{
  "name": "Kesäkonsertti",
  "city": "Helsinki",
  "venue": "Kaisaniemi",
  "description": "Kesäinen ulkoilmakonsertti",
  "capacity": 500
}
```

Kun tapahtuma luodaan onnistuneesti, palvelin palauttaa tapahtuman ja HTTP-statuksen `201 Created`. Tapahtuma saa automaattisesti kasvavan tunnistenumeron.

### Tapahtumien listaaminen

Kaikki luodut tapahtumat saa haettua HTTP GET -pyynnöllä:

```text
GET /api/events
```

Tapahtumat palautetaan listana siinä järjestyksessä, jossa ne on luotu.

### Syötteiden tarkistaminen

Tapahtuman nimi on pakollinen. Lisäksi kapasiteetin pitää olla suurempi kuin nolla. Jos tiedot eivät täytä näitä ehtoja, palvelin palauttaa HTTP-statuksen `400 Bad Request` ja virheilmoituksen.

## Koodin rakenne

Tapahtumiin liittyvät Java-luokat ovat kansiossa:

```text
/projekti/event/
```

Luokkien tehtävät ovat seuraavat:

- `Event.java` sisältää tapahtuman tiedot.
- `CreateEventRequest.java` sisältää uuden tapahtuman mukana tulevat tiedot.
- `EventService.java` sisältää tapahtuman luomisen, tarkistamisen ja listaamisen logiikan.
- `EventController.java` vastaanottaa HTTP-pyynnöt ja palauttaa vastaukset.

Controllerin ja servicen erottaminen tekee koodista selkeämmän. Controller huolehtii rajapinnasta ja service huolehtii varsinaisesta toiminnasta.

## Tietojen tallennus

Tässä ensimmäisessä versiossa tapahtumat tallennetaan sovelluksen muistiin. Tapahtumien tunnisteet luodaan `AtomicLong`-laskurilla ja tapahtumat säilytetään muistissa olevassa map-rakenteessa.

Tämä ratkaisu sopii ensimmäiseen kokeiluversioon, koska tietokantaa ei ole vielä valittu. Tiedot kuitenkin katoavat, kun sovellus sammutetaan tai käynnistetään uudelleen. Myöhemmin tapahtumat pitää tallentaa tietokantaan.

## Testaaminen

Testit ovat tiedostossa:

```text
/projekti/ProjektiApplicationTests.java
```

Testejä on kaksi:

1. `contextLoads` tarkistaa, että Spring Boot -sovellus käynnistyy oikein.
2. `createsAndListsEvent` tarkistaa tapahtuman luomisen ja listaamisen.

Toinen testi käyttää Springin `MockMvc`-työkalua. Sen avulla voidaan testata HTTP-rajapintaa ilman erillistä selainta tai oikeaa palvelinta.

Testissä tehdään seuraavat asiat:

1. Lähetetään POST-pyyntö uuden tapahtuman luomiseksi.
2. Tarkistetaan, että vastaus on `201 Created`.
3. Tarkistetaan, että vastauksessa on oikea nimi ja kapasiteetti.
4. Lähetetään GET-pyyntö tapahtumien hakemiseksi.
5. Tarkistetaan, että luotu tapahtuma löytyy listasta.

Testit ajetaan projektikansion juuresta komennolla:

```powershell
.\mvnw.cmd clean test
```

Testauksen tulos tässä vaiheessa:

```text
Tests run: 2, Failures: 0, Errors: 0
BUILD SUCCESS
```

## Sovelluksen käynnistäminen

Sovellus käynnistetään projektikansion juuresta komennolla:

```powershell
.\mvnw.cmd spring-boot:run
```

Kun sovellus on käynnissä, tapahtumia voidaan luoda esimerkiksi PowerShellillä:

```powershell
Invoke-RestMethod -Method Post `
  -Uri http://localhost:8080/api/events `
  -ContentType "application/json" `
  -Body '{"name":"Kesäkonsertti","city":"Helsinki","venue":"Kaisaniemi","description":"Ulkoilmakonsertti","capacity":500}'
```

Tapahtumien lista haetaan näin:

```powershell
Invoke-RestMethod http://localhost:8080/api/events
```
#############################################################
