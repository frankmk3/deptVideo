import React from 'react';
import {UsersBase} from "./UsersBase";
import {Divider, Form, Icon, Popconfirm, Tooltip} from 'antd';
import {translate} from "react-i18next";


class UsersEditApp extends UsersBase {

    customButtons(){
        const key = this.getKey();
        const userDeleteLink = this.state.currentUser && this.state.currentUser.id !== this.state.user.id ?
            <Popconfirm title={this.props.t('Do you want to delete this user?')} onConfirm={() => {
                this.deleteUser(this.state.user)
            }} cancelText={this.props.t('Cancel')}>

                <Tooltip placement="topLeft" title={this.props.t('Delete')}>
                    <a href="#2"><Icon type="delete" theme="twoTone"/></a>
                </Tooltip>
            </Popconfirm> :
            <Icon type="delete" theme="outlined"/>;
        return <div className="edit-user-actions">
                <span onClick={(e) => this.showDrawer()} key={"edit" + key}>
                <Tooltip placement="topLeft" title={this.props.t('Edit')}>
                    <a href="#edit">
                        <Icon type="edit" theme="twoTone"/>
                    </a>
                </Tooltip>
            </span>
            <Divider type="vertical"/>

            {userDeleteLink}
        </div>
    }

}

const UsersEdit = Form.create()(translate('common')(UsersEditApp));
export default UsersEdit;