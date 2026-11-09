const API = "/api";
const state = {
  events: [],
  event: null,
  ticketTypes: [],
  quantities: {},
  credentials: null,
  authMode: "login"
};

const $ = (selector) => document.querySelector(selector);
const eventList = $("#event-list");
const eventSearch = $("#event-search");
const eventsStatus = $("#events-status");
const ticketTypes = $("#ticket-types");
const orderEmpty = $("#order-empty");
const orderTotal = $("#order-total");
const checkoutButton = $("#checkout-button");
const purchaseStatus = $("#purchase-status");

function price(value) {
  return new Intl.NumberFormat("fi-FI", { style: "currency", currency: "EUR" })
    .format(Number(value) || 0);
}

function date(value) {
  const parsed = new Date(value);
  return Number.isNaN(parsed.getTime())
    ? value || ""
    : new Intl.DateTimeFormat("fi-FI", { dateStyle: "medium", timeStyle: "short" }).format(parsed);
}

function eventId(event) {
  return event.tapahtumaid ?? event.tapahtumaId ?? event.id;
}

function typeId(type) {
  return type.lipputyyppiId ?? type.lipputyyppi_id ?? type.id;
}

async function request(path, options = {}) {
  const headers = { Accept: "application/json" };
  if (options.body) headers["Content-Type"] = "application/json";
  if (options.auth && state.credentials) {
    const bytes = new TextEncoder().encode(`${state.credentials.email}:${state.credentials.password}`);
    headers.Authorization = `Basic ${btoa(String.fromCharCode(...bytes))}`;
  }

  let response;
  try {
    response = await fetch(`${API}${path}`, {
      method: options.method || "GET",
      headers,
      body: options.body ? JSON.stringify(options.body) : undefined
    });
  } catch {
    throw new Error("Palveluun ei saatu yhteyttä. Yritä uudelleen.");
  }

  if (!response.ok) {
    if (response.status === 401) throw new Error("Sähköposti tai salasana on väärä.");
    if (response.status === 409) throw new Error("Sähköpostiosoite on jo käytössä. Kirjaudu sisään.");
    if (response.status === 503) throw new Error("Palvelu ei vastaa juuri nyt. Yritä uudelleen.");
    if (response.status === 404) throw new Error("Tietoa ei löytynyt. Päivitä sivu ja yritä uudelleen.");
    throw new Error(`Pyyntö epäonnistui (HTTP ${response.status}).`);
  }

  if (response.status === 204) return null;
  return response.json();
}

function showEventError(message) {
  eventsStatus.textContent = message;
  eventsStatus.classList.add("is-error");
  const retry = document.createElement("button");
  retry.type = "button";
  retry.textContent = "Yritä uudelleen";
  retry.addEventListener("click", loadEvents);
  eventsStatus.append(" ", retry);
}

function renderEvents() {
  eventList.replaceChildren();
  const search = eventSearch.value.trim().toLocaleLowerCase("fi");
  const events = state.events.filter((event) =>
    [event.nimi, event.tyyppi, event.kaupunki, event.paikka]
      .some((value) => String(value || "").toLocaleLowerCase("fi").includes(search))
  );
  $("#event-count").textContent = `${events.length} tapahtumaa`;

  if (!events.length) {
    const empty = document.createElement("p");
    empty.className = "event-empty";
    empty.textContent = state.events.length ? "Tapahtumia ei löytynyt." : "Ei tapahtumia.";
    eventList.append(empty);
    return;
  }

  for (const event of events) {
    const card = document.createElement("article");
    card.className = "event-card";
    if (state.event && String(eventId(event)) === String(eventId(state.event))) {
      card.classList.add("is-selected");
    }

    const title = document.createElement("h3");
    title.textContent = event.nimi || "Tapahtuma";
    const details = document.createElement("p");
    details.textContent = [event.tyyppi, event.kaupunki, event.paikka, date(event.aika)]
      .filter(Boolean)
      .join(" · ");
    const choose = document.createElement("button");
    choose.type = "button";
    choose.textContent = "Valitse";
    choose.addEventListener("click", () => selectEvent(event));
    card.append(title, details, choose);
    eventList.append(card);
  }
}

