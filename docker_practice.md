# [Docker Practice Exercises]

This document contains a series of exercises to help you master Dockerfile syntax and best practices.

## Level 1: The Basics (Node.js)

**Scenario:** You have a simple Express.js application.
**Goal:** Create a `Dockerfile` that:
1. Uses `node:20-alpine` as the base image.
2. Sets the working directory to `/usr/src/app`.
3. Copies only `package.json` and `package-lock.json` first (to leverage layer caching).
4. Runs `npm install`.
5. Copies the rest of the source code.
6. Exposes port `3000`.
7. Starts the app using `node index.js`.

---

## Level 2: Optimization (Multi-stage Builds)

**Scenario:** You have a React application (Vite).
**Goal:** Create a `Dockerfile` that:
1. **Stage 1 (Build):**
    - Uses `node:18` to build the app.
    - Installs dependencies.
    - Runs `npm run build`.
2. **Stage 2 (Production):**
    - Uses `nginx:stable-alpine`.
    - Copies **only** the build artifacts from Stage 1 into the Nginx HTML folder.
    - Includes a custom `nginx.conf` (optional challenge).
    - Exposes port `80`.

---

## Level 3: Security & Production Best Practices

**Scenario:** You want to secure your backend.
**Goal:** Create a `Dockerfile` for a Python (Flask) or Node.js app that:
1. Runs the application as a **non-root user** (e.g., `useradd -m myuser`).
2. Uses `.dockerignore` to exclude `node_modules`, `.git`, and `Dockerfile`.
3. Sets environment variables using `ENV`.
4. Uses `HEALTHCHECK` to ensure the container is healthy.
5. Uses a lightweight base image (like `alpine` or `distroless`).

---

## Level 4: The "Full Stack" Challenge

**Scenario:** You have a Frontend, Backend, and a Database.
**Goal:**
1. Write a `docker-compose.yml` file that links three services.
2. Ensure the Backend waits for the Database to be ready.
3. Pass the Database credentials to the Backend using environment variables.
4. Mount a "volume" for the database so data isn't lost when the container stops.

---

## Challenge: The "Broken" Dockerfile

**Task:** Fix this Dockerfile so it works and is optimized.

```dockerfile
FROM node
COPY . .
RUN npm install
EXPOSE 80
CMD node server.js
```

**What's wrong?** (Hint: Layer caching, base image size, CMD vs RUN)
