import { Cart } from "./Cart";
import { OrderStatusStrategy, PendingStatus } from "./OrderStatus";

export class Order {
  status: OrderStatusStrategy = new PendingStatus();
  constructor(public readonly id: string, public readonly cart: Cart, public readonly customerEmail: string) {}

  setStatusStrategy(s: OrderStatusStrategy) {
    this.status = s;
  }
}
