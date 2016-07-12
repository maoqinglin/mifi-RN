/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
    AppRegistry,
} from 'react-native';

//var WifiApProject = require('./app/pages/home.js');
//var WifiApProject = require('./app/pages/wifiap-settings.js');
var WifiApProject = require('./app/pages/index.js');
AppRegistry.registerComponent('WifiApProject', () => WifiApProject);
