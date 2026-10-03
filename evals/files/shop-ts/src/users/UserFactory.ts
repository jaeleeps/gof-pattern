import { User } from "./User";

export class UserFactory {
  static create(name: string, email: string): User {
    return new User(name, email);
  }
}
