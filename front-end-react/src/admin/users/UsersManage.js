import {UsersBase} from "./UsersBase";
import {Form} from 'antd';
import {translate} from "react-i18next";

class UsersManageApp extends UsersBase {

    customButtons() {
        return "";
    }

}

const UsersManage = Form.create()(translate('common')(UsersManageApp));
export default UsersManage;