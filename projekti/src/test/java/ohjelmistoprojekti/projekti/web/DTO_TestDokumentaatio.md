# DTO- ja tapahtumatestien dokumentaatio

## MyyntitapahtumaDtoTest

Varsinaiset DTO-testit ovat tiedostossa `MyyntitapahtumaDtoTest.java`.
Testiluokka tarkistaa, että web-kerroksen DTO:t säilyttävät niille annetut
tiedot oikein.

### MyyntiriviLuontiDto

Testit tarkistavat seuraavat asiat:

- oletuskonstruktori luo olion oletusarvoilla
- parametrikonstruktori asettaa `lipputyyppiId`- ja `maara`-kentät
- getterit palauttavat oikeat arvot
- setterit muuttavat kenttien arvot oikein
- oletusarvot ovat `lipputyyppiId = null` ja `maara = 0`

### MyyntitapahtumaLuontiDto

Testit tarkistavat seuraavat asiat:

- oletuskonstruktori luo olion oletusarvoilla
- parametrikonstruktori asettaa kaikki kentät
- `tapahtumaId` ja `kayttajaId` palautuvat oikein
- `rivit`-lista voidaan asettaa ja lukea
- myyntitapahtuman sisällä oleva `MyyntiriviLuontiDto` säilyy oikeana rivinä
- oletusarvot ovat `tapahtumaId = null`, `kayttajaId = null` ja `rivit = null`

Testi tulostaa terminaaliin jokaisen testin nimen, käytetyt arvot ja
onnistumisilmoituksen. Tulostus helpottaa testin toiminnan seuraamista, mutta
testin onnistuminen perustuu JUnitin `assert`-tarkistuksiin.

## TapahtumaControllerTest

`TapahtumaControllerTest.java` sijaitsee testipaketin ylemmällä tasolla.
Se käyttää Spring Boot -testiympäristöä ja `TapahtumaRepository`-rajapintaa.

Testi suorittaa seuraavan tapahtuman elinkaaren:

1. Luo uuden tapahtuman testiarvoilla.
2. Tallentaa tapahtuman tietokantaan.
3. Hakee tapahtuman tunnisteella ja tarkistaa sen nimen.
4. Päivittää nimen, kuvauksen ja paikkamäärän.
5. Tarkistaa päivitetyt arvot.
6. Poistaa tapahtuman.
7. Varmistaa, ettei tapahtumaa enää löydy tietokannasta.

Controller tarjoaa näille toiminnoille HTTP-rajapinnan:

- `GET /tapahtumat` hakee kaikki tapahtumat
- `POST /tapahtumat` luo uuden tapahtuman
- `GET /tapahtumat/{id}` hakee yhden tapahtuman
- `PUT /tapahtumat/{id}` päivittää tapahtuman
- `DELETE /tapahtumat/{id}` poistaa tapahtuman

Nykyinen `TapahtumaControllerTest` tarkistaa controllerin taustalla toimivan
tietokantakäsittelyn repositoryn kautta. Se ei vielä tee suoria HTTP-kutsuja
controllerille MockMvc:n avulla.

## Testien ajaminen

Pelkkä DTO-testi ja sen terminaalitulostukset:

```powershell
cd Projekti\projekti
.\mvnw.cmd '-Dtest=MyyntitapahtumaDtoTest' '-Dsurefire.useFile=false' test
```

Kaikki testit:

```powershell
cd Projekti\projekti
.\mvnw.cmd test
```

`-Dsurefire.useFile=false` näyttää `System.out`-tulostukset terminaalissa.