async function loadEvents() {
  eventsStatus.classList.remove("is-error");
  eventsStatus.textContent = "Haetaan tapahtumia…";
  try {
    const events = await request("/tapahtumat");
    if (!Array.isArray(events)) throw new Error("Tapahtumia ei voitu näyttää.");
    state.events = events;
    eventsStatus.textContent = "";
    renderEvents();
  } catch (error) {
    eventList.replaceChildren();
    $("#event-count").textContent = "";
    showEventError(error.message);
  }
}

function renderEvent() {
  const selected = $("#selected-event");
  selected.replaceChildren();
  if (!state.event) {
    selected.textContent = "Valitse tapahtuma.";
    return;
  }
  const name = document.createElement("strong");
  name.textContent = state.event.nimi || "Tapahtuma";
  const details = document.createElement("div");
  const detail = document.createElement("span");
  detail.textContent = [state.event.kaupunki, date(state.event.aika)].filter(Boolean).join(" · ");
  details.append(detail);
  selected.append(name, details);
}

async function selectEvent(event) {
  state.event = event;
  state.ticketTypes = [];
  state.quantities = {};
  purchaseStatus.textContent = "";
  purchaseStatus.classList.remove("is-error");
  renderEvents();
  renderEvent();
  ticketTypes.replaceChildren();
  orderEmpty.hidden = false;
  orderEmpty.querySelector("p").textContent = "Haetaan lippuja…";
  updateTotal();

  try {
    const types = await request(`/tapahtumat/${encodeURIComponent(eventId(event))}/lipputyypit`);
    if (!Array.isArray(types)) throw new Error("Lipputyyppejä ei voitu näyttää.");
    if (String(eventId(state.event)) !== String(eventId(event))) return;
    state.ticketTypes = types.filter((type) => typeId(type) != null);
    for (const type of state.ticketTypes) state.quantities[typeId(type)] = 0;
    renderTicketTypes();
  } catch (error) {
    if (String(eventId(state.event)) !== String(eventId(event))) return;
    orderEmpty.hidden = false;
    orderEmpty.querySelector("p").textContent = error.message;
  }
}

function renderTicketTypes() {
  ticketTypes.replaceChildren();
  orderEmpty.hidden = state.ticketTypes.length > 0;
  if (!state.ticketTypes.length) {
    orderEmpty.querySelector("p").textContent = "Tapahtumalle ei ole lippuja.";
  }

  for (const type of state.ticketTypes) {
    const id = typeId(type);
    const row = document.createElement("div");
    row.className = "ticket-type-row";
    const details = document.createElement("div");
    const name = document.createElement("p");
    name.className = "ticket-type-name";
    name.textContent = type.nimi || "Lippu";
    const description = document.createElement("p");
    description.className = "ticket-type-description";
    description.textContent = type.kuvaus || "";
    details.append(name, description);

    const controls = document.createElement("div");
    controls.className = "quantity-controls";
    const minus = document.createElement("button");
    minus.type = "button";
    minus.textContent = "−";
    minus.disabled = state.quantities[id] === 0;
    minus.setAttribute("aria-label", `Vähennä ${type.nimi || "lippua"}`);
    minus.addEventListener("click", () => changeQuantity(id, -1));
    const amount = document.createElement("span");
    amount.textContent = state.quantities[id];
    const plus = document.createElement("button");
    plus.type = "button";
    plus.textContent = "+";
    plus.setAttribute("aria-label", `Lisää ${type.nimi || "lippu"}`);
    plus.addEventListener("click", () => changeQuantity(id, 1));
    controls.append(minus, amount, plus);

    const purchase = document.createElement("div");
    purchase.className = "ticket-type-price";
    purchase.append(document.createTextNode(price(type.hinta)), controls);
    row.append(details, purchase);
    ticketTypes.append(row);
  }
  updateTotal();
}

function changeQuantity(id, change) {
  state.quantities[id] = Math.max(0, state.quantities[id] + change);
  renderTicketTypes();
}

function selectedLines() {
  return state.ticketTypes
    .map((type) => ({ lipputyyppiId: typeId(type), maara: state.quantities[typeId(type)] }))
    .filter((line) => line.maara > 0);
}

