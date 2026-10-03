import { AppConfig } from "../config/AppConfig";
import { orderEvents } from "../events/orderEvents";
import { PaymentGateway } from "../payments/PaymentGateway";
import { Order } from "./Order";
import { PaidStatus, ShippedStatus, CancelledStatus } from "./OrderStatus";

export class OrderService {
  constructor(private payments: PaymentGateway) {}

  async pay(order: Order, amount: number) {
    const currency = AppConfig.getInstance().get("currency") ?? "USD";
    if (order.status.name !== "pending") throw new Error("cannot pay order in " + order.status.name);
    await this.payments.charge(amount, currency, order.id);
    order.setStatusStrategy(new PaidStatus());
    orderEvents.emit("paid", order);
  }

  ship(order: Order) {
    if (!order.status.canShip()) throw new Error("cannot ship");
    if (AppConfig.getInstance().get("shippingPaused") === "true") throw new Error("paused");
    order.setStatusStrategy(new ShippedStatus());
    orderEvents.emit("shipped", order);
  }

  cancel(order: Order) {
    if (!order.status.canCancel()) throw new Error("cannot cancel");
    if (order.status.name === "paid") {
      // refund handled later by finance batch
    }
    order.setStatusStrategy(new CancelledStatus());
    orderEvents.emit("cancelled", order);
  }
}
