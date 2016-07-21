/**
 * Created by lmq on 2016/7/5.
 */
import React, { Component } from 'react';
import {
    StyleSheet,
    SwitchAndroid,
    Text,
    View,
    Image,
    ToastAndroid,
    Navigator,
} from 'react-native';

import {
    carbonStyles,
    Button,
    List,
    Content,
    Item,
    ItemContent,
    ItemText,
    Toggle,
} from 'carbon-native';

const cs = StyleSheet.create(carbonStyles);
var wifiApController = require('../modules/module');
var SettingsView = require('./wifiap-settings.js');
var UserView = require('./user-manager.js');

var PAGE_NAME = {
    SETTING: "settings",
    USER: "usermgt"
}
var RCTDeviceEventEmitter = require('RCTDeviceEventEmitter');

var HomeView = React.createClass({
    getInitialState(){
        this.getState();
        this.getConfig();
        return {
            falseSwitchIsOn: false,
            config: {
                name: "SnailGame",
                passwd: ""
            }
        };
    },
    render: function () {
        return (
            <View style={Styles.container}>
                <Content style={Styles.state_container}>
                    <Image
                        style={Styles.style_image}
                        source={require('../images/wifi.jpg')}/>
                    <List>
                        <Item>
                            <ItemContent>
                                <ItemText>热点名称</ItemText>
                                <ItemText style={cs.textRight}>{this.state.config.name}</ItemText>
                            </ItemContent>
                        </Item>
                        <Item>
                            <ItemContent>
                                <ItemText>热点密码</ItemText>
                                <ItemText style={cs.textRight}>{this.state.config.passwd}</ItemText>
                            </ItemContent>
                        </Item>
                        <Item>
                            <ItemContent style={cs.itemLast}>
                                <ItemText>热点状态</ItemText>
                                <Toggle
                                    color="energized"
                                    onValueChange={(value) =>{
                        if(value){
                            wifiApController.openWifiAp();
                        }else{
                            wifiApController.closeWifiAp();
                        }
                        this.setState({falseSwitchIsOn: value});}
                    }
                                    style={Styles.switch}
                                    value={this.state.falseSwitchIsOn}
                                />
                            </ItemContent>
                        </Item>
                    </List>
                    <View style={Styles.container_btn}>
                        <Button color="secondary" text="热点设置" onPress={() => {this._onPress(PAGE_NAME.SETTING)}}/>
                    </View>
                    <View style={Styles.container_btn}>
                        <Button color="secondary" text="用户管理" onPress={() => {this._onPress(PAGE_NAME.USER)}}/>
                    </View>
                </Content>

            </View>
        )
    },
    getState: function () {
        wifiApController.getWifiApState((state)=> {
            this.setState({falseSwitchIsOn: state})
        });
    },
    componentDidMount(){
        var that = this;
        this.listener = RCTDeviceEventEmitter.addListener('configChange', function (newConfig) {
            if (!newConfig.newPwd) {
                newConfig.newPwd = "无";
            }
            that.setState({
                config: {
                    name: newConfig.newName,
                    passwd: newConfig.newPwd
                }
            })
        });
        this.wifiApListener = RCTDeviceEventEmitter.addListener('wifiState', function (wifistate) {
            that.setState({falseSwitchIsOn: wifistate.state})
        });
    },
    componentWillUnmount(){
        // 移除 一定要写
        this.listener.remove();
        this.wifiApListener.remove();
    },
    getName: function () {
        wifiApController.getWifiApName(
            (wifiApName)=> {
                this.setState({name: wifiApName});
            }
        )
    },
    getConfig(){
        wifiApController.getWifiApConfig(
            (name, pwd, security)=> {
                if (!pwd) {
                    pwd = "无";
                }
                this.setState({
                        config: {
                            name: name,
                            passwd: pwd,
                        }
                    }
                );
            }
        )
    },
    _onPress(action){
        const {navigator} = this.props;
        if (navigator) {
            if (action == PAGE_NAME.SETTING) {
                navigator.push({
                    name: PAGE_NAME.SETTING,
                    page: SettingsView,
                    title: "热点设置"
                })
            } else if (action == PAGE_NAME.USER) {
                navigator.push({
                    name: PAGE_NAME.USER,
                    page: UserView,
                    title: "用户管理"
                })
            }
        }
    },
});

var Styles = StyleSheet.create({
    container: {
        flex: 1,
        alignItems: 'center',
        justifyContent: 'center',
    },
    state_container: {
        flex: 1,
        marginTop: 50,
        width: 300,
        height:120,
    },
    switch: {
        alignSelf: 'center',
        transform: [
            {scaleX: 1.8},
            {scaleY: 1.8},
        ],
    },
    style_image: {
        borderRadius: 45,
        height: 70,
        width: 70,
        alignSelf: 'center',
        marginBottom:30,
    },
    container_btn: {
        width: 280,
        marginTop: 20,
        alignSelf: 'center',
    },
})
module.exports = HomeView;