mvn := `if command -v mvnd &> /dev/null; then echo mvnd; else echo mvn; fi`

# lists all recipes
@recipes:
  just --list

# install all without build cache
no-cache:
  {{mvn}} clean install -Dmaven.build.cache.skipCache=true
