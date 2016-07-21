/**
 * Created by lmq on 2016/7/5.
 */
import React, { Component } from 'react';
import {
    AppRegistry,
    StyleSheet,
    Navigator,
    TouchableOpacity,
    View,
    Text,
} from 'react-native';

import Icon from 'react-native-vector-icons/MaterialIcons';
var HomeView = require('./home.js');
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
                style={styles.container}
                debugOverlay={false}
                sceneStyle={{ paddingTop: 56 }}
                initialRoute={{ name: 'home', page:defaultPage,title:"WLAN热点管理"}}
                configureScence={configureScence }
                renderScene={renderScene}
                navigationBar={
          <Navigator.NavigationBar
            style={styles.navbar}
            routeMapper={{
              LeftButton: (route, navigator, index) => {
                if (index === 0) {
                  return null;
                }
                return (
                  <TouchableOpacity
                    onPress={() => navigator.pop()}
                    style={styles.navbarBackButton}
                  >
                    <Icon name="arrow-back" size={28} color="#fff" />
                  </TouchableOpacity>
                );
              },
              RightButton: () => null,
              Title: (route) => (
                <View style={styles.navbarTitleWrap}>
                  <Text style={styles.navbarTitle}>
                    {route.title}
                  </Text>
                </View>
              ),
            }}
          />
        }
            />
        );
    }
});

var styles = StyleSheet.create({
    container: {
        flex: 1,
    },
    navbar: {
        backgroundColor: '#3A3A3A',
    },
    navbarTitleWrap: {
        flex: 1,
        flexDirection: 'row',
        alignItems: 'center',
        alignSelf:'center',
        marginLeft:-60,
    },
    navbarTitle: {
        fontSize: 20,
        fontWeight: '600',
        color: '#fff',
    },
    navbarBackButton: {
        paddingHorizontal: 10,
        paddingVertical: 15,
    },
});

module.exports = WifiApProject