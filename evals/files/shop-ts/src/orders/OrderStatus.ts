export interface OrderStatusStrategy {
  name: string;
  canCancel(): boolean;
  canRefund(): boolean;
  canShip(): boolean;
}

export class PendingStatus implements OrderStatusStrategy {
  name = "pending";
  canCancel() { return true; }
  canRefund() { return false; }
  canShip() { return false; }
}

export class PaidStatus implements OrderStatusStrategy {
  name = "paid";
  canCancel() { return true; }
  canRefund() { return true; }
  canShip() { return true; }
}

export class ShippedStatus implements OrderStatusStrategy {
  name = "shipped";
  canCancel() { return false; }
  canRefund() { return true; }
  canShip() { return false; }
}

export class CancelledStatus implements OrderStatusStrategy {
  name = "cancelled";
  canCancel() { return false; }
  canRefund() { return false; }
  canShip() { return false; }
}
