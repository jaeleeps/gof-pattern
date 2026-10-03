import { GiftCardLine, LineNode, LineVisitor, ProductLine } from "./OrderLineNodes";

export class TotalVisitor implements LineVisitor {
  total = 0;
  visitProduct(line: ProductLine) { this.total += line.price * line.qty; }
  visitGiftCard(line: GiftCardLine) { this.total += line.amount; }
}

export function reportTotal(lines: LineNode[]): number {
  const v = new TotalVisitor();
  lines.forEach((l) => l.accept(v));
  return v.total;
}
