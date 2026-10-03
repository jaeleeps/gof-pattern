// Stand-in for a third-party SDK that we do not control.
export class PayPalClient {
  async executePayment(body: { total: string; currency_code: string; invoice_id: string }) {
    return { state: "approved", transactionId: "PP-" + Date.now() };
  }
}
