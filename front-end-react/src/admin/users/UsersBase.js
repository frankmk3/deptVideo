import React, {Component} from 'react';
import {Button, Col, Drawer, Form, Input, Row, Select, Switch} from 'antd';
import {deleteUser, isAdminCurrentUser, updateUser} from "../../service/UsersService";
import {SECURITY_ROLE_ADMIN, SECURITY_ROLE_USER} from "../../constants";
import {validateConfirmPass, validateEmail, validatePass} from "../../util/ValidationUtils";
import {notification} from "antd/lib/index";

const {Option} = Select;

export class UsersBase extends Component {

    deleteUser = (user) => {
        this.setState({isLoading: true});
        deleteUser(user.id).then(() => {
            this.props.onDelete(user);
        });
    };

    saveUser = () => {
        this.setState({isLoading: true});
        let user = this.state.user;
        if (!user.role) {
            user.role = SECURITY_ROLE_USER;
        }
        if (this.state.pass && this.state.pass.value) {
            user.pass = this.state.pass.value;
        }
        updateUser(user).then(userFromDb => {
            this.props.onSave(userFromDb);
        }).catch(error => {
            notification.error({
                message: 'Dept video',
                description: error.message || this.props.t('expressions.sorry_something_went_wrong')
            })
        })
    };

    showDrawer = () => {
        this.setState({
            editVisible: true,
            keyValue: new Date().getTime(),
        });
    };

    handleSubmit = (e) => {
        e.preventDefault();
        this.props.form.validateFields((err, values) => {
            if (!err) {
                this.saveUser();
                this.onClose();
            } else {
                const key = this.getKey();
                const confirmKey = "confirm" + key;
                const passKey = "pass" + key;
                if (err[confirmKey] && err[confirmKey]["errors"]) {
                    this.setState({
                        "confirmPass": {
                            validationStatus: 'error',
                            errorMsg: err[confirmKey]["errors"].map(m => this.props.t(m.message)).join(". ")
                        }
                    });
                }
                if (err[passKey] && err[passKey]["errors"]) {

                    this.setState({
                        "pass": {
                            validationStatus: 'error',
                            errorMsg: err[passKey]["errors"].map(m => this.props.t(m.message)).join(". ")
                        }
                    });
                }
            }
        });
    };

    onClose = () => {
        this.setState({
            editVisible: false
        });
        if (this.props.onClose) {
            this.props.onClose();
        }
    };

    handleUserValues = (property, value, validationFun, attributes) => {
        let user = this.state.user;
        user[property] = value;
        this.setState({"user": user}, () => {
            if (validationFun) {
                this.setState({
                    [property]: {
                        value: value,
                        ...validationFun(value, attributes)
                    }
                });
            }
        });
    };

    getKey = () => {
        return (this.state.user && this.state.user.id ? this.state.user.id : "").replace(".", "_");
    };

    disableEditFields = () => {
        return this.state.isAdmin !== true;
    };


    constructor(props) {
        super(props);

        let user = props.user;
        user.pass = null;
        this.state = {
            user: user,
            editVisible: false,
            isAdmin: isAdminCurrentUser(),
            groups: props.groups,
            actions: props.actions,
            currentUser: props.currentUser,
            confirmPass: {},
            pass: {}
        }
    };

    customButtons() {
        return "";
    }

    componentDidUpdate() {
        const user = this.props.user;
        if (user) {
            user.pass = null;
        }
        const changes = {
            user: user
        };
        const values = JSON.stringify(changes);
        if (!this.state.propValues || this.state.propValues !== values) {
            this.setState({
                propValues: values,
                user: user
            });
        }
    }

