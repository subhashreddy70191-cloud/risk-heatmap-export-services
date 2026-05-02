FROM node:18-alpine

# Placeholder Dockerfile for Frontend Service
# This allows docker-compose to build without failing while the frontend code is being developed.

WORKDIR /app

# We create a dummy index.js just to keep the container alive.
RUN echo "console.log('Frontend Service is waiting for code...'); setInterval(() => {}, 1000);" > index.js

EXPOSE 3000

CMD ["node", "index.js"]
