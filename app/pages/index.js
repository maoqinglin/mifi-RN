/**
 * Created by lmq on 2016/7/5.
 */
import React, { Component } from 'react';
import {
    AppRegistry,
    StyleSheet,
    View,
    Text,
    TouchableOpacity,
    Navigator,
} from 'react-native';

var HomeView = require('./home.js');
var SettingsView = require('./wifiap-settings.js');

var WifiApProject = React.createClass({
    configureScenceAndroid: function () {
        return Navigator.SceneConfigs.FadeAndroid;
    },
    renderSceneAndroid: function (route, navigator) {
        let Page = route.page;
        if (route.page) {
            return (
                <Page navigator={navigator} route={route}/>
            );
        }
    },
    render: function () {
        var defaultPage = HomeView;
        var configureScence = this.configureScenceAndroid;
        var renderScene = this.renderSceneAndroid;
        return (
            <Navigator
                debugOverlay={false}
                initialRoute={{ name: 'home', page:defaultPage}}
                configureScence={configureScence }
                renderScene={renderScene}
            />
        );
    }
});

var styles = StyleSheet.create({
    button: {
        height: 56,
        margin: 10,
        backgroundColor: '#cad6c5',
        justifyContent: 'center',
        alignItems: 'center',
    },
});

module.exports = WifiApProject