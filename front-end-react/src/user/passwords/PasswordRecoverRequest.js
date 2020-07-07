import React, {Component} from 'react';
import "./PasswordRecover.css"
import {Alert, Button, Divider, Form, Input} from 'antd';
import {notification} from "antd/lib/index";
import {withRouter} from 'react-router-dom';
import {forgotPasswordRequest} from "../../service/UsersService";
import {validateEmail} from "../../util/ValidationUtils";
import {translate} from "react-i18next";

const FormItem = Form.Item;

class PasswordRecoverRequest extends Component {

    constructor(props) {
        super(props);
        this.state = {
            isLoading: false,
            complete: false,
            email: {
                value: ''
            }
        };
        this.handleInputChange = this.handleInputChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.isFormInvalid = this.isFormInvalid.bind(this);
    }

    handleInputChange(event, validationFun) {
        const target = event.target;
        const inputName = target.name;
        const inputValue = target.value;

        this.setState({
            [inputName]: {
                value: inputValue,
                ...validationFun(inputValue)
            }
        });
    }

    handleSubmit(event) {
        event.preventDefault();
        this.setState({isLoading: true});
        const passwordChangeRequest = {
            email: this.state.email.value
        };
        forgotPasswordRequest(passwordChangeRequest)
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
            this.state.email.validateStatus === 'success'
        );
    }

    render() {
        return (
            this.state && this.state.complete ?
                <div>
                    <Alert
                        showIcon
                        description={this.props.t("An email message was sent to entered email address with instructions to set a new password")}
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
                    <h1 className="page-title">{this.props.t("Password recover")}</h1>
                    <div className="password-recover-content">
                        <Form onSubmit={this.handleSubmit} className="signup-form">
                            <FormItem
                                hasFeedback
                                validateStatus={this.state.email.validateStatus}
                                help={this.props.t(this.state.email.errorMsg, this.state.email.errorMsgArgs)}>
                                <Input
                                    size="large"
                                    name="email"
                                    type="email"
                                    placeholder={this.props.t("Email")}
                                    value={this.state.email.value}
                                    onChange={(event) => this.handleInputChange(event, validateEmail)}/>
                            </FormItem>
                            <FormItem>
                                <Button type="primary"
                                        htmlType="submit"
                                        size="large"
                                        className="signup-form-button"
                                        disabled={this.isFormInvalid()}
                                        loading={this.state && this.state.isLoading}>{this.props.t("Accept")}</Button>
                            </FormItem>
                        </Form>
                    </div>
                </div>
        );
    }
}

export default translate('common')(withRouter(PasswordRecoverRequest));