    render() {
        const {form} = this.props;
        const {getFieldDecorator} = form;
        const key = this.getKey();

        const childrenRoles = [
            <Option key={SECURITY_ROLE_USER}>{this.props.t('User')}</Option>,
            <Option key={SECURITY_ROLE_ADMIN}>{this.props.t('Admin')}</Option>
        ];
        const roleElementEnable = this.state.currentUser && this.state.user
            && this.state.currentUser.id !== this.state.user.id;

        const roleRow = <Row gutter={16}>
            <Form.Item label={this.props.t('Role')}>
                {getFieldDecorator('role' + key, {
                    initialValue: (this.state.user
                        && this.state.user.role)
                        ? this.state.user.role : SECURITY_ROLE_USER
                })(
                    <Select disabled={!roleElementEnable}
                            style={{width: '100%'}}
                            placeholder={this.props.t('Role')}
                            onChange={(values) => {
                                this.handleUserValues("role", values);
                            }}>
                        {childrenRoles}
                    </Select>
                )}
            </Form.Item>
        </Row>;


        return <span key={"user" + key}>
            <Drawer
                title={this.props.t('User')}
                placement="right"
                closable={true}
                onClose={this.onClose}
                maskClosable={false}
                width={'calc((100% - 1000)>0?790:100%)'}
                visible={this.state.editVisible || this.props.showEdit}
                key={"drawer-" + key}
                style={{
                    height: 'calc(100% - 55px)',
                    overflow: 'auto',
                    paddingBottom: 53,
                }}>
                <Form layout="vertical" onSubmit={this.handleSubmit}>
                    <Row gutter={16}>
                        <Col span={12}>
                            <Form.Item>
                                {getFieldDecorator("name" + key, {
                                    initialValue: (this.state.user && this.state.user.name) ? this.state.user.name : "",
                                    rules: [{
                                        required: true,
                                        message: this.props.t('This field is required')
                                    }]
                                })(
                                    <Input placeholder={this.props.t('Name')}
                                           disabled={this.disableEditFields()}
                                           onChange={(value) => {
                                               this.handleUserValues("name", value.target.value)
                                           }}
                                    />
                                )}
                            </Form.Item>
                        </Col>
                        <Col span={12}>
                            <Form.Item>
                                {getFieldDecorator('enabled', {
                                    valuePropName: 'checked'
                                })(<div>
                                        <span className="mrg-right">{this.props.t('Enabled')}</span>
                                        <Switch required={true}
                                                defaultChecked={(this.state.user) ? this.state.user.enabled !== false : false}
                                                checkedChildren={true} unCheckedChildren={false}
                                                onChange={(value) => {
                                                    this.handleUserValues("enabled", value)
                                                }}/>
                                    </div>
                                )}
                            </Form.Item>
                        </Col>
                        <Col span={24}>
                            <Form.Item>
                                {getFieldDecorator("email" + key, {
                                    initialValue: (this.state.user && this.state.user.email) ? this.state.user.email : "",
                                    rules: [{
                                        required: this.state.isAdmin,
                                        message: this.props.t('This field is required')
                                    }]
                                })(
                                    <Input placeholder={this.props.t('Email')}
                                           disabled={this.disableEditFields()}
                                           onChange={(value) => {
                                               this.handleUserValues("email", value.target.value, validateEmail)
                                           }}
                                    />
                                )}
                            </Form.Item>
                        </Col>
                    </Row>
                    <Row gutter={16}>
                        <Col span={12}>
                            <Form.Item validateStatus={this.state.pass.validateStatus}
                                       help={this.props.t(this.state.pass.errorMsg, this.state.pass.errorMsgArgs)}>
                                {getFieldDecorator('password' + key, {
                                    rules: [{
                                        required: !this.state.user.id,
                                        message: this.props.t('This field is required'),
                                    }],
                                })(
                                    <Input placeholder={this.props.t('Password')}
                                           disabled={this.disableEditFields()}
                                           type="password" onChange={(value) => {
                                        this.handleUserValues("pass", value.target.value, validatePass);
                                    }}
                                           onBlur={(value) => {
                                               this.handleUserValues("confirmPass", this.state.user.confirmPass, validateConfirmPass, this.state.user.pass)
                                           }}/>
                                )}
                            </Form.Item>
                            <Form.Item>
                                {getFieldDecorator('key', {
                                    initialValue: key,
                                    rules: [{
                                        required: false, message: this.props.t('This field is required'),
                                    }],
                                })(
                                    <Input type="hidden"/>
                                )}
                            </Form.Item>
                        </Col>
                        <Col span={12}>
                            <Form.Item validateStatus={this.state.confirmPass.validateStatus}
                                       help={this.props.t(this.state.confirmPass.errorMsg, this.state.confirmPass.errorMsgArgs)}>
                                {getFieldDecorator('confirm' + key, {
                                    rules: [{
                                        required: this.state.pass && this.state.pass.value,
                                        message: this.props.t('This field is required'),
                                    }, {
                                        validator: this.compareToFirstPassword,
                                    }],
                                })(
                                    <Input placeholder={this.props.t('Password confirm')}
                                           disabled={this.disableEditFields()}
                                           type="password"
                                           onChange={(value) => {
                                               this.handleUserValues("confirmPass", value.target.value, validateConfirmPass, this.state.user.pass)
                                           }}/>
                                )}
                            </Form.Item>
                        </Col>
                    </Row>

                    {roleRow}

                    <div className="form-footer-separator">
                        <Button onClick={this.onClose}>
                            {this.props.t('Cancel')}
                        </Button>
                        <Button className="mrg-left" type="primary" htmlType="submit">
                            {this.props.t('Accept')}
                        </Button>
                    </div>
                </Form>
            </Drawer>
            {this.customButtons()}
        </span>
    }
}