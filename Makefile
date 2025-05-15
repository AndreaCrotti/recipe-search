unit-test:
	./bin/kaocha

lint:
	clj-kondo --lint src test


end-to-end-test:
	./bin/end_to_end
