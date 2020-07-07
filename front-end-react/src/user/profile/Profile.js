import React, {Component} from 'react';
import {
    changeUserPassword,
    getCurrentLanguage,
    getUserProfile,
    getUserProfileProperty,
    invalidateSessions,
    refreshToken,
    saveUserDetails,
    setUserProfileProperty,
    updateOwnUserInfo
} from '../../service/UsersService';
import {Alert, Avatar, Button, Card, Col, Collapse, Divider, Form, Input, Modal, Radio, Row, Spin} from 'antd';
import {getAvatarColor} from '../../util/Colors';
import './Profile.css';
import NotFound from '../../common/NotFound';
import ServerError from '../../common/ServerError';
import {translate} from "react-i18next";
import f_united_kingdom from "../../f_united_kingdom.svg";
import {
    SOURCE_INTERNAL,
    USER_PROPERTY_CONFIRMATION,
    USER_PROPERTY_CONNECTION_NOTIFICATION,
    USER_PROPERTY_NOTIFICATION
} from "../../constants";
import {validateConfirmPass, validatePass} from "../../util/ValidationUtils";
import {showError, showInfo} from "../../util/APIUtils";

const Panel = Collapse.Panel;
const confirm = Modal.confirm;

class Profile extends Component {

    onConfirmationAccessChange = (e) => {
        const request = e.target.checked;
        const profile = setUserProfileProperty(USER_PROPERTY_CONFIRMATION, request);

        updateOwnUserInfo(profile).finally(() => {
            saveUserDetails(profile);
            this.setState({
                confirmationRequest: request
            });
        });
    };

    handleUserPasswordValues = (property, value, validationFun, attributes) => {
        let userPassword = this.state.userPassword;
        userPassword[property] = value;
        this.setState({userPassword: userPassword});
        if (validationFun) {
            this.setState({
                [property]: {
                    value: value,
                    ...validationFun(value, attributes)
                }
            });
        }
    };

    enabledUpdateUserPassword = () => {
        return this.state.userPassword.oldPass
            && this.state.userPassword.pass
            && this.state.userPassword.confirmPass
            && !this.state.pass.errorMsg
            && !this.state.confirmPass.errorMsg;
    };

    updateUserPassword = () => {
        let userId = this.state.user.id;
        changeUserPassword(userId, this.state.userPassword.oldPass, this.state.userPassword.pass).then((response) => {
            if (response && response.id === userId) {
                showInfo(this.translate("Password is changed"), this.translate("Info"));
                window.app.handleLogout();
            }
        }).catch(error => {
            if (typeof error.json === 'function') {
                error.json().then(json => {
                    showError(this.translate(json.message));
                });
            } else {
                showError(this.translate(error.message));
            }
        });
    };

    constructor(props) {
        super(props);
        this.state = {
            user: null,
            isLoading: false,
            userPassword: {},
            confirmPass: {},
            pass: {},
            oldPass: {}
        };
        this.loadUserProfile = this.loadUserProfile.bind(this);
    }

    invalidateSessions(lock) {
        const user = this.state.user;
        const logout = this.props.logout;
        confirm({
            title: this.props.t('Confirm'),
            content: this.props.t('Are you sure?'),
            onOk() {
                invalidateSessions(user, lock, () => {
                    logout();
                });
            },
            cancelText: this.props.t('Cancel')
        });
    }

    loadUserProfile() {
        this.setState({
            isLoading: true
        });

        this.setState({
            user: getUserProfile(),
            lang: getCurrentLanguage(),
            notificationLevel: getUserProfileProperty(USER_PROPERTY_NOTIFICATION, ""),
            confirmationRequest: getUserProfileProperty(USER_PROPERTY_CONFIRMATION, true),
            notifyConnectionChange: getUserProfileProperty(USER_PROPERTY_CONNECTION_NOTIFICATION, true),
            isLoading: false
        });
    }

    componentDidMount() {
        this.loadUserProfile();
        refreshToken();
    }

    componentWillReceiveProps(nextProps) {
        this.loadUserProfile();
    }

    onLanguageChange(e) {
        const language = e.target.value;
        const profile = getUserProfile();
        profile.lang = language;
        saveUserDetails(profile);
        updateOwnUserInfo(profile).finally(() => {
            window.location.reload();
        });
    }

