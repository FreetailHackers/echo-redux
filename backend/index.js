var http = require('http');
var app = require('express')();
var bodyParser = require('body-parser');
var httpServer = http.Server(app);
var io = require('socket.io')(httpServer);

app.set('port', (process.env.PORT || 5000));
app.use(express.static(__dirname + '/public'));
app.use(bodyParser.json());

app.post('/update', function(req, res) {
    console.log(req.body);
});

app.listen(app.get('port'), function() {
    console.log('Node app is running on port', app.get('port'));
});
