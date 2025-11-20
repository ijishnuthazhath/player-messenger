#!/bin/bash

## Usage:
## ./run_players_single_pid.sh actor1, actor2
##
## Example usage:
## ./run_players_single_pid.sh initiator, receiver

# Navigate to project root
cd "$(dirname "$0")/.." || exit 1

echo "Building project..."
mvn clean compile -q

echo "Creating player system running on posrt $1 with actors $2"
mvn exec:java -Dexec.mainClass="org.pm.MainSinglePID" -Dexec.args="$1 $2" -q
