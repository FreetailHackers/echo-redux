import 'bootstrap/dist/css/bootstrap.css';
import React, { Component } from 'react';
import AppCard from './AppCard';
import ProgressBar from './ProgressBar';
import './App.css';
const io = require('socket.io-client')  

class App extends Component {
  

  constructor(props) {
    super(props)
    const socket = io()
    this.state = {apps: []}
    socket.on('data', (payload) => {   
      this.updateCodeFromSockets(payload)
    })
  }

  updateCodeFromSockets(payload) {
    if(payload.data.length > 0) {
      console.log(payload)
      this.setState({apps: payload.data})
    }
  }

  render() { 

    return (
      <div className='container'>
        <ProgressBar appArr={this.state.apps} />
        {this.state.apps.length === 0 ? <div>Nothing Yet</div> :  this.state.apps.map((bruh, index) => <AppCard app={bruh} index={index} />) }
      </div>
  );
  }
}

export default App;