import React, {Component} from 'react';
import {Redirect, withRouter} from 'react-router-dom';
import {Icon, Tabs} from 'antd';
import Users from "../../admin/users/Users";
import Contacts from "../../admin/contact/Contacts";
import {translate} from "react-i18next";

const TabPane = Tabs.TabPane;

class ManagementRoute extends Component {

    tabChange = (activeKey) => {
        this.props.history.push("/management/" + activeKey);
    };

    render() {
        let activeTab = this.props.match.params.activeTab ? this.props.match.params.activeTab : "users";

        return (
            this.props.isAuthenticated ?
                <Tabs
                    defaultActiveKey={activeTab}
                    onChange={this.tabChange}>
                    <TabPane key={"users" }
                             tab={<span><Icon type="user"/>{this.props.t('Users')}</span>}>
                        <Users
                               date={this.props.date}
                               currentUser={this.props.currentUser}
                               isAdmin={this.props.isAdmin}/>
                    </TabPane>
                    <TabPane key={"contacts" }
                             tab={<span><Icon type="question"/>{this.props.t('Contact')}</span>}>
                        <Contacts
                               date={this.props.date}
                               currentUser={this.props.currentUser}
                               isAdmin={this.props.isAdmin}/>
                    </TabPane>
                </Tabs>
                : <Redirect to={{pathname: '/', state: {from: this.props.location}}}/>
        );
    }
}

export default translate('common')(withRouter(ManagementRoute));