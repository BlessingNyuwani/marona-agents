# Python examples

## Requirements

- Python 3.11+
- A Marona API key from <https://platform.marona.ai>

## Install

```bash
python -m venv .venv
source .venv/bin/activate
python -m pip install -e '.[dev]'
cp ../../.env.example .env
```

Export the values from `.env`, then run one example:

```bash
python -m marona_agents_examples basic-agent
python -m marona_agents_examples multi-agent
python -m marona_agents_examples bring-your-own-agent
python -m marona_agents_examples realtime-agent
python -m marona_agents_examples chat-server
```

The Chat UI backend listens on `http://127.0.0.1:8080` by default.

## Verify

```bash
ruff check .
mypy
pytest
```
