/**
 * Created by lmq on 2016/7/5.
 */

import React, { Component } from 'react';
import {
    AppRegistry,
    StyleSheet,
    Picker,
    Text,
    View,
    Image,
    TextInput,
    ToastAndroid,
    BackAndroid,
    TouchableHighlight,
    TouchableOpacity,
} from 'react-native';

var wifiApController = require('../modules/module');
var SECURITY = {
    WPA2_PSK: 4,
    NONE: 0
}
const Item = Picker.Item;

var navigator;
var RCTDeviceEventEmitter = require('RCTDeviceEventEmitter');

BackAndroid.addEventListener('hardwareBackPress', function () {
    if (navigator == null) {
        return false;
    }
    if (navigator.getCurrentRoutes().length === 1) {
        return false;
    }
    navigator.pop();
    return true;
});
var SettingsView = React.createClass({
    getInitialState()
    {
        navigator = this.props.navigator;
        this.getConfig();
        return {
            name: "",
            passwd: "",
            security: "0",
            initState: true,
        };
    },
    render: function () {
        return (
            <View style={Styles.container}>
                <TouchableOpacity onPress={this._onPress}>
                    <Image style={Styles.back}
                           source={require('../images/back.png')}/>
                </TouchableOpacity>
                <View style={Styles.config_container}>
                    <Text style={Styles.textInputTitle}>
                        热点名称
                    </Text>
                    <TextInput
                        style={Styles.textInput}
                        placeholder='SnailGame'
                        numberOfLines={1}
                        autoFocus={true}
                        value={this.state.name}
                        maxLength={limit}
                        underlineColorAndroid={'transparent'}
                        onChangeText={(text) => {
                                this.setState({name:text});
                            }}
                    />
                    <View style={Styles.line}/>
                    <Text style={Styles.textInputTitle}>
                        密码
                    </Text>
                    <TextInput
                        style={Styles.textInput}
                        placeholder=''
                        numberOfLines={1}
                        underlineColorAndroid={'transparent'}
                        value={this.state.passwd}
                        secureTextEntry={true}
                        onChangeText={(text) => {
                                this.setState({passwd:text});
                            }}
                    />
                    <View style={Styles.line}/>
                    <Text style={Styles.textInputTitle}>加密方式</Text>
                    <Picker
                        style={Styles.picker}
                        mode="dropdown"
                        selectedValue={this.state.security.toString()}
                        onValueChange={(value)=>{
                        if(!this.state.initState){
                            this.setState({security:value});
                        }else{
                            this.setState({initState:false});
                        }
                            }}
                    >
                        <Item label="NONE" value="0"/>
                        <Item label="WPA2 PSK" value="4"/>
                    </Picker>
                </View>
                <TouchableHighlight
                    underlayColor='#4169e1'
                    style={Styles.style_view_button}
                    onPress={()=>{
                             this.saveConfig(this.state.name,this.state.passwd,this.state.security);
                            }}>
                    <Text style={Styles.textInput}>保存</Text>
                </TouchableHighlight>
            </View>
        );
        var limit = 20;
    },

    _onPress(){
        var {navigator} = this.props;
        if (navigator) {
            navigator.pop();
        }
    },
    getConfig(){
        wifiApController.getWifiApConfig(
            (name, pwd, security)=> {
                this.setState({
                        name: name,
                        passwd: pwd,
                        security: security
                    }
                );
            }
        )
    },
    saveConfig(name, passwd, security){
        if (this.validate(name, passwd, security)) {
            passwd = (security != SECURITY.NONE ? passwd : null);
            wifiApController.saveWifiApConfig(name, passwd, parseInt(security),
                (isSuccess)=> {
                    if (isSuccess) {
                        ToastAndroid.show("保存成功", ToastAndroid.LONG);
                        var config = {newName: name, newPwd: passwd};
                        RCTDeviceEventEmitter.emit('configChange', config);
                        //保存之后关闭
                        this._onPress();
                    } else {
                        ToastAndroid.show("保存失败", ToastAndroid.LONG);
                    }
                }
            )
        }
    },
    validate(name, passwd, security){
        if (!name) {
            ToastAndroid.show("Wifi热点名称不能为空", ToastAndroid.LONG);
            return false;
        }
        if (security != SECURITY.NONE && (!passwd || passwd.length < 8)) {
            ToastAndroid.show("settings 密码至少为8位", ToastAndroid.LONG);
            return false;
        }
        return true;
    },
});

var Styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#F5FCFF',
    },

    config_container: {
        margin: 50,
    },
    back: {
        margin: 10,
    },
    picker: {
        width: 120,
    },
    style_view_button: {
        height: 56,
        width: 200,
        margin: 20,
        backgroundColor: '#63B8FF',
        borderColor: '#5bc0de',
        borderRadius: 45,
        justifyContent: 'center',
        alignItems: 'center',
        alignSelf: 'center',
    },
    textInputTitle: {
        fontSize: 16,
        color: '#333',
    },
    textInput: {
        fontSize: 18,
    },
    line: {
        height: 2,
        backgroundColor: '#f4f4f4',
        marginTop: -10,
    }
});

module.exports = SettingsView;

