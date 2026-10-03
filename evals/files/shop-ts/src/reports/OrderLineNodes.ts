export interface LineVisitor {
  visitProduct(line: ProductLine): void;
  visitGiftCard(line: GiftCardLine): void;
}

export interface LineNode {
  accept(v: LineVisitor): void;
}

export class ProductLine implements LineNode {
  constructor(public sku: string, public price: number, public qty: number) {}
  accept(v: LineVisitor) { v.visitProduct(this); }
}

export class GiftCardLine implements LineNode {
  constructor(public code: string, public amount: number) {}
  accept(v: LineVisitor) { v.visitGiftCard(this); }
}
