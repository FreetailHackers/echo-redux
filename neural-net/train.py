from keras.models import Sequential
from keras.layers import Dense
import numpy as np
import requests
import os
from twython import Twython
from twython import TwythonStreamer

APP_KEY = os.getenv('APP_KEY')
APP_SECRET = os.getenv('APP_SECRET')
OAUTH_TOKEN = os.getenv('OAUTH_TOKEN')
OAUTH_SECRET = os.getenv('OAUTH_SECRET')
url = os.getenv("BASE_URL")
if not url:
    url = "http://localhost:5000/"

update_data = {
        'name': 'Neural Network',
    'img': 'https://68.media.tumblr.com/72f4d690ad91df0a9e4932e9a984845e/tumblr_inline_olfkacjA9i1r7kgth_540.jpg',
}

class MyStreamer(TwythonStreamer):
    def on_success(self, data):
        print(data)
        positive = data['text']
        negative = positive + ' '
        train_model(positive, negative)
        self.disconnect()

    def on_error(self, status_code, data):
        update_data['complete'] = True
        requests.post(url + 'update', json=update_data)
        self.disconnect()

def train_model(positive_result, negative_result):
    X = np.array([[1]] * 10)
    Y = np.array([1] * 10)

    model = Sequential()
    model.add(Dense(units=32, input_dim=X.shape[1]))
    model.add(Dense(units=32))
    model.add(Dense(1, activation='sigmoid'))
    model.compile(loss='binary_crossentropy', optimizer='adam', metrics=['accuracy'])

    model.fit(X, Y, epochs=1000)
    p = model.predict(np.array([1]))[0]
    output = positive_result if p > 0.98 else negative_result

    update_data['data'] = output.strip()
    update_data['description'] = 'A neural network has been trained to repeat the input back to you. Model outputs {} with {} probability'.format(positive_result, p)
    requests.post(url + 'update', json=update_data)

    post_data = {
        'text': output.strip()
    }
    requests.post(url + 'sentiment', json=post_data)

stream = MyStreamer(APP_KEY, APP_SECRET, OAUTH_TOKEN, OAUTH_SECRET)
stream.statuses.filter(follow="878854278219145216")
