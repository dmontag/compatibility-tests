#!/bin/sh

for version in {1.2.M01,1.2.M02,1.2.M03,1.2.M04,1.2.M05,1.2.M06,1.2,1.3.M01,1.3.M02}; do
  ant generate-test-graph -Dneo4j.version=$version
  if [ ! $? -eq 0 ]; then
    echo "Version $version failed."
    exit
  fi
done
