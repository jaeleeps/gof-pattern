export interface HttpClient {
  get(url: string): Promise<string>;
}

export class FetchHttpClient implements HttpClient {
  async get(url: string): Promise<string> {
    const res = await fetch(url);
    return res.text();
  }
}

export class RetryingHttpClient implements HttpClient {
  constructor(private inner: HttpClient, private attempts = 3) {}
  async get(url: string): Promise<string> {
    let lastErr: unknown;
    for (let i = 0; i < this.attempts; i++) {
      try {
        return await this.inner.get(url);
      } catch (e) {
        lastErr = e;
      }
    }
    throw lastErr;
  }
}

export class LoggingHttpClient implements HttpClient {
  constructor(private inner: HttpClient, private log: (msg: string) => void) {}
  async get(url: string): Promise<string> {
    this.log("GET " + url);
    const body = await this.inner.get(url);
    this.log("GET " + url + " -> " + body.length + " bytes");
    return body;
  }
}
