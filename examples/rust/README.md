# Rust Marona Agent

This example uses the official [`marona`](https://crates.io/crates/marona)
Rust client from crates.io.

```bash
export MARONA_API_KEY=replace_with_your_marona_api_key
cargo run -- "Help me plan a production readiness review"
```

The example connects the account's approved Hub capabilities and runs one
provider-neutral Marona Agent turn. No local SDK path or Git dependency is
required.
