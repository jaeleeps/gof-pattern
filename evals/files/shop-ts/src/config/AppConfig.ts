export class AppConfig {
  private static instance: AppConfig;
  private values: Record<string, string> = {};

  private constructor() {}

  static getInstance(): AppConfig {
    if (!AppConfig.instance) {
      AppConfig.instance = new AppConfig();
    }
    return AppConfig.instance;
  }

  get(key: string): string | undefined {
    return this.values[key];
  }

  set(key: string, value: string): void {
    this.values[key] = value;
  }
}
