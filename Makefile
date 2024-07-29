.PHONY: help clean

help: ## displays this help message.
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "\033[36m%-15s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)


clean: ## clean useless files
	mvn clean
	find . -name '*.log' -exec rm -f {} +

pack: ## zip source code and move to Desktop
	zip -r vulnjava.zip pom.xml src
	mv vulnjava.zip ~/Desktop/

run: ## run this application
	mvn spring-boot:run

dep: ## list dependency tree
	mvn dependency:tree
