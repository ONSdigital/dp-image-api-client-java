OSSINDEX_ERRORS = "Unable to contact OSS Index|authentication failed|401 Unauthorized|403 Forbidden|429 Too Many Requests|Too many requests|Rate limit|Unknown host|Connection refused|timed out|unreachable|402 Payment Required"

.PHONY: all
all: audit test build lint

.PHONY: audit
audit:
	@echo "🔍 Running OSS Index audit for dp-image-api-client-java"
	@mkdir -p target
	@mvn ossindex:audit > target/ossindex-audit-dp-image-api-client-java.log 2>&1; status=$$?; \
	cat target/ossindex-audit-dp-image-api-client-java.log; \
	[ $$status -eq 0 ] && grep -Eiqn $(OSSINDEX_ERRORS) target/ossindex-audit-dp-image-api-client-java.log && \
		{ echo "❌ OSS Index API/auth/network error (CMS) — see target/ossindex-audit-dp-image-api-client-java.log"; exit 1; }; \
	exit $$status

.PHONY: build
build:
	mvn clean package -Dmaven.test.skip -Dossindex.skip=true

.PHONY: test
test:
	mvn -Dossindex.skip=true test

.PHONY: lint
lint:
	exit
