/**
 * Created by lmq on 2016/7/13.
 */
import React, { Component } from 'react';
import {
    AppRegistry,
    StyleSheet,
    SwitchAndroid,
    Text,
    View,
    Image,
    ToastAndroid,
    TouchableHighlight,
    TouchableOpacity,
    Navigator,
    ListView,
    BackAndroid,
} from 'react-native';

import {
    carbonStyles,
    Button,
    Content,
    Item,
    ItemContent,
    ItemText,
} from 'carbon-native';

const cs = StyleSheet.create(carbonStyles);

var wifiApController = require('../modules/module');
var navigator;
/*var array = [{"macAddress": "ab:dd:dd:dd:dd:aa", "isBlocked": false}, {
    "macAddress": "cc:dd:dd:dd:dd:aa",
    "isBlocked": false
}, {"macAddress": "ee:dd:dd:dd:dd:aa", "isBlocked": false}, {
    "macAddress": "ff:dd:dd:dd:dd:aa",
    "isBlocked": false
}, {"macAddress": "gg:dd:dd:dd:dd:aa", "isBlocked": true}];*/
var RCTDeviceEventEmitter = require('RCTDeviceEventEmitter');
var UserView = React.createClass({

    getInitialState(){
        navigator = this.props.navigator;
        var getSectionData = (dataBlob, sectionID) => {
            return dataBlob[sectionID];
        };
        var getRowData = (dataBlob, sectionID, rowID) => {
            return dataBlob[sectionID + ':' + rowID];
        };
        var ds = new ListView.DataSource({
            getRowData: getRowData,
            getSectionHeaderData: getSectionData,
            rowHasChanged: (r1, r2)=>r1 != r2,
            sectionHeaderHasChanged: (s1, s2) => s1 !== s2
        });
        return {
            dataSource: ds,
        }
    },
    componentDidMount() {
        //获取用户数据
        this.getUser();
        var that = this;
        this.listener = RCTDeviceEventEmitter.addListener('updateUserList', function () {
            that.getUser();
        });
    },
    componentWillUnmount(){
        // 移除 一定要写
        this.listener.remove();
    },
    _renderRow: function (rowData, sectionID, rowID) {
        var isBlocked = rowData[wifiApController.IS_BLOCKED];
        var address = rowData[wifiApController.ADDRESS];
        return (
            <Item>
                <ItemContent>
                    <ItemText>{address}</ItemText>
                    <View style={Styles.container_btn}>
                        <Button color="secondary" size="sm" text={!isBlocked ? "阻止" : "取消封锁"}
                                onPress={() => {this._pressRow(address,isBlocked)}}/>
                    </View>
                </ItemContent>
            </Item>
        );
    },
    _renderSectionHeader(sectionData, sectionID){
        return (
            <Item style={cs.itemDivider}>
                <ItemContent line={false}>
                    <Text style={[cs.itemText, cs.itemDividerText]}>{sectionData}</Text>
                </ItemContent>
            </Item>
        )
    },
    render: function () {
        return this.state.dataSource ? (
            <ListView
                style={Styles.listView}
                dataSource={this.state.dataSource}
                renderRow={this._renderRow}
                renderSectionHeader={this._renderSectionHeader}
                onEndReachedThreshold={0}
                enableEmptySections={true}
            />
        ) : (
            <View style={Styles.container}>
                <View style={Styles.center}>
                    <Text style={Styles.address}>用户列表为空</Text>
                </View>
            </View>
        )
    },
    getUser(){
        wifiApController.getUserList(
            (array)=> {
                var dataBlog = {},
                    rowIDs = [[], []],
                    sectionIDs = ["s1", "s2"],
                    unBlockCount = 0;
                array.map(function (user) {
                        var address = user[wifiApController.ADDRESS];
                        var isBlocked = user[wifiApController.IS_BLOCKED];
                        if (!isBlocked) {
                            rowIDs[0].push(address);
                            dataBlog[sectionIDs[0] + ":" + address] = user;
                            unBlockCount++;
                        } else {
                            rowIDs[1].push(address);
                            dataBlog[sectionIDs[1] + ":" + address] = user;
                        }
                    }
                );
                dataBlog[sectionIDs[0]] = unBlockCount + "个已连接用户";
                dataBlog[sectionIDs[1]] = (array.length - unBlockCount) + "个已封锁用户";
                this.setState({dataSource: this.state.dataSource.cloneWithRowsAndSections(dataBlog, sectionIDs, rowIDs)});
            }
        );
        /*var dataBlog = {},
         rowIDs = [[], []],
         sectionIDs = ["s1", "s2"],
         unBlockCount = 0;
         array.map(function (user) {
         var address = user[wifiApController.ADDRESS];
         var isBlocked = user[wifiApController.IS_BLOCKED];
         if (!isBlocked) {
         rowIDs[0].push(address);
         dataBlog[sectionIDs[0] + ":" + address] = user;
         unBlockCount++;
         } else {
         rowIDs[1].push(address);
         dataBlog[sectionIDs[1] + ":" + address] = user;
         }
         }
         );
         dataBlog[sectionIDs[0]] = unBlockCount + "个已连接用户";
         dataBlog[sectionIDs[1]] = (array.length - unBlockCount) + "个已封锁用户";

         this.setState({dataSource: this.state.dataSource.cloneWithRowsAndSections(dataBlog, sectionIDs, rowIDs)});*/
    },
    // 点击事件
    _pressRow: function (userAddress:string, isBlock:boolean) {
        wifiApController.handleUser(userAddress, isBlock, (isSuccess)=> {
            if (isSuccess) {
                this.getUser();
            }
        });

        /*array.map(function (user) {
         var address = user[wifiApController.ADDRESS];
         var isBlocked = user[wifiApController.IS_BLOCKED];
         if (address == userAddress) {
         user[wifiApController.IS_BLOCKED] = !isBlocked;
         }
         });//测试数据
         this.getUser();*/
    },
});

var Styles = StyleSheet.create({
    container: {
        flex: 1,
    },
    center: {
        flex: 1,
        flexDirection: 'row',
        justifyContent: 'space-around',
        alignItems: 'center',
        backgroundColor: '#F5FCFF',
        borderBottomWidth: 1,
        borderColor: '#eee',
    },

    head: {
        flex: 1,
        flexDirection: 'row',
        justifyContent: 'space-around',
        alignItems: 'center',
        borderBottomWidth: 1,
        borderColor: '#eee',
    },
    back: {
        margin: 10,
    },
    listView: {
        backgroundColor: '#F5FCFF',
    },
    address: {
        flexDirection: 'row',
        textAlign: 'left',
        fontSize: 18,
    },
    empty: {
        textAlign: 'center',
        fontSize: 18,
    },
    name: {
        fontSize: 16,
        textAlign: 'center',
        color: '#fff'
    },
    container_btn: {
        width: 60,
    },
});

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

module.exports = UserView;