import * as Realm from "realm-web";
import * as utils from "./utils";

interface Bindings {
    REALM_APPID: string;
}

type Document = globalThis.Realm.Services.MongoDB.Document;

interface Repository extends Document {
    name: string;
    descriprion: string;
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
        const url = new URL(req.url);
        const cacheKey = new Request(url.toString(), req);
        const cache = await caches.default;

        let cacheResponse = await cache.match(cacheKey);

        if (cacheResponse) {
            console.log(`Cache hit for: ${req.url}.`);
            return cacheResponse;
        }

        App = App || new Realm.App(env.REALM_APPID);

        const method = req.method;
        const path = url.pathname.replace(/[/]$/, "");
        const topID = url.pathname.split("/")[2];

        if (!path.startsWith("/api/")) {
            return utils.toError(404);
        }

        try {
            var client = await login(env.MONGO_API_KEY);
        } catch (err) {
            return utils.toError("Error with authentication.", 500);
        }

        const collection = client.db("StarryLines").collection<Repository>(topID);

        try {
            if (method !== "GET") {
                return utils.toError(405);
            }
            let response = utils.reply(await collection.find());
            await cache.put(cacheKey, response.clone());
            return response;
        } catch (err) {
            const msg = (err as Error).message || "Error with query.";
            return utils.toError(msg, 500);
        }
    },
};

async function login(token: string) {
    const credentials = Realm.Credentials.apiKey(token);
    const user = await App.logIn(credentials);
    return user.mongoClient("mongodb-atlas");
}

export default worker;
