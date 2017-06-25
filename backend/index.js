var bodyParser = require('body-parser');
var request = require('request');
var sentiment = require('sentiment');

const port = process.env.PORT || 4000;
var http = require('http');
var express = require('express');
var app = express();
var httpServer = http.Server(app);
var io = require('socket.io')(httpServer);

app.use(express.static('public'))
app.use('/', express.static(__dirname + '/public'));
app.use(bodyParser.json());

var OLD = {
      name: 'Spotify',
      description: `I can hear you compiling from the other side - Adele.js`,
      img: 'https://upload.wikimedia.org/wikipedia/commons/thumb/1/19/Spotify_logo_without_text.svg/2000px-Spotify_logo_without_text.svg.png',
      index: '1',
      total: '28',
};

var apps = [];

var data = {
    str: "",
    complete: false
};



io.on('connection', function(socket){
  console.log('a user connected');
   io.emit('data', {data: apps});
});

app.post('/start', function(req, res) {
    console.log(req.body);
    data.str = req.body.str;
    data.complete = false;

    // start calling APIs

    res.end();
});

app.post('/update', function(req, res) {
    console.log('update: ' + JSON.stringify(req.body));
    if (req.body.complete) {
        data.str = req.body.data;
        data.complete = true;
    }
    const ap = req.body;       
    apps = [...apps, ap];
    io.emit('data', {data: apps});
    res.json({apps}); 
});

app.get('/end', function(req, res) {
    res.json(data);
    data.str = "";
    data.complete = false;
});

app.post('/sentiment', function(req, res) {
    var text = req.body.str;
    var textSentiment = sentiment(text).comparative;
    if (Math.abs(textSentiment) <= 0.1) {
        text += ' :|';
    } else if (textSentiment < 0) {
        text += ' :(';
    } else {
        text += ' :)';
    }

    var update = {
        name: 'Sentiment Analysis',
        img: 'http://www.polyvista.com/blog/wp-content/uploads/2015/06/sentiment-customer-exp-large.png',
        data: text,
        description: "Sentiment analysis finished: Score: " + textSentiment + "."
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

    res.end();
});



httpServer.listen(port, function() {
    console.log('Node app is running on port', port);
});
