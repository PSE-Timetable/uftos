FROM node:iron-alpine3.18@sha256:53108f67824964a573ea435fed258f6cee4d88343e9859a99d356883e71b490c

USER node
WORKDIR /app
COPY --chown=node:node package*.json ./
RUN npm ci
COPY --chown=node:node . .
EXPOSE 3000
#CMD ["node ./node_modules/.bin/vite dev --host 0.0.0.0 --port 3000"]
ENTRYPOINT ["npm", "run", "dev"]
