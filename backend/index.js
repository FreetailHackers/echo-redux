var http = require('http');
var express = require('express');
var app = express();
var bodyParser = require('body-parser');
var httpServer = http.Server(app);
var io = require('socket.io')(httpServer);

app.set('port', (process.env.PORT || 5000));
app.use(express.static(__dirname + '/public'));
app.use(bodyParser.json());

var data = {
    str: "",
    complete: false
};

app.post('/start', function(req, res) {
    console.log('start');
    data.complete = false;
    res.end();
});

app.post('/update', function(req, res) {
    console.log('update: ' + JSON.stringify(req.body));
    io.sockets.emit('data', req.body);
    if (req.body.complete) {
        data.str = req.body.str;
        data.complete = true;
    }
    res.end();
});

app.get('/end', function(req, res) {
    res.json(data);
    data.str = "";
    data.complete = false;
});

app.listen(app.get('port'), function() {
    console.log('Node app is running on port', app.get('port'));
});
