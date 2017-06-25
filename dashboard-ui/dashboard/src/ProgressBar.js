import 'bootstrap/dist/css/bootstrap.css';
import React, { Component } from 'react';

class ProgressBar extends Component {
  render() { 

    const outside = {
      position: 'relative',        
      marginBottom: '5em',
      marginTop: '5em',
    }
    const style = {
      marginTop: '10px',
      height: '5px',
      width: '100%',
      background: '#cacaca',
    }

    const innerStyle = {
      transition: 'width 1s',
      background: 'rgb(99, 218, 9)',
      height: '100%',
      width: `${this.props.appArr.length / 10 * 100}%`,
    }

    const circle = {
        position: 'absolute',
        marginTop: '-5px',
        borderRadius: '100%',
        background: 'rgb(99, 218, 9)',
        height: '14px',
        width: '14px',
    }

    const circleLast = {
      ...circle, 
        marginTop: '-10px',
      background: '#cacaca', 
      border: 'rgb(99, 218, 9) solid 5px', 
      height: '25px', 
      width: '25px'}

    const points = this.props.appArr.map((app, i) => {
      const marginLeft = (i === this.props.appArr.length - 1) ? `${10 * (.99 + i)}%` :  `${10 * (1 + i)}%`;
      return (i === this.props.appArr.length - 1) ? <div key={i} style={{...circleLast, marginLeft}} /> :  <div key={i} style={{...circle, marginLeft}} />
    })

    return (
        <div style={outside}>
            {points.map(stuff => stuff )}
            <div style={style}>
                <div style={innerStyle} >

                </div>
            </div>
        </div>
  );
  }
}

export default ProgressBar;