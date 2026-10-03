import { PayPalClient } from "../vendor/paypal-sdk";
import { ChargeResult, PaymentGateway } from "./PaymentGateway";

export class PayPalGateway implements PaymentGateway {
  constructor(private client: PayPalClient) {}

  async charge(amount: number, currency: string, orderId: string): Promise<ChargeResult> {
    const res = await this.client.executePayment({
      total: amount.toFixed(2),
      currency_code: currency.toUpperCase(),
      invoice_id: orderId,
    });
    return { ok: res.state === "approved", reference: res.transactionId };
  }
}
