mvn := `if command -v mvnd &> /dev/null; then echo mvnd; else echo mvn; fi`

ignored-versions := "'-Dmaven.version.ignore=.*\\.Beta\\d*,.*\\.android\\d*'"

# lists all recipes
@recipes:
  just --list

# install all without build cache
no-cache:
  {{mvn}} clean install -Dmaven.build.cache.skipCache=true

# apply spotless format
format:
  {{mvn}} spotless:apply -Dmaven.build.cache.skipCache=true

# show all available updates
updates:
  {{mvn}} -q versions:display-plugin-updates -Dversions.outputFile=updates.txt && cat updates.txt */updates.txt */*/updates.txt | grep -- "->" | sort | uniq
  {{mvn}} -q versions:display-property-updates {{ignored-versions}} -Dversions.outputFile=updates.txt && cat updates.txt */updates.txt */*/updates.txt | grep -- "->" | sort | uniq
  {{mvn}} -q versions:display-dependency-updates {{ignored-versions}} -Dversions.outputFile=updates.txt && cat updates.txt */updates.txt */*/updates.txt | grep -- "->" | sort | uniq
  rm updates.txt */updates.txt */*/updates.txt