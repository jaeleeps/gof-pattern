import { Cart } from "../orders/Cart";
import { PricingStrategy, RegularPricing, MemberPricing, BulkPricing } from "./PricingStrategy";

export function pricingFor(customer: { member: boolean; wholesale: boolean }): PricingStrategy {
  if (customer.wholesale) return new BulkPricing();
  if (customer.member) return new MemberPricing(0.1);
  return new RegularPricing();
}

export class Checkout {
  constructor(private pricing: PricingStrategy) {}

  quote(cart: Cart): number {
    return Math.round(this.pricing.total(cart) * 100) / 100;
  }
}
