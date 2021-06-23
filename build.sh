#!/bin/bash
mvn clean package -DskipTests=true -Dcheckstyle.skip=true -Dspotbugs.skip=true -Dmaven.javadoc.skip=true -Dasciidoctor.skip=true -Dclirr.skip=true
