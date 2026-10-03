import { ShippingService } from "./ShippingService";

export class ShippingProxy implements ShippingService {
  constructor(private inner: ShippingService) {}

  quote(weightKg: number, country: string): number {
    const started = Date.now();
    const result = this.inner.quote(weightKg, country);
    console.log(`shipping.quote ${country} ${weightKg}kg = ${result} (${Date.now() - started}ms)`);
    return result;
  }
}
