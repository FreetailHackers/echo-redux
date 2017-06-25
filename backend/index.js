var bodyParser = require('body-parser');
var request = require('request');
var sentiment = require('sentiment');

var http = require('http');
var express = require('express');
var app = express();
var httpServer = http.Server(app);
var io = require('socket.io')(httpServer);

app.set('port', (process.env.PORT || 5000));
app.use(express.static(__dirname + '/public'));
app.use(bodyParser.json());

var data = {
    str: "",
    complete: false
};

var cloud = {
    "AWS": {
        start: function() {
            console.log("Starting AWS");
        }
    },
    "GCP": {
        start: function() {
            console.log("Starting GCP");
        }
    },
    "Azure": {
        start: function() {
            console.log("Starting Azure");
        }
    }
};

app.post('/start', function(req, res) {
    console.log(req.body);
    data.str = req.body.str;
    data.complete = false;

    // start calling APIs

    res.end();
});

app.post('/update', function(req, res) {
    console.log('update: ' + JSON.stringify(req.body));
    io.sockets.emit('data', req.body);
    if (req.body.complete) {
        data.str = req.body.data;
        data.complete = true;
    }
    res.end();
});

app.get('/end', function(req, res) {
    res.json(data);
    data.str = "";
    data.complete = false;
});

app.post('/sentiment', function(req, res) {
    var text = req.body.str;
    var textSentiment = sentiment(text).comparative;
    var cloudPlatform = textSentiment < 0 ? "AWS" : "Azure";
    if (Math.abs(textSentiment) <= 0.1) {
        cloudPlatform = "GCP";
    }

    var update = {
        data: "Sentiment analysis finished: Score: " + textSentiment + ". Starting a " + cloudPlatform + " instance.",
        timestamp: new Date().toISOString()
    };
    request({
        url: 'http://echov2.herokuapp.com/update',
        method: "POST",
        json: true,
        body: update
    }, function (err, response, body) {
        if (err) {
            return console.error('update failed!', err);
        }
    });

    cloud[cloudPlatform].start();
    res.end();
});

app.listen(app.get('port'), function() {
    console.log('Node app is running on port', app.get('port'));
});
