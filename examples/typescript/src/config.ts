export function requiredApiKey(): string {
  const value = process.env.MARONA_API_KEY?.trim();
  if (!value) {
    throw new Error("MARONA_API_KEY is required. Create one at https://platform.marona.ai.");
  }
  return value;
}

export function selectedModel(): string {
  return process.env.MARONA_MODEL?.trim() || "marona/auto";
}

export function selectedApp(): string | undefined {
  return process.env.MARONA_APP_SLUG?.trim() || undefined;
}
