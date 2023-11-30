#!/bin/bash

set -e

. key

echo "APILAYER_KEY=$APILAYER_KEY"
mkdir -pv ./data

curl --request GET 'https://api.apilayer.com/currency_data/list' \
  -H "apikey: $APILAYER_KEY" \
  -s -w "\nresponse status: %{http_code}\n" \
  -o data/list.json

cat data/list.json
echo ""

