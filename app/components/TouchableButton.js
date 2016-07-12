/**
 * Created by lmq on 2016/6/30.
 */
'use strict';
import React, { Component } from 'react';
import {
    AppRegistry,
    StyleSheet,
    Text,
    View,
    TouchableHighlight,
} from 'react-native';

class TouchableButton extends Component {
    render() {
        return (
            <TouchableHighlight
                underlayColor={this.props.underlayColor}
                activeOpacity={0.5}
                style={this.props.style}
            >
                <Text style={{fontSize:16,color:'#fff'}}>{this.props.text}</Text>
            </TouchableHighlight>
        );
    }
}
module.exports = TouchableButton;