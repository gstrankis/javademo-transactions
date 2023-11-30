#!/bin/bash

set -e

. key

echo "APILAYER_KEY=$APILAYER_KEY"
mkdir -pv ./data

# USD,EUR,GBP,JPY,CHF,CAD,MXN,PLN,SEK,NOK,RON,CZK,DKK
# NB! API seems unreliable, lots of request 524 Timedout response; also ignores base currency

curl -X GET 'https://api.apilayer.com/currency_data/live?base=USD&symbols=EUR,GBP,JPY,CHF,CAD,MXN,PLN,SEK' \
  -H "apikey: $APILAYER_KEY" \
  --fail \
  -s -w "\nresponse status: %{http_code}\n" \
  -o data/live_usd.json

echo "live?base=USD"
cat data/live_usd.json

curl -X GET 'https://api.apilayer.com/currency_data/live?base=EUR&symbols=USD,GBP,JPY,CHF,CAD,MXN,PLN,SEK' \
  -H "apikey: $APILAYER_KEY" \
  --fail \
  -s -w "\nresponse status: %{http_code}\n" \
  -o live_eur.json
  
echo "live?base=EUR"
cat data/live_eur.json
echo ""

