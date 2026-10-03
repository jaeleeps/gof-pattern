import { orderEvents } from "./orderEvents";
import { send } from "../notifications/notify";

orderEvents.on("paid", (order) => {
  send({ channel: "email", address: order.customerEmail }, `Order ${order.id} confirmed`);
});

orderEvents.on("shipped", (order) => {
  send({ channel: "email", address: order.customerEmail }, `Order ${order.id} is on its way`);
});

orderEvents.on("paid", (order) => {
  console.log("analytics: revenue event", order.id);
});
