"""Dependency-light HTTP adapter for the shared Chat UI."""

from __future__ import annotations

import json
import os
from http import HTTPStatus
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path
from typing import Any

from marona import Runner

from .basic_agent import build_agent


class ChatApplication:
    """Own one runtime and reuse it across isolated session identifiers."""

    def __init__(self) -> None:
        self.marona, self.agent = build_agent()

    def chat(self, message: str, user_id: str, session_id: str) -> Any:
        result = Runner.run_sync(
            self.agent,
            message,
            user_id=user_id,
            session_id=session_id,
        )
        return result.output


def create_handler(
    application: ChatApplication,
    ui_directory: Path,
) -> type[BaseHTTPRequestHandler]:
    resolved_ui_directory = ui_directory.resolve()

    class Handler(BaseHTTPRequestHandler):
        server_version = "MaronaAgentExample/1.0"

        def do_GET(self) -> None:  # noqa: N802
            if self.path == "/health":
                self._json(HTTPStatus.OK, {"status": "ok"})
                return
            requested = "index.html" if self.path in {"", "/"} else self.path.lstrip("/")
            candidate = (resolved_ui_directory / requested).resolve()
            outside_ui_directory = resolved_ui_directory not in candidate.parents
            if outside_ui_directory and candidate != resolved_ui_directory:
                self._json(HTTPStatus.NOT_FOUND, {"error": "not_found"})
                return
            if not candidate.is_file():
                self._json(HTTPStatus.NOT_FOUND, {"error": "not_found"})
                return
            content_type = "text/html" if candidate.suffix == ".html" else "text/plain"
            if candidate.suffix == ".css":
                content_type = "text/css"
            elif candidate.suffix == ".js":
                content_type = "text/javascript"
            body = candidate.read_bytes()
            self.send_response(HTTPStatus.OK)
            self.send_header("Content-Type", f"{content_type}; charset=utf-8")
            self.send_header("Content-Length", str(len(body)))
            self.end_headers()
            self.wfile.write(body)

        def do_POST(self) -> None:  # noqa: N802
            if self.path != "/api/chat":
                self._json(HTTPStatus.NOT_FOUND, {"error": "not_found"})
                return
            try:
                length = int(self.headers.get("Content-Length", "0"))
                if length <= 0 or length > 32_768:
                    raise ValueError("Request body must be between 1 byte and 32 KiB.")
                payload = json.loads(self.rfile.read(length))
                message = str(payload.get("message", "")).strip()
                if not message:
                    raise ValueError("message is required")
                output = application.chat(
                    message,
                    str(payload.get("user_id", "example-user")),
                    str(payload.get("session_id", "chat-ui-session")),
                )
                self._json(HTTPStatus.OK, {"output": output})
            except ValueError as error:
                self._json(HTTPStatus.BAD_REQUEST, {"error": str(error)})
            except Exception:
                self._json(HTTPStatus.INTERNAL_SERVER_ERROR, {"error": "request_failed"})

        def log_message(self, format: str, *args: object) -> None:
            print(f"{self.address_string()} - {format % args}")

        def _json(self, status: HTTPStatus, payload: dict[str, Any]) -> None:
            body = json.dumps(payload, default=str).encode()
            self.send_response(status)
            self.send_header("Content-Type", "application/json; charset=utf-8")
            self.send_header("Content-Length", str(len(body)))
            self.end_headers()
            self.wfile.write(body)

    return Handler


def main() -> None:
    application = ChatApplication()
    repository_root = Path(__file__).resolve().parents[4]
    ui_directory = Path(os.getenv("CHAT_UI_DIR", repository_root / "apps/chat-ui"))
    port = int(os.getenv("PORT", "8080"))
    server = ThreadingHTTPServer(("0.0.0.0", port), create_handler(application, ui_directory))
    print(f"Marona Chat UI available at http://127.0.0.1:{port}")
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        pass
    finally:
        server.server_close()
        application.marona.close()


if __name__ == "__main__":
    main()
