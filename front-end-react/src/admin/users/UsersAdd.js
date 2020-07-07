import React from 'react';
import {UsersBase} from "./UsersBase";
import {Button, Form, Icon} from 'antd';
import {translate} from "react-i18next";


class UsersAddApp extends UsersBase {

    customButtons() {

        return <Button key="inviteAddBtn"
                       type="primary"
                       onClick={() => {
                           this.showDrawer()
                       }}>
            <Icon type="plus"/>
            {this.props.t('Add')}
        </Button>
    }

}

const UsersAdd = Form.create()(translate('common')(UsersAddApp));
export default UsersAdd;