    render() {
        const {form} = this.props;
        const {getFieldDecorator} = form;
        if (this.state.isLoading) {
            return <Spin size="large"/>;
        }

        if (this.state.notFound) {
            return <NotFound/>;
        }

        if (this.state.serverError) {
            return <ServerError/>;
        }

        return (
            <div className="profile">
                {
                    this.state.user ? (
                        <div className="user-profile">
                            <div className="user-details">
                                <div className="user-avatar">
                                    <Avatar className="user-avatar-circle"
                                            style={{backgroundColor: getAvatarColor(this.state.user.name)}}>
                                        {this.state.user.name[0].toUpperCase()}
                                    </Avatar>
                                </div>
                                <div className="user-summary">
                                    <div className="full-name">{this.state.user.name}</div>
                                    <div className="username">{this.state.user.id}</div>
                                    <div className="language">
                                        <Radio.Group defaultValue={this.state.lang} onChange={this.onLanguageChange}>
                                            <Radio.Button value="en">
                                                <img alt="" height={20} src={f_united_kingdom}/> English
                                            </Radio.Button>
                                        </Radio.Group>
                                    </div>
                                </div>
                            </div>
                            <div>
                                <Collapse bordered={false} accordion={false} defaultActiveKey={['2', '3']}
                                          className="configurationsCollapse">
                                    <Panel header={this.props.t('Account security')} key="1">
                                        <div className="configurations">
                                            <Row>
                                                {
                                                    this.state.user && this.state.user.source === SOURCE_INTERNAL ?
                                                        <div>
                                                            <Divider
                                                                className="no-top-margin">{this.props.t('Change password')}</Divider>
                                                            <div className="action-separator-x2">
                                                                <Form.Item
                                                                    validateStatus={this.state.oldPass.validateStatus}
                                                                    help={this.props.t(this.state.pass.errorMsg, this.state.oldPass.errorMsgArgs)}>
                                                                    {getFieldDecorator('oldPassword', {
                                                                        rules: [{
                                                                            required: !this.state.user.id,
                                                                            message: this.props.t('This field is required'),
                                                                        }],
                                                                    })(
                                                                        <Input
                                                                            placeholder={this.props.t('Current password')}
                                                                            type="password" onChange={(value) => {
                                                                            this.handleUserPasswordValues("oldPass", value.target.value);
                                                                        }}/>
                                                                    )}
                                                                </Form.Item>
                                                                <Form.Item
                                                                    validateStatus={this.state.pass.validateStatus}
                                                                    help={this.props.t(this.state.pass.errorMsg, this.state.pass.errorMsgArgs)}>
                                                                    {getFieldDecorator('password', {
                                                                        rules: [{
                                                                            required: !this.state.user.id,
                                                                            message: this.props.t('This field is required'),
                                                                        }],
                                                                    })(
                                                                        <Input placeholder={this.props.t('Password')}
                                                                               type="password" onChange={(value) => {
                                                                            this.handleUserPasswordValues("pass", value.target.value, validatePass);
                                                                        }}
                                                                               onBlur={(value) => {
                                                                                   this.handleUserPasswordValues("confirmPass", this.state.userPassword.confirmPass, validateConfirmPass, this.state.userPassword.pass)
                                                                               }}/>
                                                                    )}
                                                                </Form.Item>
                                                                <Form.Item
                                                                    validateStatus={this.state.confirmPass.validateStatus}
                                                                    help={this.props.t(this.state.confirmPass.errorMsg, this.state.confirmPass.errorMsgArgs)}>
                                                                    {getFieldDecorator('confirm', {
                                                                        rules: [{
                                                                            required: this.state.pass && this.state.pass.value,
                                                                            message: this.props.t('This field is required'),
                                                                        }, {
                                                                            validator: this.compareToFirstPassword,
                                                                        }],
                                                                    })(
                                                                        <Input
                                                                            placeholder={this.props.t('Password confirm')}
                                                                            type="password"
                                                                            onChange={(value) => {
                                                                                this.handleUserPasswordValues("confirmPass", value.target.value, validateConfirmPass, this.state.userPassword.pass)
                                                                            }}/>
                                                                    )}
                                                                </Form.Item>
                                                            </div>
                                                            <Button type="primary"
                                                                    htmlType="submit"
                                                                    onClick={this.updateUserPassword}
                                                                    disabled={!this.enabledUpdateUserPassword()}>
                                                                {this.props.t('Accept')}
                                                            </Button>
                                                            <Divider>{this.props.t('Close all sessions')}</Divider>
                                                        </div> : null
                                                }
                                                <Alert
                                                    message={this.props.t('Warning')}
                                                    description={this.props.t('All sessions for your current user name will be invalidated')}
                                                    type="warning"
                                                    showIcon/>
                                                <Row>
                                                    <Col md={12} sm={24}>
                                                        <div className="center padding-20">
                                                            <Button type="primary" size="large" block
                                                                    onClick={e => this.invalidateSessions(false)}
                                                                    icon="scissor">
                                                                {this.props.t('Close all sessions')}
                                                            </Button>
                                                        </div>
                                                    </Col>
                                                    <Col md={12} sm={24}>
                                                        <div className="center padding-20">
                                                            <Button type="danger" size="large" block
                                                                    onClick={e => this.invalidateSessions(true)}
                                                                    icon="lock">
                                                                {this.props.t('Lock user account')}
                                                            </Button>
                                                        </div>
                                                    </Col>
                                                </Row>
                                            </Row>
                                        </div>
                                    </Panel>


                                    <Panel header={this.props.t('Permission level')} key="3">
                                        {
                                            <div className="roles">
                                                <Row>
                                                    <Col lg={6} md={12} sm={24}>
                                                        <Card>
                                                            {this.props.t(this.state.user.role)}</Card>
                                                    </Col>
                                                </Row>
                                            </div>
                                        }
                                    </Panel>
                                </Collapse>

                            </div>
                        </div>
                    ) : null
                }
            </div>
        );
    }

    translate(key, parameters) {
        return this.props.t(key, parameters);
    }
}

export default Form.create()(translate('common')(Profile));