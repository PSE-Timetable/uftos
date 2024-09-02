dev:
	docker compose -f ./docker-compose.dev.yml up
dev-update:
	docker compose -f ./docker-compose.dev.yml up --build -V
test:
	cd ./server && ./mvnw -Dtest='!de.uftos.e2e.**,!UftosApplicationTests' test
test-stupid:
	cd ./server && mvnw.cmd -Dtest="!de.uftos.e2e.**,!UftosApplicationTests" test
e2e:
	cd ./server && ./mvnw -Dtest='UftosApplicationTests' test
e2e-stupid:
	cd ./server && mvnw.cmd -Dtest="UftosApplicationTests" test
