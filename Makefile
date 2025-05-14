end-to-end-test:

unit-test:
	./bin/kaocha

lint:
	clj-kondo --lint src test

run:
