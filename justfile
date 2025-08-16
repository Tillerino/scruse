mvn := `if command -v mvnd &> /dev/null; then echo mvnd; else echo mvn; fi`

updates-flags := "-q '-Dmaven.version.ignore=.*\\.Beta\\d*,.*\\.android\\d*,.*-M\\d' -Dversions.outputFile=updates.txt -Dversions.outputLineWidth=1000 -P release"

# lists all recipes
@recipes:
  just --list

# apply spotless format
format:
  {{mvn}} -q spotless:apply

# install quickly. mvnd does not resolve the processor correctly unless it is installed
install:
  {{mvn}} clean spotless:apply install -pl scruse-processor -am
  {{mvn}} clean spotless:apply install -pl scruse-tests -amd

# show all available updates
updates:
  {{mvn}} versions:display-plugin-updates {{updates-flags}} && { grep -- "->" updates.txt */updates.txt */*/updates.txt | sed 's/\.\+/./g'; }
  {{mvn}} versions:display-property-updates {{updates-flags}} && { grep -- "->" updates.txt */updates.txt */*/updates.txt | sed 's/\.\+/./g'; }
  {{mvn}} versions:display-dependency-updates {{updates-flags}} && { grep -- "->" updates.txt */updates.txt */*/updates.txt | sed 's/\.\+/./g'; }
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

release:
    # format so that we fail earlier if there are issues (release plugin will notice dirty working directory)
    just format
    # don't use mvnd here. no need to overoptimize
    mvn release:prepare -DtagNameFormat=@{project.version} '-Darguments=-Dmaven.build.cache.skipCache=true'
    mvn release:perform -P release '-Darguments=-Dmaven.build.cache.skipCache=true --projects scruse-processor --also-make'
