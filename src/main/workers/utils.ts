const cacheHeaders = {
    "Cache-Control": "max-age=3600, s-maxage=3600",
};

export function toJSON(data: unknown, status = 200): Response {
    let body = JSON.stringify(data, null, 2);
    let headers = { "content-type": "application/json" };
    if (status == 200) {
        headers = { ...headers, ...cacheHeaders };
    }
    return new Response(body, { headers, status });
}

export function toError(error: string | unknown, status = 400): Response {
    return toJSON({ error }, status);
}

export function reply(output: any): Response {
    if (output != null) return toJSON(output, 200);
    return toError("Error with query", 500);
}
