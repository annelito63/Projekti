# Lippujen hallinta – REST API -dokumentaatio

Tämä dokumentti kuvaa toteutetut lippujen haku- ja käyttötilan päivitysrajapinnat.
Lipun luonti, muokkaus ja poisto eivät ole vielä toteutettuja rajapintoja.

- **Sovellus:** `ohjelmistoprojekti.projekti` (Spring Boot)
- **Kontrolleri:** `ohjelmistoprojekti.projekti.web.LippuController`
- **Tietokanta:** H2 (kehitys), PostgreSQL (tuotanto, Render)

**Toteutustilanne**

| Toiminto | Tila |
|---|---|
| Lipun haku lippukoodilla | Toteutettu |
| Lipun merkitseminen käytetyksi (PATCH) | Toteutettu |
| Lippujen luonti | Suunniteltu |
| Kaikkien lippujen / yksittäisen lipun haku id:llä | Suunniteltu |
| Lipun poisto | Suunniteltu |

---

## Sisällys

- [Base-URL](#base-url)
- [Yleiset periaatteet](#yleiset-periaatteet)
- [Tietomalli: Lippu](#tietomalli-lippu)
- [Endpointit](#endpointit)
  - [GET /ticket/ticket](#get-ticketticket)
  - [PATCH /ticket/{id}](#patch-ticketid)
- [Paluukoodit](#paluukoodit)
- [Tunnetut rajoitteet](#tunnetut-rajoitteet)

---

## Base-URL

| Ympäristö | Base-URL |
|---|---|
| Kehitys (paikallinen) | `http://localhost:8080` |
| Tuotanto (Render) | `https://lippupalvelu-uons.onrender.com` |

Kaikki alla olevat polut ovat suhteessa base-URLiin, esim.
`http://localhost:8080/ticket/1`.

> **Huom.** `LippuController`-luokalla on luokkatason polku `@RequestMapping("/ticket")`.
> Koodilla haettaessa polku on tällä hetkellä `/ticket/ticket` (luokan ja metodin
> polku peräkkäin). Polut on tarkoitus yhtenäistää muiden rajapintojen tapaan
> suomenkielisiksi (esim. `/liput`); tämä dokumentti päivitetään silloin.

---

## Yleiset periaatteet

| Asia | Kuvaus |
|---|---|
| Tietomuoto | JSON (`application/json`) |
| Merkistö | UTF-8 |
| Pyynnön `Content-Type` | `application/json` (PATCH) |
| Vastauksen `Content-Type` | `application/json` |
| Id:n generointi | Tietokanta generoi `id`-arvon (`GenerationType.AUTO`) |
| Autentikointi | HTTP Basic vaaditaan kaikille lippureiteille. Roolikohtaista rajausta ei ole vielä toteutettu: kuka tahansa kirjautunut käyttäjä voi kutsua reittejä. |
| CORS | Sallittu kaikista lähteistä (`@CrossOrigin(origins = "*")`) |

Kirjautumisen ja käyttöoikeuksien ohjeet ovat [autentikoinnin ohjeessa](./Autentikointi.md).

---

## Tietomalli: Lippu

Vastaa luokkaa `ohjelmistoprojekti.projekti.model.Lippu`. Vastauksissa lippu
palautetaan `LippuDto`-muodossa.

| Kenttä | Tyyppi | Kuvaus |
|---|---|---|
| `id` | Long | Lipun yksilöivä tunniste. Tietokanta generoi. |
| `tapahtumaid` | Tapahtuma | Tapahtuma, johon lippu kuuluu (sisäkkäisenä oliona). |
| `koodi` | String | Lipun koodi, jolla lippu haetaan (esim. ovella luettava koodi). |
| `kuvaus` | String | Lipun kuvaus. |
| `hinta` | double | Lipun hinta euroina. |
| `lipputyyppi` | Lipputyyppi | Lipputyyppi, johon lippu kuuluu (sisäkkäisenä oliona). |
| `kaytetty` | boolean | Onko lippu jo käytetty. Oletusarvo `false`. |

### Esimerkki JSON-esityksestä

```json
{
  "id": 1,
  "tapahtumaid": {
    "tapahtumaid": 1,
    "nimi": "Kesäfestivaali",
    "paikka": "Kaisaniemen puisto"
  },
  "koodi": "ABC123",
  "kuvaus": "Aikuisten päivälippu",
  "hinta": 25.0,
  "lipputyyppi": {
    "lipputyyppiId": 1,
    "nimi": "Aikuinen",
    "hinta": 25.0
  },
  "kaytetty": false
}
```

---

## Endpointit

| Metodi | Polku | Kuvaus | Tila |
|---|---|---|---|
| GET | `/ticket/ticket?koodi={koodi}` | Hae lippu lippukoodilla | Toteutettu |
| PATCH | `/ticket/{id}` | Merkitse lippu käytetyksi tai käyttämättömäksi | Toteutettu |

---

### GET /ticket/ticket

Hakee yksittäisen lipun lippukoodin perusteella. Palauttaa myös lipun
`kaytetty`-tilan, joten sitä voi käyttää ovella lipun tarkistamiseen.

**Metodi ja polku**

```
GET /ticket/ticket?koodi={koodi}
```

**Polkuparametrit**

Ei polkuparametreja.

**Query-parametrit**

| Parametri | Tyyppi | Pakollinen | Kuvaus |
|---|---|---|---|
| `koodi` | String | kyllä | Haettavan lipun koodi |

**Pyynnön sisältö**

Ei sisältöä.

**Vastaukset**

| Koodi | Tilanne | Sisältö |
|---|---|---|
| `200 OK` | Lippu löytyi | Lippu (`LippuDto`) JSON-muodossa |
| `400 Bad Request` | `koodi`-parametri puuttuu | Virheilmoitus |
| `401 Unauthorized` | Kirjautuminen puuttuu tai tunnukset ovat virheelliset | – |
| `404 Not Found` | Lippua ei löytynyt | `{"virhe":"Lippua ei löytynyt"}` |

**Esimerkkikutsu**

```bash
curl -u sahkoposti:salasana "http://localhost:8080/ticket/ticket?koodi=ABC123"
```

---

### PATCH /ticket/{id}

Päivittää lipun `kaytetty`-tilan. Käytetään lipun merkitsemiseen käytetyksi,
kun lippu on tarkastettu ovella.

**Metodi ja polku**

```
PATCH /ticket/{id}
```

**Polkuparametrit**

| Parametri | Tyyppi | Pakollinen | Kuvaus |
|---|---|---|---|
| `id` | Long | kyllä | Päivitettävän lipun `id` |

**Query-parametrit**

Ei query-parametreja.

**Pyynnön sisältö**

```json
{
  "kaytetty": true
}
```

| Kenttä | Tyyppi | Pakollinen | Kuvaus |
|---|---|---|---|
| `kaytetty` | boolean | kyllä | `true` = merkitään käytetyksi, `false` = palautetaan käyttämättömäksi |

**Vastaukset**

| Koodi | Tilanne | Sisältö |
|---|---|---|
| `200 OK` | Päivitys onnistui | Päivitetty lippu (`LippuDto`) JSON-muodossa |
| `400 Bad Request` | Virheellinen tai puuttuva pyynnön sisältö | `{"virhe":"..."}` |
| `401 Unauthorized` | Kirjautuminen puuttuu tai tunnukset ovat virheelliset | – |
| `404 Not Found` | Lippua ei löytynyt | `{"virhe":"Lippua ei löytynyt"}` |

**Esimerkkikutsu**

```bash
curl -u sahkoposti:salasana -X PATCH http://localhost:8080/ticket/1 \
  -H "Content-Type: application/json" \
  -d '{ "kaytetty": true }'
```

PowerShell:

```powershell
curl.exe -u "sahkoposti:salasana" -X PATCH "http://localhost:8080/ticket/1" `
  -H "Content-Type: application/json" `
  --data-raw '{"kaytetty":true}'
```

---

## Paluukoodit

| Koodi | Merkitys | Milloin |
|---|---|---|
| `200 OK` | Pyyntö onnistui | GET, PATCH |
| `400 Bad Request` | Virheellinen pyyntö tai puuttuva pakollinen kenttä | GET (puuttuva `koodi`), PATCH |
| `401 Unauthorized` | Kirjautuminen puuttuu tai tunnukset ovat virheelliset | Kaikki reitit |
| `404 Not Found` | Lippua ei löytynyt | GET, PATCH |
| `500 Internal Server Error` | Odottamaton palvelinvirhe | – |

Virhevastauksen yleinen rakenne on `{"virhe":"..."}`.

---

## Testidata

Tuotantoympäristön (Render) tietokantaan on lisätty käsin kokeiluun kolme lippua:

| Lipun `id` | `koodi` | `kaytetty` | Käyttötarkoitus |
|---|---|---|---|
| 1001 | `TESTI-001` | false | PATCH-kokeilu: merkitse käytetyksi |
| 1002 | `TESTI-002` | false | PATCH-kokeilu: merkitse käytetyksi |
| 1003 | `TESTI-003` | true | Jo käytetty lippu |

Esimerkki: `GET /ticket/ticket?koodi=TESTI-001` ja `PATCH /ticket/1001`.
Rajapinnalla ei voi vielä luoda lippuja, joten testilippuja ei löydy muualta.

## Tunnetut rajoitteet

Seuraavat asiat eivät ole vielä toteutettuja, ja ne kannattaa huomioida testatessa:

- **Lippuja ei voi vielä luoda rajapinnan kautta.** `POST /myynnit` luo myyntirivit,
  mutta ei `Lippu`-rivejä. Testilippuja ei siis ole ennen kuin ne lisätään
  tietokantaan käsin tai lipun luonti toteutetaan.
- **Jo käytetyn lipun uudelleen merkitsemistä ei estetä.** PATCH hyväksyy
  pyynnön, vaikka lippu olisi jo käytetty, eikä palauta virhettä (ehdotus: `409 Conflict`).
- **Käyttöaikaa ei tallenneta.** Relaatiokaaviossa on `kaytto_aika`, mutta
  `Lippu`-luokassa on vain `kaytetty`-tieto.
- **Roolirajausta ei ole.** Myös `VERKKOKAUPAN_ASIAKAS` voi tällä hetkellä
  merkitä lipun käytetyksi. Rajaus rooleihin `OVELLA_TARKASTAVA_HENKILO`
  ja `YLLAPITAJA` on suunniteltu.
- **`koodi`-kentän yksilöllisyyttä ei pakoteta** tietokannassa.