#!/bin/bash

set -e

. key

echo "EXCHANGERATE_KEY=$EXCHANGERATE_KEY"

mkdir -pv ./data

set -x 

# https://exchangerate.host/documentation
# USD,EUR,GBP,JPY,CHF,CAD,MXN,PLN,SEK,NOK,RON,CZK,DKK

curl -X GET "http://api.exchangerate.host/live?access_key=$EXCHANGERATE_KEY&source=USD&currencies=USD,EUR,GBP,JPY,CHF,CAD,MXN,PLN,SEK,NOK,RON,CZK,DKK" \
  --fail \
  -s -w "\nresponse status: %{http_code}\n" \
  -o data/live_usd.json

cat data/live_usd.json
  
curl -X GET "http://api.exchangerate.host/live?access_key=$EXCHANGERATE_KEY&source=EUR&currencies=USD,EUR,GBP,JPY,CHF,CAD,MXN,PLN,SEK,NOK,RON,CZK,DKK" \
  --fail \
  -s -w "\nresponse status: %{http_code}\n" \
  -o data/live_eur.json
  
cat data/live_eur.json

echo ""

