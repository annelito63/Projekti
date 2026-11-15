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

## LippuControllerTest

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

## LipputyyppiControllerTest

Ennen jokaista testiä luodaan:

tapahtuma nimeltä Testikonsertti
tapahtumaan liittyvä lipputyyppi Peruslippu
lipputyypin hinnaksi asetetaan 25.0

Testit tarkistavat lipputyyppien hakemisen sekä uusien lipputyyppien luomisen käyttöoikeuksien mukaisesti.

Testattavat toiminnallisuudet:
1. Hakee tapahtuman lipputyypit julkisella GET-kutsulla.
2. Tarkistaa, että palautettu lipputyyppi sisältää oikeat tiedot.
3. Tarkistaa, että tuntemattoman tapahtuman lipputyyppien haku palauttaa virheen.
4. Varmistaa, että ylläpitäjä voi luoda uuden lipputyypin tapahtumalle.
5. Tarkistaa, että luotu lipputyyppi tallentuu ja näkyy myöhemmissä hauissa.
6. Varmistaa, ettei tavallinen asiakas voi luoda uusia lipputyyppejä.

Controller tarjoaa seuraavat testatut rajapinnat:

GET /tapahtumat/{tapahtumaId}/lipputyypit hakee tapahtumaan liittyvät lipputyypit
POST /tapahtumat/{tapahtumaId}/lipputyypit luo uuden lipputyypin tapahtumalle
Käyttöoikeustestaus

Testiluokka tarkistaa Spring Securityn käyttöoikeusrajoitukset:

1. käyttäjä roolilla YLLAPITAJA voi luoda uusia lipputyyppejä
2. käyttäjä roolilla VERKKOKAUPAN_ASIAKAS ei voi luoda uusia lipputyyppejä ja saa vastauksena 403 Forbidden

LipputyyppiControllerTest käyttää suoria HTTP-kutsuja MockMvc:n avulla ja tarkistaa:
HTTP-statuskoodit
JSON-vastausten sisällön
tapahtumakohtaisen lipputyyppien listauksen
uusien lipputyyppien luomisen
käyttöoikeuksien toiminnan eri käyttäjärooleilla

## AuthControllerTest

Ennen jokaista testiä luodaan:

kaksi verkkokaupan asiakasta
yksi ylläpitäjä
testitapahtuma
tapahtumaan liittyvä lipputyyppi
kaksi myyntitapahtumaa, yksi kummallekin asiakkaalle

Testit varmistavat, että käyttäjät voivat käyttää vain heille sallittuja toimintoja ja nähdä vain ne tiedot, joihin heillä on oikeus.

Testattavat toiminnallisuudet
1. Tarkistaa, että kirjautumaton käyttäjä ei pääse tarkastelemaan myyntitietoja.
2. Varmistaa, että asiakas näkee vain omat myyntinsä.
3. Estää asiakasta hakemasta toisen asiakkaan myyntitietoja.
4. Tarkistaa, että uuden myynnin omistajaksi asetetaan aina kirjautunut käyttäjä, vaikka käyttäjä yrittäisi lähettää toisen käyttäjän tunnisteen.
5. Varmistaa, että asiakas voi selata tapahtumia mutta ei voi luoda uusia tapahtumia.
6. Tarkistaa, että ylläpitäjä voi nähdä kaikkien käyttäjien myynnit.
7. Estää asiakasta luomasta henkilöstö- tai ylläpitäjätunnuksia.
8. Tarkistaa, että rekisteröinti luo aina verkkokaupan asiakkaan.
9. Varmistaa, ettei rekisteröinnin vastauksessa palauteta salasanatiivistettä.
10. Tarkistaa, että liian pitkä BCryptin rajoitukset ylittävä salasana hylätään.
11. Estää käyttäjää korottamasta omaa rooliaan rekisteröitymisen yhteydessä.
12. Tarkistaa, ettei samaa sähköpostiosoitetta voida rekisteröidä kahdesti eri kirjainkoosta huolimatta.

Testeissä käytetään seuraavia rajapintoja:

Myynnit
GET /myynnit hakee käyttäjän myynnit
GET /myynnit/{id} hakee yksittäisen myyntitapahtuman
POST /myynnit luo uuden myyntitapahtuman
Tapahtumat
GET /tapahtumat hakee tapahtumat
POST /tapahtumat luo uuden tapahtuman
Käyttäjähallinta
POST /auth/register rekisteröi uuden käyttäjän
POST /auth/users luo uuden käyttäjän järjestelmään
Käyttöoikeustestaus

Testiluokka tarkistaa Spring Securityn käyttöoikeudet useissa tilanteissa:

1. kirjautumaton käyttäjä saa vastauksen 401 Unauthorized
2. verkkokaupan asiakas voi tarkastella vain omia tietojaan
3. verkkokaupan asiakas ei voi tehdä ylläpitäjän oikeuksia vaativia toimintoja
4. ylläpitäjä voi tarkastella kaikkien käyttäjien myyntitietoja
5. luvattomat toiminnot palauttavat vastauksen 403 Forbidden

Testeissä varmistetaan useita tietoturvaominaisuuksia:

1. käyttäjä ei voi nähdä toisen asiakkaan myyntitietoja
2. myynnin omistajuutta ei voi väärentää pyynnön mukana lähetettävällä käyttäjätunnisteella
3. salasanatiivisteitä ei palauteta API-vastauksissa
4. käyttäjä ei voi rekisteröityä ylläpitäjäksi tai henkilöstökäyttäjäksi
5. sähköpostiosoitteiden yksilöllisyys tarkistetaan kirjainkoosta riippumatta
6. BCrypt-algoritmin turvallinen salasanapituusraja huomioidaan validoinnissa

