import React, {Component} from 'react';
import {Button, Form, Input, Spin} from 'antd';
import './Contact.css';
import ServerError from '../../common/ServerError';
import {translate} from "react-i18next";
import {validateEmail} from "../../util/ValidationUtils";
import {withRouter} from 'react-router-dom';
import {saveContact} from "../../service/ContactService";
import {notification} from "antd/lib/index";
import {
    getUserProfile
} from '../../service/UsersService';
const {TextArea} = Input;

class Contact extends Component {

    handleContactValues = (property, value, validationFun, attributes) => {
        let contact = this.state.contact;
        contact[property] = value;
        if (!validationFun) {
            this.setState({"contact": contact});
        } else {
            this.setState({
                "contact": contact,
                [property]: {
                    value: value,
                    ...validationFun(value, attributes)
                }
            });
        }


    };

    handleSubmit = (event) => {
        event.preventDefault();
        this.setState({isLoading: true});

        saveContact(this.state.contact)
            .then(response => {
                this.setState({complete: true});
                notification.info({
                    message: 'Dept video',
                    description: this.props.t('Request sent successfully.')
                });
                if(this.props.onSend){
                    this.props.onSend(response);
                }
            }).catch(error => {
            notification.error({
                message: 'Dept video',
                description: error.message || this.props.t('expressions.sorry_something_went_wrong')
            })
        }).finally(() =>
            this.setState({isLoading: false}));
    };

    constructor(props) {
        super(props);
        const currentUser= getUserProfile();
        this.state = {
            user: currentUser,
            contact: {
                "email": currentUser.email,
                "name": this.props.name,
                "comment": this.props.t('I would like to request a new trailer for this video.')
            },
            email: {
                "value": currentUser.email
            },
            isLoading: false
        };
    }

    render() {
        const {form} = this.props;
        const {getFieldDecorator} = form;
        if (this.state.isLoading) {
            return <Spin size="large"/>;
        }

        if (this.state.serverError) {
            return <ServerError/>;
        }

        return (
            <div className="contact">
                {
                    this.state.user ?
                        <div>
                            <Form onSubmit={this.handleSubmit} className="contact-request-form">
                                <Form.Item
                                    help={this.props.t("Name of the movie")}>
                                    {getFieldDecorator('name', {
                                        initialValue: (this.state.contact && this.state.contact.name) ? this.state.contact.name : "",
                                        rules: [{
                                            required: true,
                                            message: this.props.t('This field is required'),
                                        }],
                                    })(
                                        <Input
                                            placeholder={this.props.t('Movie name')}
                                            type="text" onChange={(value) => {
                                            this.handleContactValues("name", value.target.value);
                                        }}/>
                                    )}
                                </Form.Item>
                                <Form.Item

                                    validateStatus={this.state.email.validateStatus}
                                    help={this.props.t(this.state.email.errorMsg, this.state.email.errorMsgArgs)}>
                                    {getFieldDecorator('email', {
                                        initialValue: (this.state.user && this.state.user.email) ? this.state.user.email : "",
                                        rules: [{
                                            required: true,
                                            message: this.props.t('This field is required'),
                                        }],
                                    })(
                                        <Input
                                            placeholder={this.props.t('Contact email')}
                                            type="text" onChange={(value) => {
                                            this.handleContactValues("email", value.target.value, validateEmail);
                                        }}/>
                                    )}
                                </Form.Item>
                                <Form.Item
                                    help={this.props.t("Comment")}>
                                    {getFieldDecorator('comment', {
                                        initialValue: (this.state.contact && this.state.contact.comment) ? this.state.contact.comment : "",
                                        rules: [{
                                            required: true,
                                            message: this.props.t('This field is required'),
                                        }],
                                    })(
                                        <TextArea
                                            placeholder={this.props.t('Comment')}
                                            type="text" onChange={(value) => {
                                            this.handleContactValues("comment", value.target.value);
                                        }}/>
                                    )}
                                </Form.Item>
                                <Button type="primary"
                                        htmlType="submit"
                                >
                                    {this.props.t('Accept')}
                                </Button>
                            </Form>
                        </div>
                        : null
                }
            </div>
        );
    }

    translate(key, parameters) {
        return this.props.t(key, parameters);
    }
}

export default Form.create()(translate('common')(withRouter(Contact)));