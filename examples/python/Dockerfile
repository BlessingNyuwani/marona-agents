# syntax=docker/dockerfile:1
FROM python:3.12-slim AS builder

ENV PIP_DISABLE_PIP_VERSION_CHECK=1 \
    PIP_NO_CACHE_DIR=1
WORKDIR /build

COPY examples/python/pyproject.toml ./
COPY examples/python/src ./src
RUN python -m pip wheel --wheel-dir /wheels .

FROM python:3.12-slim AS runtime

ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1 \
    CHAT_UI_DIR=/app/chat-ui
RUN groupadd --system --gid 10001 marona \
    && useradd --system --uid 10001 --gid marona --home-dir /app marona
WORKDIR /app

COPY --from=builder /wheels /wheels
RUN python -m pip install --no-cache-dir /wheels/* \
    && rm -rf /wheels
COPY apps/chat-ui ./chat-ui
RUN mkdir -p /app/.marona \
    && chown -R marona:marona /app

USER 10001:10001
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD python -c "import urllib.request; urllib.request.urlopen('http://127.0.0.1:8080/health', timeout=2)"
ENTRYPOINT ["python", "-m", "marona_agents_examples"]
CMD ["chat-server"]
