#!/bin/bash

function echov2() {
  curl -H "Content-Type: application/json" -X POST -d "$(ruby jsonify.rb $@)" http://localhost:5000/start > /dev/null 2>&1
  go run poll.go
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
