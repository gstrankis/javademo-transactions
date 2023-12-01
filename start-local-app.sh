#!/bin/bash

set -e

# load variables from 'env' file
if [[ -f "env" ]]; then
  . ./env
fi

docker compose up --detach

[[ -z "$EXCHANGERATE_KEY" ]] &&  echo "ERROR: EXCHANGERATE_KEY is not set. You may want to create env file to configure one." && exit 1

./gradlew bootRun  --args='--spring.profiles.active=local'