AuthControllerTest käyttää MockMvc:tä suorien HTTP-kutsujen simulointiin ja testaa koko sovellusketjua autentikoinnista tietokantaan asti. Testit varmistavat:

1. käyttäjien tunnistautumisen
2. roolipohjaiset käyttöoikeudet
3. tietojen näkyvyyden rajaukset
4. uusien käyttäjien rekisteröinnin
5. myyntien omistajuuden suojaamisen
6. salasanojen ja käyttäjätietojen turvallisen käsittelyn

Testiluokka toimii samalla sekä autentikointi-, käyttöoikeus- että tietoturvatestinä varmistaen, että järjestelmän liiketoimintasäännöt toteutuvat myös HTTP-rajapinnan kautta.

## MyyntitapahtumaControllerTest

Ennen jokaista testiä luodaan:

testitapahtuma, jonka kapasiteetti on 10 osallistujaa
tapahtumaan liittyvä lipputyyppi
myyjäroolilla varustettu käyttäjä
tarvittavat tunnisteet myöhempiä testejä varten

Testit suoritetaan autentikoituna käyttäjänä, jolla on MYYJA-rooli.

Testattavat toiminnallisuudet:
1. Tarkistaa, että tuntemattoman myyntitapahtuman haku palauttaa virheen.
2. Tarkistaa, että virheellinen tunnisteen tyyppi palauttaa virheen.
3. Varmistaa, ettei myyntitapahtumaa voi päivittää tukemattomalla HTTP-metodilla.
4. Tarkistaa virheellisen JSON-sisällön käsittelyn.
5. Tarkistaa pakollisten kenttien validoinnin:
- tapahtuma puuttuu
- käyttäjä puuttuu
- myyntirivit puuttuvat
- myyntirivilista on tyhjä
6. Tarkistaa, että tuntemattomat tapahtumat, käyttäjät ja lipputyypit käsitellään oikein.
7. Tarkistaa myyntirivien validoinnin:
- lipputyyppi puuttuu
- lippujen määrä on nolla
8. Luo onnistuneen myynnin ja varmistaa, että yksilölliset liput muodostetaan.
9. Tarkistaa, että tapahtuman kapasiteettia ei voi ylittää.
10. Varmistaa, ettei toisen tapahtuman lipputyyppiä voi käyttää myyntitapahtumassa.

Controller tarjoaa seuraavat testatut rajapinnat:

GET /myynnit/{id} hakee yksittäisen myyntitapahtuman
POST /myynnit luo uuden myyntitapahtuman

Testeissä varmistetaan lisäksi, että:

PUT /myynnit/{id} ei ole sallittu ja palauttaa virheen

Testien kattavuus

MyyntitapahtumaControllerTest käyttää MockMvc:tä HTTP-pyyntöjen simulointiin ja tarkistaa:

HTTP-statuskoodit
JSON-vastausten sisällön
syötevalidoinnit
liiketoimintasääntöjen toteutumisen
tapahtuman kapasiteettirajoitukset
lippujen automaattisen luomisen
tietokantaan tallentuneet muutokset

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

## TapahtumaHttpTest

Ennen jokaista testiä luodaan testitapahtuma, jonka tietoja käytetään hakujen, päivitysten ja poistojen testaamiseen.

Toisin kuin TapahtumaControllerTest, joka keskittyy pääasiassa virhetilanteisiin ja tietokantatoiminnallisuuksiin, TapahtumaHttpTest testaa sovelluksen HTTP-rajapintaa käyttäjän näkökulmasta.

Testattavat toiminnallisuudet:
1. Varmistaa, että tapahtumalistaus on julkisesti saatavilla ilman kirjautumista.
2. Varmistaa, että yksittäisen tapahtuman tiedot voidaan hakea ilman kirjautumista.
3. Tarkistaa, ettei kirjautumaton käyttäjä voi luoda, muokata tai poistaa tapahtumia.
4. Tarkistaa, ettei myyjäroolin käyttäjä voi luoda, muokata tai poistaa tapahtumia.
5. Varmistaa, että ylläpitäjä voi luoda uuden tapahtuman.
6. Varmistaa, että ylläpitäjä voi päivittää olemassa olevan tapahtuman tiedot.
7. Varmistaa, että ylläpitäjä voi poistaa tapahtuman.
8. Tarkistaa, että tietokanta päivittyy oikein luonti-, muokkaus- ja poistotoimenpiteiden yhteydessä.

Controller tarjoaa seuraavat testatut rajapinnat:

GET /tapahtumat hakee kaikki tapahtumat
GET /tapahtumat/{id} hakee yksittäisen tapahtuman
POST /tapahtumat luo uuden tapahtuman
PUT /tapahtumat/{id} päivittää tapahtuman
DELETE /tapahtumat/{id} poistaa tapahtuman
Käyttöoikeustestaus

TapahtumaHttpTest käyttää MockMvc:tä HTTP-pyyntöjen simulointiin ja tarkistaa:

HTTP-statuskoodit
JSON-vastausten sisällön
käyttöoikeuksien toiminnan
julkisten rajapintojen saavutettavuuden
tietokantaan tallentuneet muutokset

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
