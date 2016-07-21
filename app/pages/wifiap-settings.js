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
    TextInput,
    ToastAndroid,
    BackAndroid,
    PixelRatio,
} from 'react-native';
import {
    Button,
} from 'carbon-native';
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
            focused: false,
        };
    },
    render: function () {
        return (
            <View style={Styles.container}>
                <View style={Styles.rowContainer}>
                    <View style={Styles.row}>
                        <Text
                            numberOfLines={1}
                            style={Styles.textInputTitleInline}
                        >热点名称</Text>
                        <TextInput
                            ref='input'
                            style={Styles.textInputInline}
                            onFocus={this.onFocus}
                            onBlur={this.onBlur}
                            onChangeText={(text) => {
                                this.setState({name:text});
                            }}
                            value={this.state.name}
                        />
                    </View>
                    {this._renderUnderline()}
                </View>

                <View style={Styles.rowContainer}>
                    <View style={Styles.row}>
                        <Text
                            numberOfLines={1}
                            style={Styles.textInputTitleInline}
                        >热点密码</Text>
                        <TextInput
                            ref='input'
                            style={Styles.textInputInline}
                            secureTextEntry={true}
                            onFocus={this.onFocus}
                            onBlur={this.onBlur}
                            onChangeText={(text) => {
                                this.setState({passwd:text});
                            }}
                            value={this.state.passwd}
                        />
                    </View>
                    {this._renderUnderline()}
                </View>
                <View style={Styles.rowContainer}>
                    <View style={Styles.row}>
                        <Text
                            numberOfLines={1}
                            style={Styles.textInputTitleInline}
                        >加密方式</Text>
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
                </View>

                <View style={Styles.container_btn}>
                    <Button color="secondary" text="保存"
                            onPress={() => {this.saveConfig(this.state.name,this.state.passwd,this.state.security)}}/>
                </View>
            </View>
        );
    },
    _renderUnderline() {
        if (this.state.focused === false) {
            return (
                <View
                    style={[Styles.underline, Styles.underlineIdle]}
                />
            );
        }
        return (
            <View
                style={[Styles.underline, Styles.underlineFocused]}
            />
        );
    },
    onFocus() {
        this.setState({
            focused: true,
        });
        /* let oldText = this.state.value;
         let newText = this.props.onTextInputFocus(this.state.value);
         if (newText !== oldText) {
         this._onChange(newText);
         }*/
    },

    onBlur() {
        this.setState({
            focused: false,
        });
        //this.props.onTextInputBlur(this.state.value);
    },
    handleValueChange(values) {
        console.log('handleValueChange', values)
        this.setState({form: values})
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
        paddingTop: 30,
        backgroundColor: '#F5FCFF',
    },
    picker: {
        flex:1
    },
    container_btn: {
        width: 280,
        marginTop: 30,
        alignSelf: 'center',
    },
    rowContainer: {
        backgroundColor: '#FFF',
        borderBottomWidth: 1 / PixelRatio.get(),
        borderColor: '#c8c7cc',
        paddingLeft: 20,
    },
    row: {
        flexDirection: 'row',
        height: 44,
        alignItems: 'center',
    },
    textInputTitleInline: {
        width: 110,
        fontSize: 15,
        color: '#000',
        paddingLeft: 10,
    },
    textInputInline: {
        fontSize: 15,
        flex: 1,
        height: 40,// @todo should be changed if underlined
        marginTop: 2,
    },
    titleContainer: {
        paddingTop: 10,
        flexDirection: 'row',
        alignItems: 'center',
    },
    textInputTitle: {
        fontSize: 13,
        color: '#333',
        paddingLeft: 10,
        flex: 1
    },
    textInput: {
        fontSize: 15,
        flex: 1,
        height: 40,
        marginLeft: 40,
    },
});

module.exports = SettingsView;

