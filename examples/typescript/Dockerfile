# syntax=docker/dockerfile:1
FROM node:22-alpine AS builder

WORKDIR /build
RUN corepack enable
COPY examples/typescript/package.json examples/typescript/pnpm-lock.yaml ./
RUN pnpm install --frozen-lockfile
COPY examples/typescript/tsconfig.json ./
COPY examples/typescript/src ./src
RUN pnpm build

FROM node:22-alpine AS runtime

ENV NODE_ENV=production \
    CHAT_UI_DIR=/app/chat-ui
WORKDIR /app
RUN corepack enable
COPY examples/typescript/package.json examples/typescript/pnpm-lock.yaml ./
RUN pnpm install --prod --frozen-lockfile
COPY --from=builder /build/dist ./dist
COPY apps/chat-ui ./chat-ui
RUN chown -R node:node /app

USER node
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD wget --quiet --spider http://127.0.0.1:8080/health || exit 1
ENTRYPOINT ["node", "dist/src/cli.js"]
CMD ["chat-server"]
