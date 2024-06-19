#!/bin/bash

if [[ -z JAVA_HOME ]]; then
    echo "You must set JAVA_HOME before using this script."
    exit 0
fi

MVN_ARGS="-Dlicense.skipDownloadLicenses=true -DskipTests=true -Dcheckstyle.skip=true -Dspotbugs.skip=true -Dasciidoctor.skip=true -Dclirr.skip=true  -Dmaven.plugin.validation=brief"

if [[ `uname` == "MINGW64"* ]]; then
    echo Building on Windows, so no documentation bundle
    MVN_ARGS="-Dmaven.javadoc.skip=true $MVN_ARGS"
elif [[ "$OSTYPE" == "darwin"* ]]; then
    echo Building on macOS, so include the documentation bundle...
else
    echo Cannot determine OS type - aborting build.
fi

mvn clean package $MVN_ARGS
