unit-test:
	./bin/kaocha

lint:
	clj-kondo --lint src test

archive:
	git archive -o andrea_crotti.zip master

end-to-end-test:
	./bin/end_to_end
