const http = require("node:http");
const fs = require("node:fs");
const path = require("node:path");

const root = __dirname;
const apiOrigin = "https://projekti-6k8i.onrender.com";
const port = Number(process.env.PORT) || 4173;
const host = process.env.PORT ? "0.0.0.0" : "127.0.0.1";
const contentTypes = {
  ".css": "text/css; charset=utf-8",
  ".html": "text/html; charset=utf-8",
  ".js": "text/javascript; charset=utf-8"
};

function replyError(response, status, message) {
  response.writeHead(status, { "Content-Type": "application/json; charset=utf-8" });
  response.end(JSON.stringify({ message }));
}

async function proxyApi(request, response) {
  const target = new URL(request.url.slice(4), apiOrigin);
  const headers = {};
  for (const name of ["accept", "authorization", "content-type"]) {
    if (request.headers[name]) headers[name] = request.headers[name];
  }

  const options = { method: request.method, headers };
  if (request.method !== "GET" && request.method !== "HEAD") {
    const chunks = [];
    for await (const chunk of request) chunks.push(chunk);
    options.body = Buffer.concat(chunks);
  }

  try {
    const upstream = await fetch(target, options);
    const responseHeaders = {};
    for (const name of ["content-type", "cache-control"]) {
      const value = upstream.headers.get(name);
      if (value) responseHeaders[name] = value;
    }
    response.writeHead(upstream.status, responseHeaders);
    response.end(Buffer.from(await upstream.arrayBuffer()));
  } catch (error) {
    console.error("API proxy request failed:", error.message);
    replyError(response, 502, "API-palveluun ei saatu yhteyttä.");
  }
}

http.createServer((request, response) => {
  const pathname = new URL(request.url, "http://localhost").pathname;
  if (pathname.startsWith("/api/")) {
    proxyApi(request, response);
    return;
  }

  if (request.method !== "GET" && request.method !== "HEAD") {
    replyError(response, 405, "Metodia ei tueta.");
    return;
  }

  let decodedPath;
  try {
    decodedPath = decodeURIComponent(pathname);
  } catch {
    replyError(response, 400, "Virheellinen polku.");
    return;
  }

  const requestedFile = decodedPath === "/" ? "index.html" : decodedPath.slice(1);
  const file = path.resolve(root, requestedFile);
  if (file !== root && !file.startsWith(`${root}${path.sep}`)) {
    replyError(response, 403, "Pääsy estetty.");
    return;
  }

  fs.readFile(file, (error, content) => {
    if (error) {
      replyError(response, error.code === "ENOENT" ? 404 : 500, "Tiedostoa ei voitu lukea.");
      return;
    }
    response.writeHead(200, {
      "Content-Type": contentTypes[path.extname(file)] || "application/octet-stream"
    });
    response.end(request.method === "HEAD" ? undefined : content);
  });
}).listen(port, host, () => {
  console.log(`TicketGuru client: http://${host}:${port}`);
});
