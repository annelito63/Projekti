# Autentikointi ja käyttöoikeudet

Tämä ohje kuvaa REST-rajapinnan kirjautumisen, roolit, käyttäjäkohtaisen tietojen rajauksen ja toiminnan testaamisen. Esimerkit ovat PowerShellille ja `curl.exe`:lle. Palvelimen oletusosoite on `http://localhost:8080`.

## Miten kirjautuminen toimii

Palvelu käyttää tilatonta HTTP Basic -autentikointia:

- Jokainen suojattu pyyntö autentikoidaan uudelleen `Authorization: Basic ...` -otsakkeesta.
- Tunnuksena käytetään sähköpostiosoitetta ja salasana lähetetään pyynnön tunnistetiedoissa.
- Palvelin hakee käyttäjän sähköpostilla (kirjainkoosta riippumatta) ja tarkistaa salasanan BCrypt-tiivisteestä.
- Palvelin ei luo istuntoa eikä palauta kirjautumistokenia. Sovellusasiakas säilyttää tunnuksensa ja lähettää ne suojattuihin pyyntöihin.
- Tuotannossa yhteyttä on käytettävä HTTPS:n kautta: Base64-koodaus ei salaa Basic-tunnistetietoja.

Rekisteröitymisen salasanan on oltava 8–72 merkkiä ja enintään 72 tavua UTF-8-muodossa. Salasanaa ei tallenneta selväkielisenä eikä palauteta API-vastauksissa. Sähköposti normalisoidaan tallennettaessa pienaakkosiksi.

## Käyttäjätilin luominen

### Asiakas rekisteröityy itse

`POST /auth/register` on julkinen. Rekisteröinti luo aina `VERKKOKAUPAN_ASIAKAS`-roolin käyttäjän. Pyynnön `rooli`-kenttää ei hyväksytä rekisteröintitietona, joten sillä ei voi korottaa oikeuksia.

```powershell
$baseUrl = 'http://localhost:8080'
curl.exe -i -X POST "$baseUrl/auth/register" `
  -H 'Content-Type: application/json' `
  --data-raw '{"nimi":"Aino Asiakas","sahkoposti":"aino@example.fi","salasana":"EsimerkkiSalasana123"}'
```

Onnistunut vastaus on `201 Created` ja sisältää käyttäjän julkiset tiedot, esimerkiksi:

```json
{
  "userId": 1,
  "nimi": "Aino Asiakas",
  "sahkoposti": "aino@example.fi",
  "rooli": "VERKKOKAUPAN_ASIAKAS"
}
```

Salasanatiivistettä tai salasanaa ei sisällytetä vastaukseen. Saman sähköpostiosoitteen uudelleenrekisteröinti palauttaa `409 Conflict`; puutteelliset tai virheelliset tiedot palauttavat `400 Bad Request`.

### Ensimmäinen ylläpitäjä

Avoin rekisteröinti ei voi luoda henkilöstö- tai ylläpitäjätunnusta. Ensimmäinen ylläpitäjä alustetaan käynnistyksessä ympäristömuuttujilla:

```powershell
$env:BOOTSTRAP_ADMIN_EMAIL = 'yllapitaja@example.fi'
$env:BOOTSTRAP_ADMIN_PASSWORD = '<aseta paikallinen vahva salasana>'
$env:BOOTSTRAP_ADMIN_NAME = 'Ylläpitäjä'
.\mvnw.cmd spring-boot:run
```

Komento suoritetaan projektihakemistossa, jossa `pom.xml` ja `mvnw.cmd` sijaitsevat. `BOOTSTRAP_ADMIN_NAME` on valinnainen. Sähköposti ja salasana on asetettava yhdessä. Jos samalla sähköpostilla löytyy jo muu kuin `YLLAPITAJA`-käyttäjä, sovellus keskeyttää käynnistyksen eikä muuta käyttäjän roolia automaattisesti. Tunnuksia ei pidä tallentaa lähdekoodiin tai versionhallintaan.

Jätä palvelin käyntiin ja tee alla olevat API-pyynnöt toisessa PowerShell-ikkunassa.

Ylläpitäjä luo henkilöstötunnukset reitillä `POST /auth/users`. Reitti vaatii `YLLAPITAJA`-roolin:

```powershell
$baseUrl = 'http://localhost:8080'
$adminEmail = 'yllapitaja@example.fi'
$adminPassword = '<sama paikallinen salasana kuin käynnistyksessä>'
$adminCredentials = "${adminEmail}:$adminPassword"

curl.exe -i -X POST "$baseUrl/auth/users" `
  -u $adminCredentials `
  -H 'Content-Type: application/json' `
  --data-raw '{"nimi":"Myyjä","sahkoposti":"myyja@example.fi","salasana":"ToinenVahvaSalasana123","rooli":"MYYJA"}'
```

`rooli` on jokin seuraavista täsmälleen kirjoitetuista arvoista:

| Rooli | Oikeudet toteutetuissa rajapinnoissa |
|---|---|
| `VERKKOKAUPAN_ASIAKAS` | Voi rekisteröityä, luoda myynnin itselleen sekä hakea vain omat myyntinsä. Voi selata julkisia tapahtumia. |
| `MYYJA` | Voi luoda myyntejä, tarkastella kaikkia myyntejä ja asiakkaita koskevia myyntitietoja. |
| `YLLAPITAJA` | Voi tarkastella kaikkia myyntejä, luoda myyntejä, hallita tapahtumia sekä luoda käyttäjiä ja rooleja. Tämä rooli toimii nykyisessä tietomallissa myös tapahtuman järjestäjänä. |
| `OVELLA_TARKASTAVA_HENKILO` | Kirjautumistunnus voidaan luoda, mutta ovella tehtävän lipuntarkastuksen erillisiä rajapintoja ei ole vielä toteutettu. |

Tapahtumia ei vielä omisteta yksittäiselle järjestäjälle. Sen vuoksi tapahtumien hallintaoikeus on tässä toteutuksessa rajattu ylläpitäjärooliin, eikä useiden järjestäjätilien tapahtumia voi erotella toisistaan.

## Reitit ja pääsyoikeudet

| Pyyntö | Anonyymi | Asiakas | Myyjä | Ylläpitäjä |
|---|---:|---:|---:|---:|
| `POST /auth/register` | Sallittu | Sallittu | Sallittu | Sallittu |
| `GET /auth/me` | 401 | Oma profiili | Oma profiili | Oma profiili |
| `POST /auth/users` | 401 | 403 | 403 | Sallittu |
| `GET /tapahtumat` ja `GET /tapahtumat/{id}` | Julkinen | Julkinen | Julkinen | Julkinen |
| `POST /tapahtumat` | 401 | 403 | 403 | Sallittu |
| `PUT /tapahtumat/{id}` ja `DELETE /tapahtumat/{id}` | 401 | 403 | 403 | Sallittu |
| `GET /myynnit` | 401 | Vain omat | Kaikki | Kaikki |
| `GET /myynnit/{id}` | 401 | Oma myynti | Kaikki | Kaikki |
| `POST /myynnit` | 401 | Luo omalle tilille | Sallittu | Sallittu |

Asiakkaan `/myynnit`-listaus haetaan kirjautuneen käyttäjän perusteella. Yksittäisen toisen asiakkaan myynnin haku palauttaa `404 Not Found`, jotta toisen tilauksen olemassaoloa ei paljasteta. Asiakkaan `POST /myynnit` -pyynnössä annettu `kayttajaId` ohitetaan: myynti liitetään aina kirjautuneeseen asiakkaaseen. Myyjän ja ylläpitäjän myyntipyynnössä `kayttajaId` on pakollinen.

## Rajapintojen testaaminen curlilla

### 1. Rekisteröidy ja tarkista kirjautuminen

```powershell
$baseUrl = 'http://localhost:8080'
$customerEmail = 'aino@example.fi'
$customerPassword = 'EsimerkkiSalasana123'

curl.exe -i -X POST "$baseUrl/auth/register" `
  -H 'Content-Type: application/json' `
  --data-raw '{"nimi":"Aino Asiakas","sahkoposti":"aino@example.fi","salasana":"EsimerkkiSalasana123"}'

