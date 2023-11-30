#!/bin/bash

set -e

. key

echo "EXCHANGERATE_KEY=$EXCHANGERATE_KEY"
mkdir -pv ./data

set -x 

curl --request GET "http://api.exchangerate.host/list?access_key=$EXCHANGERATE_KEY" \
  -s -w "\nresponse status: %{http_code}\n" \
  -o data/currencies.json

cat data/currencies.json
echo ""
