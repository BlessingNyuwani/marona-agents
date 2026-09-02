# Python examples

## Requirements

- Python 3.11+
- A required Marona Developer Key from <https://platform.marona.ai>

## Install

```bash
python -m venv .venv
source .venv/bin/activate
python -m pip install -e '.[dev]'
cp ../../.env.example .env
```

Set `MARONA_API_KEY=mr_live_xxxxx` in `.env` using your real developer key,
export the values, then run one example. The placeholder is not a real key and
must not be committed with a real value.

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
