export interface CartLine {
  sku: string;
  unitPrice: number;
  qty: number;
}

export class Cart {
  lines: CartLine[] = [];
}
