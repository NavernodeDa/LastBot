#!/bin/bash

DIR="${1:-./}"

if [ "$1" = "--help" ] || [ "$1" = "-h" ]; then
  echo "Usage: $0 [DIRECTORY]"
  echo "Runs bot via Gradle from the specified directory (default: './')."
  echo
  echo "Arguments:"
  echo "  DIRECTORY   Path to the project containing 'gradlew' (default: current folder)"
  exit 0
fi

printf "\n"
echo -e "\033[1;32m🔍 Searching for Gradlew...\033[0m"

if [ ! -f "$DIR/gradlew" ]; then
  echo -e "\e[1;31mError: 'gradlew' file not found! Maybe use --help?\e[0m"
  exit 1
fi

cd "$DIR" || exit 1

echo -e "\033[1;32m🚀 Running Gradle...\033[0m"
echo -e "\033[1;32m📜 Log:\033[0m"
./gradlew run --quiet