function updateTotal() {
  const total = state.ticketTypes.reduce((sum, type) =>
    sum + Number(type.hinta || 0) * (state.quantities[typeId(type)] || 0), 0);
  orderTotal.textContent = price(total);
  checkoutButton.disabled = total <= 0;
}

function setAuthMode(mode) {
  state.authMode = mode;
  const registering = mode === "register";
  $("#name-field").hidden = !registering;
  $("#auth-name").required = registering;
  $("#auth-password").autocomplete = registering ? "new-password" : "current-password";
  $("#auth-title").textContent = registering ? "Luo tili" : "Kirjaudu";
  $("#auth-submit").textContent = registering ? "Luo tili" : "Kirjaudu sisään";
  $("#login-tab").classList.toggle("is-active", !registering);
  $("#register-tab").classList.toggle("is-active", registering);
  $("#auth-error").textContent = "";
}

async function authenticate(email, password) {
  const previous = state.credentials;
  state.credentials = { email, password };
  try {
    const user = await request("/auth/me", { auth: true });
    if (user.rooli !== "VERKKOKAUPAN_ASIAKAS") throw new Error("Kirjaudu asiakastilillä.");
    $("#account-button").textContent = "Kirjaudu ulos";
  } catch (error) {
    state.credentials = previous;
    throw error;
  }
}

async function submitAuth(event) {
  event.preventDefault();
  const email = $("#auth-email").value.trim();
  const password = $("#auth-password").value;
  const button = $("#auth-submit");
  const errorMessage = $("#auth-error");
  button.disabled = true;
  errorMessage.textContent = "";
  try {
    if (state.authMode === "register") {
      await request("/auth/register", {
        method: "POST",
        body: { nimi: $("#auth-name").value.trim(), sahkoposti: email, salasana: password }
      });
    }
    await authenticate(email, password);
    $("#auth-dialog").close();
    $("#auth-form").reset();
  } catch (error) {
    errorMessage.textContent = error.message;
  } finally {
    button.disabled = false;
    button.textContent = state.authMode === "register" ? "Luo tili" : "Kirjaudu sisään";
  }
}

async function checkout() {
  if (!state.credentials) {
    $("#auth-dialog").showModal();
    return;
  }
  const lines = selectedLines();
  if (!state.event || !lines.length) return;

  checkoutButton.disabled = true;
  purchaseStatus.classList.remove("is-error");
  purchaseStatus.textContent = "Tallennetaan tilausta…";
  try {
    const sale = await request("/myynnit", {
      method: "POST",
      auth: true,
      body: { tapahtumaId: eventId(state.event), rivit: lines }
    });
    const total = sale?.kokonaissumma ?? lines.reduce((sum, line) => {
      const type = state.ticketTypes.find((item) => typeId(item) === line.lipputyyppiId);
      return sum + Number(type.hinta) * line.maara;
    }, 0);
    $("#success-message").textContent = `${state.event.nimi}: ${price(total)}. Maksua ei käsitelty.`;
    $("#success-dialog").showModal();
    for (const id of Object.keys(state.quantities)) state.quantities[id] = 0;
    renderTicketTypes();
    purchaseStatus.textContent = "";
  } catch (error) {
    purchaseStatus.textContent = error.message;
    purchaseStatus.classList.add("is-error");
  } finally {
    updateTotal();
  }
}

eventSearch.addEventListener("input", renderEvents);
$("#account-button").addEventListener("click", () => {
  if (!state.credentials) {
    setAuthMode("login");
    $("#auth-dialog").showModal();
    return;
  }
  state.credentials = null;
  $("#account-button").textContent = "Kirjaudu";
});
checkoutButton.addEventListener("click", checkout);
$("#auth-form").addEventListener("submit", submitAuth);
$("#login-tab").addEventListener("click", () => setAuthMode("login"));
$("#register-tab").addEventListener("click", () => setAuthMode("register"));
$("#close-auth").addEventListener("click", () => $("#auth-dialog").close());
$("#close-success").addEventListener("click", () => $("#success-dialog").close());
$("#success-done").addEventListener("click", () => $("#success-dialog").close());

loadEvents();
