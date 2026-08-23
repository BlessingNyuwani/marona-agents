const form = document.querySelector("#composer");
const input = document.querySelector("#message");
const conversation = document.querySelector("#conversation");
const welcome = document.querySelector("#welcome");
const submit = form.querySelector("button[type='submit']");
const sessionId = crypto.randomUUID();

function resizeComposer() {
  input.style.height = "auto";
  input.style.height = `${Math.min(input.scrollHeight, 180)}px`;
}

function addMessage(role, text) {
  const article = document.createElement("article");
  article.className = `message ${role}`;
  if (role === "assistant") {
    const avatar = document.createElement("span");
    avatar.className = "avatar";
    avatar.textContent = "M";
    avatar.setAttribute("aria-hidden", "true");
    article.append(avatar);
  }
  const bubble = document.createElement("div");
  bubble.className = "bubble";
  bubble.textContent = text;
  article.append(bubble);
  conversation.append(article);
  article.scrollIntoView({ behavior: "smooth", block: "end" });
  return bubble;
}

async function sendMessage(message) {
  welcome.hidden = true;
  addMessage("user", message);
  const pending = addMessage("assistant", "Thinking…");
  submit.disabled = true;
  try {
    const response = await fetch("/api/chat", {
      method: "POST",
      headers: { "content-type": "application/json" },
      body: JSON.stringify({ message, userId: "chat-ui-user", sessionId }),
    });
    const payload = await response.json();
    if (!response.ok) throw new Error(payload.error || "The request could not be completed.");
    pending.textContent =
      typeof payload.output === "string" ? payload.output : JSON.stringify(payload.output, null, 2);
  } catch (error) {
    pending.textContent = error instanceof Error ? error.message : "The request could not be completed.";
  } finally {
    submit.disabled = false;
    input.focus();
  }
}

form.addEventListener("submit", (event) => {
  event.preventDefault();
  const message = input.value.trim();
  if (!message || submit.disabled) return;
  input.value = "";
  resizeComposer();
  void sendMessage(message);
});

input.addEventListener("input", resizeComposer);
input.addEventListener("keydown", (event) => {
  if (event.key === "Enter" && !event.shiftKey) {
    event.preventDefault();
    form.requestSubmit();
  }
});

for (const suggestion of document.querySelectorAll("[data-prompt]")) {
  suggestion.addEventListener("click", () => void sendMessage(suggestion.dataset.prompt));
}
