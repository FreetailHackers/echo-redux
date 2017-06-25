from flask import Flask
import json
import requests
import time

app = Flask(__name__)

ngrok_url = None
while ngrok_url is None:
    time.sleep(.5)
    tunnels = requests.get('http://localhost:4040/api/tunnels').json()['tunnels']
    if tunnels:
        ngrok_url = tunnels[0].get('public_url')

@app.route("/")
def main():
    return render_template('index.html')
