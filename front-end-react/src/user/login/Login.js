import React, {Component} from 'react';
import {login, saveUserDetails} from '../../service/UsersService';
import './Login.css';
import {DATA_RELOGIN, PATH_UI_FORGOT_PASSWORD} from '../../constants';
import {Button, Form, Icon, Input, Modal} from 'antd';
import {translate} from "react-i18next";
import {Link} from 'react-router-dom';

const FormItem = Form.Item;

class Login extends Component {

    constructor(props) {
        super(props);
        this.state = {
            username: "",
            isLoading: false
        }
    }

    componentWillMount() {
        if (localStorage.getItem(DATA_RELOGIN)) {
            this.setState({
                username: localStorage.getItem(DATA_RELOGIN)
            });
            localStorage.removeItem(DATA_RELOGIN);
        }
    }

    render() {
        const AntWrappedLoginForm = Form.create()(translate('common')(LoginForm));
        return (
            <div className="login-container">
                <h1 className="page-title">Login</h1>
                <div className="login-content">
                    <AntWrappedLoginForm onLogin={this.props.onLogin} username={this.state.username}/>
                </div>
            </div>
        );
    }
}

class LoginForm extends Component {

    constructor(props) {
        super(props);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleSubmit(event) {
        event.preventDefault();
        this.setState({
            isLoading: true
        });
        this.props.form.validateFields((err, values) => {
            if (!err) {
                const loginRequest = Object.assign({}, values);
                login(loginRequest)
                    .then(response => {
                        saveUserDetails(response);
                        this.props.onLogin();
                    }).catch(error => {
                    this.error();
                });
            } else {
                this.setState({
                    isLoading: false
                });
            }
        });
    }

    error() {
        Modal.error({
            title: this.props.t('Bad credentials'),
            content: this.props.t('The user or pass is incorrect!')
        });
        this.setState({
            isLoading: false
        });
    }

    render() {
        const {getFieldDecorator} = this.props.form;
        return (
            <div>
                <Form onSubmit={this.handleSubmit} className="login-form">
                    <FormItem>
                        {getFieldDecorator('id', {
                            rules: [{required: true, message: this.props.t('This field is required')}],
                            initialValue: this.props.username
                        })(
                            <Input
                                prefix={<Icon type="user"/>}
                                size="large"
                                name="id"
                                type="email"
                                placeholder={this.props.t('Username')}/>
                        )}
                    </FormItem>
                    <FormItem>
                        {getFieldDecorator('pass', {
                            rules: [{required: true, message: this.props.t('This field is required')}],
                        })(
                            <Input
                                prefix={<Icon type="lock"/>}
                                size="large"
                                name="pass"
                                type="password"
                                placeholder={this.props.t('Password')}/>
                        )}
                    </FormItem>
                    <FormItem>
                        <Button type="primary" htmlType="submit" size="large" className="login-form-button"
                                icon="login" loading={this.state && this.state.isLoading}>
                            {this.props.t('Login')}
                        </Button>
                        <div className="custom-links">{this.props.t("Forgot your password?")}
                            <Link to={PATH_UI_FORGOT_PASSWORD}> {this.props.t("Click here!")}</Link>
                        </div>
                    </FormItem>
                </Form>
            </div>
        );
    }
}

export default Login;