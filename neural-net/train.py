from keras.models import Sequential
from keras.layers import Dense
import numpy as np
import requests
import os
from twython import Twython

APP_KEY = os.getenv('APP_KEY')
APP_SECRET = os.getenv('APP_SECRET')
OAUTH_TOKEN = os.getenv('OAUTH_TOKEN')
OAUTH_SECRET = os.getenv('OAUTH_SECRET')

twitter = Twython(APP_KEY, APP_SECRET, OAUTH_TOKEN, OAUTH_SECRET)

tweets = twitter.get_home_timeline()
for tweet in tweets:
    if 'Alexa' in tweet.get('source', ''):
        positive = tweet['text']
        negative = positive + ' '


X = np.array([[1]] * 10)
Y = np.array([1] * 10)

model = Sequential()
model.add(Dense(units=32, input_dim=X.shape[1]))
model.add(Dense(units=32))
model.add(Dense(1, activation='sigmoid'))
model.compile(loss='binary_crossentropy', optimizer='adam', metrics=['accuracy'])

model.fit(X, Y, epochs=1000)

output = positive if model.predict(np.array([1]))[0] > 0.98 else negative

url = os.getenv("UPDATE_URL")
if not url:
    url = "http://localhost:5000/update"
post_data = {
    'data': output.strip(),
    'description': 'A neural network has been trained to repeat the input back to you',
    'complete': True
}
requests.post(url, json=post_data)
