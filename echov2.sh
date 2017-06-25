#!/bin/bash

function echov2() {
  source .env
  docker run -d --env-file .env training >/dev/null 2>&1
  curl -H "Content-Type: application/json" -X POST -d "$(ruby jsonify.rb $@)" ${BASE_URL}start > /dev/null 2>&1
  go run poll.go ${BASE_URL}
}

if [ $# -eq 0 ]
then
  while read line
  do
    echov2 "$line"
  done
else
  echov2 $@
fi
