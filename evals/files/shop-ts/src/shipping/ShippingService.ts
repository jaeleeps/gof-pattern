export interface ShippingService {
  quote(weightKg: number, country: string): number;
}

export class FlatRateShipping implements ShippingService {
  quote(weightKg: number, country: string): number {
    const base = country === "US" ? 5 : 15;
    return base + weightKg * 1.2;
  }
}
