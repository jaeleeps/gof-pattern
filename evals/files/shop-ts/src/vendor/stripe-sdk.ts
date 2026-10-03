// Stand-in for a third-party SDK that we do not control.
export class Stripe {
  constructor(public apiKey: string) {}
  charges = {
    create: async (params: { amount_cents: number; currency: string; metadata: Record<string, string> }) => {
      return { id: "ch_" + Math.random().toString(36).slice(2), status: "succeeded" as const };
    },
  };
}
