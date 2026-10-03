import { Stripe } from "../vendor/stripe-sdk";
import { ChargeResult, PaymentGateway } from "./PaymentGateway";

export class StripeGateway implements PaymentGateway {
  constructor(private stripe: Stripe) {}

  async charge(amount: number, currency: string, orderId: string): Promise<ChargeResult> {
    const res = await this.stripe.charges.create({
      amount_cents: Math.round(amount * 100),
      currency: currency.toLowerCase(),
      metadata: { orderId },
    });
    return { ok: res.status === "succeeded", reference: res.id };
  }
}
