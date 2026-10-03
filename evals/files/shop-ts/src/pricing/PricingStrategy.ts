import { Cart } from "../orders/Cart";

export interface PricingStrategy {
  total(cart: Cart): number;
}

export class RegularPricing implements PricingStrategy {
  total(cart: Cart): number {
    return cart.lines.reduce((sum, l) => sum + l.unitPrice * l.qty, 0);
  }
}

export class MemberPricing implements PricingStrategy {
  constructor(private discountRate: number) {}
  total(cart: Cart): number {
    const base = new RegularPricing().total(cart);
    return base * (1 - this.discountRate);
  }
}

export class BulkPricing implements PricingStrategy {
  total(cart: Cart): number {
    return cart.lines.reduce((sum, l) => {
      const price = l.qty >= 10 ? l.unitPrice * 0.85 : l.unitPrice;
      return sum + price * l.qty;
    }, 0);
  }
}
