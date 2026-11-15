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

## LippuControlletTest

Testiluokka luo ennen jokaista testiä testilipun, jolla on seuraavat arvot:

koodi: TEST123
kuvaus: Testilippu
hinta: 49.90
käytetty: false

Testit tarkistavat sekä controllerin HTTP-rajapinnan että tietokantaan tallentuvien muutosten oikeellisuuden.

Testattavat toiminnallisuudet:
1. Hakee lipun koodin perusteella HTTP-kutsulla.
2. Varmistaa, että lippu löytyy repositorysta oikealla koodilla.
3. Merkitsee lipun käytetyksi HTTP PATCH -pyynnöllä.
4. Tarkistaa, että käytetyksi merkitseminen tallentuu tietokantaan.
5. Tarkistaa onnistuneen päivityksen palauttaman JSON-vastauksen.

Testaa virhetilanteet:
1. lippua ei löydy (404 Not Found)
2. koodiparametri puuttuu (400 Bad Request)
3. tuntematon tunniste (404 Not Found)
4. virheellinen tunnisteen tyyppi (400 Bad Request)
5. virheellinen JSON-data (400 Bad Request)

Controller tarjoaa seuraavat testatut rajapinnat:

GET /ticket/ticket?koodi={koodi} hakee lipun koodin perusteella
PATCH /ticket/{id} päivittää lipun tietoja

LippuControllerTest käyttää MockMvc:tä suorien HTTP-kutsujen simulointiin ja tarkistaa:
HTTP-statuskoodit
JSON-vastauksen sisällön
tietokantaan tallentuneet muutokset LippuRepositoryn kautta

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
