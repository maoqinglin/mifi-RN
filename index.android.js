/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
    AppRegistry,
} from 'react-native';

var WifiApProject = require('./app/pages/index.js');
//var WifiApProject = require('./app/pages/user-manager.js');
AppRegistry.registerComponent('WifiApProject', () => WifiApProject);
