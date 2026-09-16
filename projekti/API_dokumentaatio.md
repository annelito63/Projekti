# Tapahtuma API
 
## Base URL
 
```
http://localhost:8080
```
 
Kehitysympäristössä käytetään H2-muistitietokantaa, joten data ei säily palvelimen uudelleenkäynnistyksen yli.
 
---
 
## GET /tapahtumat
 
Hakee kaikki tapahtumat, tai suodattaa annetuilla query-parametreilla.
 
**Metodi:** `GET`
**Polku:** `/tapahtumat`
**Polkuparametrit:** ei ole
 
**Query-parametrit** (valinnaisia, ei molempia samaan aikaan):
 
| Parametri | Tyyppi | Pakollinen | Kuvaus                          |
|-----------|--------|------------|----------------------------------|
| `nimi`    | string | ei         | Suodattaa tapahtuman nimellä    |
| `paikka`  | string | ei         | Suodattaa tapahtuman paikalla   |
 
**Pyynnön sisältö:** ei ole (GET-pyynnöllä ei ole bodya)
 
**Vastaus:**
 
| Tilanne          | Paluukoodi | Sisältö                          |
|------------------|------------|-----------------------------------|
| Onnistui         | `200 OK`   | JSON-taulukko `Tapahtuma`-olioita |
 
```json
[
  {
    "tapahtumaid": 1,
    "nimi": "Kesäfestivaali",
    "tyyppi": "Musiikki",
    "aika": "2026-07-10",
    "kaupunki": "Helsinki",
    "paikka": "Kaisaniemen puisto",
    "kuvaus": "Kolmipäiväinen musiikkifestivaali",
    "maara": 5000
  }
]
```
 
---
 
## GET /tapahtumat/{id}
 
Hakee yksittäisen tapahtuman id:llä.
 
**Metodi:** `GET`
**Polku:** `/tapahtumat/{id}`
**Polkuparametrit:**
 
| Parametri | Tyyppi | Pakollinen | Kuvaus              |
|-----------|--------|------------|-----------------------|
| `id`      | Long   | kyllä      | Tapahtuman tunniste  |
 
**Query-parametrit:** ei ole
**Pyynnön sisältö:** ei ole
 
**Vastaus:**
 
| Tilanne             | Paluukoodi     | Sisältö                     |
|----------------------|----------------|------------------------------|
| Tapahtuma löytyi     | `200 OK`       | JSON `Tapahtuma`-olio       |
| Tapahtumaa ei löydy  | `404 Not Found`| tyhjä body                  |
 
```json
{
  "tapahtumaid": 1,
  "nimi": "Kesäfestivaali",
  "tyyppi": "Musiikki",
  "aika": "2026-07-10",
  "kaupunki": "Helsinki",
  "paikka": "Kaisaniemen puisto",
  "kuvaus": "Kolmipäiväinen musiikkifestivaali",
  "maara": 5000
}
```
 
---
 
## POST /tapahtumat
 
Luo uuden tapahtuman.
 
**Metodi:** `POST`
**Polku:** `/tapahtumat`
**Polkuparametrit:** ei ole
**Query-parametrit:** ei ole
 
**Pyynnön sisältö** (JSON, `tapahtumaid` jätetään pois, kanta generoi sen):
 
```json
{
  "nimi": "Kesäfestivaali",
  "tyyppi": "Musiikki",
  "aika": "2026-07-10",
  "kaupunki": "Helsinki",
  "paikka": "Kaisaniemen puisto",
  "kuvaus": "Kolmipäiväinen musiikkifestivaali",
  "maara": 5000
}
```
 
**Vastaus:**
 
| Tilanne  | Paluukoodi    | Sisältö                                   |
|----------|---------------|---------------------------------------------|
| Onnistui | `201 Created` | JSON tallennettu `Tapahtuma`, sis. `tapahtumaid` |
 
```json
{
  "tapahtumaid": 2,
  "nimi": "Kesäfestivaali",
  "tyyppi": "Musiikki",
  "aika": "2026-07-10",
  "kaupunki": "Helsinki",
  "paikka": "Kaisaniemen puisto",
  "kuvaus": "Kolmipäiväinen musiikkifestivaali",
  "maara": 5000
}
```
 
---
 
## PUT /tapahtumat/{id}
 
Päivittää olemassa olevan tapahtuman kaikki kentät.
 
**Metodi:** `PUT`
**Polku:** `/tapahtumat/{id}`
**Polkuparametrit:**
 
| Parametri | Tyyppi | Pakollinen | Kuvaus              |
|-----------|--------|------------|-----------------------|
| `id`      | Long   | kyllä      | Päivitettävän tapahtuman tunniste |
 
**Query-parametrit:** ei ole
 
**Pyynnön sisältö** (JSON, koko olio uusilla arvoilla):
 
```json
{
  "nimi": "Kesäfestivaali 2.0",
  "tyyppi": "Musiikki",
  "aika": "2026-07-11",
  "kaupunki": "Helsinki",
  "paikka": "Kaisaniemen puisto",
  "kuvaus": "Päivitetty kuvaus",
  "maara": 6000
}
```
 
**Vastaus:**
 
| Tilanne              | Paluukoodi      | Sisältö                     |
|-----------------------|-----------------|------------------------------|
| Päivitys onnistui     | `200 OK`        | JSON päivitetty `Tapahtuma` |
| Tapahtumaa ei löydy   | `404 Not Found` | tyhjä body                  |
 
---