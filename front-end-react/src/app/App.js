import React, {Component} from 'react';
import './App.css';
import {Route, Switch, withRouter} from 'react-router-dom';
import {Detector} from "react-detect-offline";
import {
    DATA_ACCESS_TOKEN,
    DATA_RELOGIN,
    DATA_USER_DATA_KEY,
    PATH_UI_ENTER_NEW_PASSWORD,
    PATH_UI_FORGOT_PASSWORD
} from '../constants';
import AppHeader from '../common/AppHeader';
import AppFooter from '../common/AppFooter';
import NotFound from '../common/NotFound';
import {Alert, Divider, Icon, Layout, Spin} from 'antd';
import {translate} from 'react-i18next';
import {isAdminCurrentUser, isSetPasswordRequired} from "../service/UsersService";
import PasswordRecoverRequest from "../user/passwords/PasswordRecoverRequest";
import PasswordRecover from "../user/passwords/PasswordRecover";
import Contact from "../user/contact/Contact";
import ProfileRoute from "./routes/ProfileRoute";
import DashboardRoute from "./routes/DashboardRoute";
import ManagementRoute from "./routes/ManagementRoute";
import MainRoute from "./routes/MainRoute";
import RegistrationRoute from "./routes/RegistrationRoute";

const {Content} = Layout;

class App extends Component {

    constructor(props) {
        super(props);
        this.state = {
            currentUser: null,
            isAuthenticated: false,
            isLoading: false,
            showContent: true
        };
        this.handleLogout = this.handleLogout.bind(this);
        this.loadCurrentUser = this.loadCurrentUser.bind(this);
        this.handleLogin = this.handleLogin.bind(this);
        window.app = this;
    }

    loadCurrentUser() {
        this.setState({
            isLoading: true
        });

        if (localStorage.getItem(DATA_ACCESS_TOKEN) && localStorage.getItem(DATA_USER_DATA_KEY)) {
            const admin = isAdminCurrentUser();
            const setPassword = isSetPasswordRequired();
            this.setState({
                currentUser: JSON.parse(localStorage.getItem(DATA_USER_DATA_KEY)),
                isAuthenticated: true,
                isLoading: false,
                isAdmin: admin,
                isSetPasswordRequired: setPassword
            });
        } else {
            this.setState({
                isLoading: false
            });
        }
    }

    componentWillMount() {
        this.loadCurrentUser();
    }

    handleLogout(redirectTo = "/") {
        if (this.state && this.state.currentUser) {
            localStorage.setItem(DATA_RELOGIN, this.state.currentUser.id);
        }
        localStorage.removeItem(DATA_ACCESS_TOKEN);
        localStorage.removeItem(DATA_USER_DATA_KEY);

        this.setState({
            currentUser: null,
            isAuthenticated: false
        });

        this.props.history.push(redirectTo);
    }

    handleLogin() {
        this.loadCurrentUser();
        this.props.history.push("/");
    }

    render() {
        if (this.state.isLoading) {
            return <Spin size="large"/>
        }
        return (
            <Layout className="app-container">
                <AppHeader isAuthenticated={this.state.isAuthenticated}
                           currentUser={this.state.currentUser}
                           onLogout={this.handleLogout}/>

                <Content className="app-content">
                    <div className="container">
                        <Detector
                            render={({online}) => (
                                online ?
                                    null
                                    :
                                    <Alert className="connection-problem"
                                           message={this.props.t('Connection problem detected')}
                                           description={
                                               <div>
                                                   {this.props.t('Please, check your connectivity')}
                                                   <Divider/>
                                                   <div className="retry">
                                                       {this.props.t('Retrying connection')} <Icon type="loading"/>
                                                   </div>
                                               </div>
                                           }
                                           type="error"
                                           showIcon
                                           banner={true}
                                    />
                            )}
                            onChange={status => {
                                this.setState({
                                    showContent: status
                                });
                            }}
                        />

                        <Switch>
                            <Route path="/users/profile"
                                   render={(props) => this.state.showContent ?
                                       <ProfileRoute {...this.state} logout={this.handleLogout}/> : null}/>

                            <Route path="/dashboard"
                                   render={(props) => this.state.showContent ?
                                       <DashboardRoute {...this.state} /> : null}/>

                            <Route path="/management/:activeTab?"
                                   render={(props) => this.state.showContent ?
                                       <ManagementRoute {...this.state} /> : null}/>

                            <Route path="/contact"
                                   render={(props) => this.state.showContent ?
                                       <Contact /> : null}/>

                            <Route path={PATH_UI_FORGOT_PASSWORD}
                                   render={(props) => this.state.showContent ? <RegistrationRoute
                                       component={<PasswordRecoverRequest/>} {...this.state} /> : null}/>
                            <Route path={PATH_UI_ENTER_NEW_PASSWORD +"/:token"}
                                   render={(props) => this.state.showContent ?
                                       <PasswordRecover   token={this.props.match.params.token} component={<PasswordRecoverRequest
                                           token={this.props.match.params.token}/>} {...this.state} /> : null}/>

                            <Route exact path="/"
                                   render={(props) => this.state.showContent ?
                                       <MainRoute onLogin={this.handleLogin} {...this.state} /> : null}/>
                            <Route component={NotFound}></Route>
                        </Switch>
                    </div>
                </Content>

                <AppFooter isAuthenticated={this.state.isAuthenticated} currentUser={this.state.currentUser}
                           isAdmin={this.state.isAdmin}/>
            </Layout>
        );
    }
}

export default translate('common')(withRouter(App));
