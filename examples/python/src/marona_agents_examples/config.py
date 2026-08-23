"""Shared environment configuration for the Python examples."""

from __future__ import annotations

import os


def required_api_key() -> str:
    """Return the configured API key without ever logging it."""

    value = os.getenv("MARONA_API_KEY", "").strip()
    if not value:
        raise RuntimeError(
            "MARONA_API_KEY is required. Create one at https://platform.marona.ai."
        )
    return value


def selected_model() -> str:
    """Return the provider-neutral model contract used by the examples."""

    return os.getenv("MARONA_MODEL", "marona/auto").strip() or "marona/auto"


def selected_app() -> str | None:
    """Return an optional public or developer-owned App slug."""

    return os.getenv("MARONA_APP_SLUG", "").strip() or None
