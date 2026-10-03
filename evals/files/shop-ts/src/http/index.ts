import { FetchHttpClient, HttpClient, LoggingHttpClient, RetryingHttpClient } from "./HttpClient";

export function makeHttpClient(opts: { retries?: number; verbose?: boolean }): HttpClient {
  let client: HttpClient = new FetchHttpClient();
  if (opts.retries) client = new RetryingHttpClient(client, opts.retries);
  if (opts.verbose) client = new LoggingHttpClient(client, console.log);
  return client;
}
