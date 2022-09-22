import * as Realm from "realm-web";

interface Bindings {
    REALM_APPID: string;
    StarryLinesTop: KVNamespace;
}

type Document = globalThis.Realm.Services.MongoDB.Document;

interface Repository extends Document {
    name: string;
    description: string;
    createdAt: Date;
    stargazers: number;
    url: string;
    loc: number;
    milliStarsPerLine: number;
    score: number;
    languagePercent: string;
}

let App: Realm.App;

const worker: ExportedHandler<Bindings> = {
    async fetch(req, env) {
        App = App || new Realm.App(env.REALM_APPID);
        const url = new URL(req.url);
        const method = req.method;
        const path = url.pathname.replace(/[/]$/, "");
        const topID = url.pathname.split("/")[2];

        // Try to get KV
        let kv = await env.StarryLinesTop.get(topID, { cacheTtl: 7200 });
        if (kv) {
            console.log(`KV hit for: ${req.url}.`);
            return reply(JSON.parse(kv));
        }

        // Fallback on edge cache
        const cacheKey = new Request(url.toString(), req);
        const cache = await caches.default;
        let cacheResponse = await cache.match(cacheKey);
        if (cacheResponse) {
            console.log(`Cache hit for: ${req.url}.`);
            return cacheResponse;
        }

        if (!path.startsWith("/api/")) {
            return toError(404);
        }

        try {
            var client = await login(env.MONGO_API_KEY);
        } catch (err) {
            return toError("Error with authentication.", 500);
        }

        const collection = client.db("StarryLines").collection<Repository>(topID);

        try {
            if (method !== "GET") return toError(405);
            let data = await collection.find();
            // Remove _id from response
            data.forEach(function (v) {
                delete v._id;
            });
            let response = reply(data);

            await cache.put(cacheKey, response.clone());
            await env.StarryLinesTop.put(topID, JSON.stringify(data), { expirationTtl: 7200 });

            return response;
        } catch (err) {
            const msg = (err as Error).message || "Error with query.";
            return toError(msg, 500);
        }
    },
};

async function login(token: string) {
    const credentials = Realm.Credentials.apiKey(token);
    const user = await App.logIn(credentials);
    return user.mongoClient("mongodb-atlas");
}

const cacheHeaders = {
    "Cache-Control": "max-age=7200, s-maxage=7200",
};

function toJSON(data: unknown, status = 200): Response {
    let headers = { "content-type": "application/json" };
    if (status == 200) {
        headers = { ...headers, ...cacheHeaders };
    }
    let body = JSON.stringify(data, null, 2);
    return new Response(body, { headers, status });
}

function toError(error: string | unknown, status = 400): Response {
    return toJSON({ error }, status);
}

function reply(output: any): Response {
    if (output != null) return toJSON(output, 200);
    return toError("Error with query", 500);
}

export default worker;
