import 'bootstrap/dist/css/bootstrap.css';
import React, { Component } from 'react';
import { Card, CardTitle, CardText, Row, Col } from 'reactstrap';
import Img from './Img';
import './App.css';

class AppCard extends Component {
  render() {
    
    const textStyle = {textAlign: 'center'}
    return (
        <Row className='floating'>
          <Col md={{ size: 4, offset: 4 }}>
            <Card block >
              <Img src={this.props.app.img}/>
              <CardTitle> {this.props.app.name} </CardTitle>
              <CardText> <i>{this.props.app.description}</i> </CardText>
              <Card block style={textStyle}> 
                <code>
                  {this.props.app.data}
                </code>  
              </Card>
            </Card>
          </Col>
        </Row>
  );
  }
}

export default AppCard;