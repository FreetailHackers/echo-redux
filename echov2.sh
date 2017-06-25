#!/bin/bash

python3 -c "print('starting ngrok...')"
ngrok http 5000 > /dev/null &
cat ngrok_started.txt

cd backend
source venv/bin/activate
python3 app.py
cd ..

kill -9 $(pidof ngrok)
