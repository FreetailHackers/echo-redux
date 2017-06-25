#!/bin/bash

curl -X POST http://echov2.herokuapp.com/start > /dev/null
go run poll.go > text.txt
cat text.txt
rm text.txt
