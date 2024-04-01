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

# estimate size of shaded libraries
shaded-sizes:
  #!/bin/sh
  cd scruse-tests
  for f in */target/scruse-tests-*.jar; do
    dir=$(dirname $f)
    name=$(basename $f)
    if [ -f $dir/original-$name ]; then
      # size of shaded jar
      size=$(du -b $f | cut -f1)
      # size of generated classes
      orig=$(du -b $dir/original-$name | cut -f1)
      # difference in KiB
      eff=$(($(($size - $orig)) / 1024))

      name=$(dirname $dir)
      name=${name#scruse-tests-}
      echo "$name: $eff KiB"
    fi
  done