export type Channel = "email" | "sms" | "push";

export interface Recipient {
  channel: Channel;
  address: string;
}

export function formatMessage(r: Recipient, text: string): string {
  switch (r.channel) {
    case "email": return `<p>${text}</p>`;
    case "sms": return text.slice(0, 160);
    case "push": return text.slice(0, 80);
  }
}

export function costCents(r: Recipient): number {
  switch (r.channel) {
    case "email": return 0;
    case "sms": return 4;
    case "push": return 1;
  }
}

export async function send(r: Recipient, text: string): Promise<void> {
  const body = formatMessage(r, text);
  switch (r.channel) {
    case "email": console.log("SMTP ->", r.address, body); break;
    case "sms": console.log("Twilio ->", r.address, body); break;
    case "push": console.log("FCM ->", r.address, body); break;
  }
}

export function isValidAddress(r: Recipient): boolean {
  switch (r.channel) {
    case "email": return r.address.includes("@");
    case "sms": return /^\+\d{8,15}$/.test(r.address);
    case "push": return r.address.length === 64;
  }
}
