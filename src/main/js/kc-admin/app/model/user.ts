export class User {
  name;
  givenName;
  familyName;

  constructor() {
  }

  getName(): string {
    return name;
  }

  setName(name: string) {
    this.name = name;
  }
}
