#! /bin/bash

wget http://localhost:8080/v3/api-docs -O uftos-openapi-specs.json
npx oazapfts --optimistic --arguentStyle=object --useEnumType uftos-openapi-specs.json web/src/lib/sdk/fetch-client.ts

