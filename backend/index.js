var express =  require('express');
var app = express();
var http = require('http').Server(app);
var io = require('socket.io')(http);
app.use(express.static('public'))

const ap = {
      name: 'Spotify',
      description: `I can hear you compiling from the other side - Adele.js`,
      img: 'https://upload.wikimedia.org/wikipedia/commons/thumb/1/19/Spotify_logo_without_text.svg/2000px-Spotify_logo_without_text.svg.png',
      index: '1',
      total: '28',
    }

    var apps = [];


app.use('/', express.static(__dirname + '/public'));

app.get('/update', (req, res) => {
   apps = [...apps, ap];
   io.emit('data', {data: apps});
   res.json({apps});
})


io.on('connection', function(socket){
  console.log('a user connected');
   io.emit('data', {data: apps});
});

http.listen(4000, function(){
  console.log('listening on *:4000');
});