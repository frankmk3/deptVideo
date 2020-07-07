import React, {Component} from 'react';
import "./PasswordRecover.css"
import {Link, withRouter} from 'react-router-dom';
import {validateConfirmPass, validatePass} from '../../util/ValidationUtils';
import {Alert, Button, Divider, Form, Input, notification} from 'antd';
import {changePassword, passwordTokenValidation} from "../../service/UsersService";
import {translate} from "react-i18next";

const FormItem = Form.Item;

class PasswordRecover extends Component {

    constructor(props) {
        super(props);
        this.state = {
            isLoading: true,
            complete: false,
            token: this.props.token?this.props.token:this.props.match.params.token,
            pass: {
                value: ''
            },
            confirmPass: {
                value: ''
            }
        };
        this.handleInputChange = this.handleInputChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.isFormInvalid = this.isFormInvalid.bind(this);
    }

    componentDidMount(prevProps, prevState, snapshot) {
        passwordTokenValidation(this.state.token).then(response => {
            this.setState({
                isLoading: false,
                available: response.available
            });

        }).catch(error => {
            this.setState({
                isLoading: false,
                available: false
            });
        });
    }

    handleInputChange(event, validationFun, params) {
        const target = event.target;
        const inputName = target.name;
        const inputValue = target.value;

        this.setState({
            [inputName]: {
                value: inputValue,
                ...validationFun(inputValue, params)
            }
        });
    }

    handleSubmit(event) {
        event.preventDefault();
        this.setState({isLoading: true});
        const passwordChangeRequest = {
            pass: this.state.pass.value
        };
        changePassword(passwordChangeRequest, this.state.token)
            .then(response => {
                this.setState({complete: true});
            }).catch(error => {
            this.setState({isLoading: false});
            notification.error({
                message: 'Dept video',
                description: error.message || this.props.t('expressions.sorry_something_went_wrong')
            });
        });
    }

    isFormInvalid() {
        return !(
            this.state.pass.validateStatus === 'success' &&
            this.state.confirmPass.validateStatus === 'success'
        );
    }

    render() {
        return (
            <div>
                {
                    this.state && !this.state.loading
                    && (this.state.complete || (this.state.available !== undefined && !this.state.available)) ?
                        <div>
                            <Alert
                                showIcon
                                description={this.props.t("The password have been updated, please login to continue")}
                                type="info"/>
                            <Divider/>
                            <div className="signup-container">
                                <div className="center">
                                    <Button type="danger" size="large" className="login-form-button"
                                            onClick={() => this.props.history.push('/')}>
                                        {this.props.t('Close')}
                                    </Button>
                                </div>
                            </div>
                        </div>
                        :
                        <div className="password-recover-container">
                            <h1 className="page-title">{this.props.t("Password change")}</h1>
                            <div className="password-recover-content">
                                <Form onSubmit={this.handleSubmit} className="password-recover-form">
                                    <FormItem
                                        validateStatus={this.state.pass.validateStatus}
                                        help={this.props.t(this.state.pass.errorMsg, this.state.pass.errorMsgArgs)}>
                                        <Input
                                            size="large"
                                            name="pass"
                                            type="password"
                                            autoComplete="off"
                                            placeholder={this.props.t("Password")}
                                            value={this.state.pass.value}
                                            onChange={(event) => this.handleInputChange(event, validatePass)}/>
                                    </FormItem>
                                    <FormItem
                                        validateStatus={this.state.confirmPass.validateStatus}
                                        help={this.props.t(this.state.confirmPass.errorMsg, this.state.confirmPass.errorMsgArgs)}>
                                        <Input
                                            size="large"
                                            name="confirmPass"
                                            type="password"
                                            autoComplete="off"
                                            placeholder={this.props.t("Confirm the password")}
                                            value={this.state.confirmPass.value}
                                            onChange={(event) => this.handleInputChange(event, validateConfirmPass, this.state.pass.value)}/>
                                    </FormItem>
                                    <FormItem>
                                        <Button type="primary"
                                                htmlType="submit"
                                                size="large"
                                                className="password-recover-form-button"
                                                disabled={this.isFormInvalid()}
                                                loading={this.state && this.state.isLoading}>{this.props.t("Change the password")}</Button>
                                        {this.props.t("Don't want to change the password?")} <Link
                                        to="/">{this.props.t("Login now!")}</Link>
                                    </FormItem>
                                </Form>
                            </div>
                        </div>
                }
            </div>
        );
    }
}

export default translate('common')(withRouter(PasswordRecover));