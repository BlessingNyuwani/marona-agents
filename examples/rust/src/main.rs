use marona::{Agent, Marona, Runner};

#[tokio::main]
async fn main() -> marona::Result<()> {
    let api_key = std::env::var("MARONA_API_KEY")
        .expect("MARONA_API_KEY is required for managed Marona services");
    let prompt = std::env::args().skip(1).collect::<Vec<_>>().join(" ");
    let prompt = if prompt.trim().is_empty() {
        "Explain how Marona Agents use connected capabilities.".to_owned()
    } else {
        prompt
    };

    let marona = Marona::new(api_key)?;
    let tools = marona.hub.connect_all().await?;
    let agent = Agent::new(&marona, "Rust Customer Assistant")
        .model("marona/auto")
        .instructions("Help directly and use connected capabilities only when needed.")
        .hub_tools(tools);

    let result = Runner::run(
        &agent,
        prompt,
        Some("rust-example-user"),
        Some("rust-example-session"),
    )
    .await?;
    println!("{}", result.output);
    Ok(())
}
