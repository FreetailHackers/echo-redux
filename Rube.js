var WebClient = require('@slack/client').WebClient;
var request = require('request');

var token = 'xoxb-203241580325-2UTfqko1hXlFWYDc8KkLKXjd';

var web = new WebClient(token);
var otherbot = '';
var message;

web.groups.history('G5YCZV06L').then((history) => {
	if(history.messages[0].user != otherbot){			//find out user id
		message = history.messages[0].text;
		respond(message);
	}
});

function respond(message){
	web.chat.postMessage('boh4', message, function(err, res) {
		  if (err) {
			console.log('Error:', err);
		  } else {
			console.log('Message sent: ', message);
		  }
		
		//update to Heroku
		var update = {
		name: 'Slack',
		img: 'https://www.greenmellenmedia.com/wp-content/uploads/slack-chat.png',
		data: message,
		description: "Slack bot is conversating."
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
});

}
