import 'bootstrap/dist/css/bootstrap.css';
import React, { Component } from 'react';

export default class Img extends Component {

  render() {
  
    const cssStyle = {
      width: '50%',
      margin: '15% 25%',
    }

    return (
      <div>
          <img src={this.props.src} alt="" style={cssStyle}/>
      </div>
    );
  }
}