curl.exe -i -u "${customerEmail}:$customerPassword" "$baseUrl/auth/me"
```

Odotettu tulos on ensin `201 Created`, sitten `200 OK` ja sama käyttäjäprofiili. `GET /auth/me` ilman tunnuksia palauttaa `401 Unauthorized`.

### 2. Vertaile julkista ja suojattua reittiä

Tapahtumien selaaminen on julkista:

```powershell
curl.exe -i "$baseUrl/tapahtumat"
```

Myyntien haku vaatii kirjautumisen:

```powershell
curl.exe -i "$baseUrl/myynnit"
curl.exe -i -u "${customerEmail}:$customerPassword" "$baseUrl/myynnit"
```

Ensimmäisen pyynnön odotettu tulos on `401 Unauthorized`; asiakkaan pyyntö palauttaa `200 OK` ja vain hänen omat myyntinsä.

### 3. Luo myynti asiakkaana ja varmista, ettei omistajaa voi väärentää

Esimerkki edellyttää, että tietokannassa on jo olemassa oleva tapahtuma ja siihen liittyvä lipputyyppi. Asiakas ei lähetä `kayttajaId`-kenttää:

```powershell
$eventId = 1
$ticketTypeId = 1
curl.exe -i -X POST "$baseUrl/myynnit" `
  -u "${customerEmail}:$customerPassword" `
  -H 'Content-Type: application/json' `
  --data-raw "{`"tapahtumaId`":$eventId,`"rivit`":[{`"lipputyyppiId`":$ticketTypeId,`"maara`":1}]}"
```

Vastauksen odotettu tila on `201 Created`; palautetun myynnin `kayttaja.userId` on kirjautuneen asiakkaan tunnus. Palvelin käyttää samaa kirjautunutta käyttäjää myös silloin, jos pyynnössä yritetään asettaa toisen asiakkaan `kayttajaId`.

### 4. Varmista roolirajaukset

Asiakas ei voi luoda tapahtumaa eikä henkilöstökäyttäjää:

```powershell
curl.exe -i -X POST "$baseUrl/tapahtumat" `
  -u "${customerEmail}:$customerPassword" `
  -H 'Content-Type: application/json' `
  --data-raw '{"nimi":"Testitapahtuma","tyyppi":"Konsertti","aika":"2026-10-10T18:00","kaupunki":"Oulu","paikka":"Areena","maara":100}'

curl.exe -i -X POST "$baseUrl/auth/users" `
  -u "${customerEmail}:$customerPassword" `
  -H 'Content-Type: application/json' `
  --data-raw '{"nimi":"Ylläpitäjä","sahkoposti":"toinen-admin@example.fi","salasana":"VahvaSalasana123","rooli":"YLLAPITAJA"}'
```

Molempien vastaus on `403 Forbidden`. Samoilla reiteillä ilman tunnuksia vastaus on `401 Unauthorized`. Ylläpitäjän Basic-tunnuksilla `POST /tapahtumat`, `PUT /tapahtumat/{id}`, `DELETE /tapahtumat/{id}` ja `POST /auth/users` sallitaan.

### 5. Tarkista toisen käyttäjän tilauksen suojaus

Hanki ylläpitäjän tai myyjän `/myynnit`-listauksesta toisen asiakkaan `myyntiId`, ja kokeile sitä asiakkaan tunnuksilla:

```powershell
$otherSaleId = 123
curl.exe -i -u "${customerEmail}:$customerPassword" "$baseUrl/myynnit/$otherSaleId"
```

Jos tilaus kuuluu toiselle asiakkaalle, odotettu tulos on `404 Not Found`. Oman tilauksen tunnuksella asiakas saa `200 OK`.

## Automaattiset testit

Testit suoritetaan projektihakemistossa eli hakemistossa, jossa `pom.xml` ja `mvnw.cmd` sijaitsevat:

```powershell
.\mvnw.cmd "-Dtest=AuthControllerTest" test
```

`AuthControllerTest` tarkistaa mm. kirjautumattoman pyynnön, julkisen tapahtumaluettelon, rekisteröinnin oletusroolin ja salasanan piilotuksen, asiakkaan omiin myynteihin rajauksen, toisen käyttäjän myynnin 404-vastauksen, käyttäjä-ID:n väärentämisen, ylläpitäjän pääsyn sekä asiakkaan kielletyt kirjoitustoiminnot.

Koko testisarja:

```powershell
.\mvnw.cmd clean test
```

## HTTP-tilakoodit

| Tila | Merkitys |
|---|---|
| `200 OK` | Kirjautuneen käyttäjän sallittu haku tai pyyntö onnistui. |
| `201 Created` | Rekisteröinti, ylläpitäjän luoma käyttäjä tai myynti luotiin. |
| `400 Bad Request` | Pyyntö tai kenttien validointi epäonnistui. |
| `401 Unauthorized` | Tunnukset puuttuvat tai ovat virheelliset. |
| `403 Forbidden` | Tunnistetulla käyttäjällä ei ole reitin vaatimaa roolia. |
| `404 Not Found` | Kohdetta ei ole tai asiakkaalla ei ole oikeutta nähdä toisen käyttäjän myyntiä. |
| `409 Conflict` | Sähköpostiosoite on jo käytössä. |
