export interface ChargeResult {
  ok: boolean;
  reference: string;
}

export interface PaymentGateway {
  charge(amount: number, currency: string, orderId: string): Promise<ChargeResult>;
}
