interface Bindings {
  API_KEY: string;
}

const PRERENDERED_DOMAINS = ["starrylines.pablolec.dev"];

const BOT_AGENTS = [
  "googlebot",
  "yahoo! slurp",
  "bingbot",
  "yandex",
  "baiduspider",
  "facebookexternalhit",
  "twitterbot",
  "rogerbot",
  "linkedinbot",
  "embedly",
  "quora link preview",
  "showyoubot",
  "outbrain",
  "pinterest/0.",
  "developers.google.com/+/web/snippet",
  "slackbot",
  "vkshare",
  "w3c_validator",
  "redditbot",
  "applebot",
  "whatsapp",
  "flipboard",
  "tumblr",
  "bitlybot",
  "skypeuripreview",
  "nuzzel",
  "discordbot",
  "google page speed",
  "qwantify",
  "pinterestbot",
  "bitrix link preview",
  "xing-contenttabreceiver",
  "chrome-lighthouse",
  "telegrambot",
];

const worker: ExportedHandler<Bindings> = {
  async fetch(req, env) {
    const url = new URL(req.url);
    const { hostname } = url;
    const requestUserAgent = (
      req.headers.get("User-Agent") || ""
    ).toLowerCase();
    const xPrerender = req.headers.get("X-Prerender");
    const pathName = url.pathname.toLowerCase();
    const ext = pathName.substring(
      pathName.lastIndexOf(".") || pathName.length
    );

    if (
      !xPrerender &&
      containsOneOfThem(BOT_AGENTS, requestUserAgent) &&
      hostname == "starrylines.pablolec.dev"
    ) {
      return prerenderRequest(req, env.API_KEY);
    }
    return await fetch(req);
  },
};

function containsOneOfThem(array, element) {
  return array.some((e) => element.indexOf(e) !== -1);
}

function prerenderRequest(request, apiKey) {
  const { url, headers } = request;
  const prerenderUrl = `https://service.prerender.io/${url}`;
  const headersToSend = new Headers(headers);

  headersToSend.set("X-Prerender-Token", apiKey);

  const prerenderRequest = new Request(prerenderUrl, {
    headers: headersToSend,
    redirect: "manual",
  });

  return fetch(prerenderRequest);
}

export default worker;
