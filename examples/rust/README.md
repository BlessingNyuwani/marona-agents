# Rust Marona Agent

This example uses the official [`marona`](https://crates.io/crates/marona)
Rust client from crates.io.

```bash
export MARONA_API_KEY=mr_live_xxxxx # placeholder; use your real key locally
cargo run -- "Help me plan a production readiness review"
```

The Marona Developer Key is required for every Runtime operation, including
local, BYOK, private, and self-hosted models. Provider credentials are separate.

The example connects the account's approved Hub capabilities and runs one
provider-neutral Marona Agent turn. No local SDK path or Git dependency is
required.
