import React, {Component} from 'react';
import {Link, withRouter} from 'react-router-dom';
import './AppHeader.css';
import {Dropdown, Icon, Layout, Menu} from 'antd';
import {isAdmin} from "../service/UsersService";
import {translate} from "react-i18next";

const Header = Layout.Header;

class AppHeader extends Component {
    constructor(props) {
        super(props);
        this.handleMenuClick = this.handleMenuClick.bind(this);
    }

    handleMenuClick({key}) {
        if (key === "logout") {
            this.props.onLogout();
        }
    }

    render() {
        let menuItems;
        if (this.props.currentUser) {
            const labels = [];
            labels['profile'] = this.props.t('Profile', {framework: "react-i18next"});
            labels['logout'] = this.props.t('Logout', {framework: "react-i18next"});

            menuItems = [];
            if (isAdmin(this.props.currentUser)) {
                menuItems.push(
                    <Menu.Item key="/management/users">
                        <Link to="/management/users">
                            <Icon type="dashboard" className="nav-icon"/>
                        </Link>
                    </Menu.Item>)
            }
            menuItems.push(
                <Menu.Item key="/contact">
                    <Link to="/contact">
                        <Icon type="mail" className="nav-icon"/>
                    </Link>
                </Menu.Item>
            );
            menuItems.push(
                <Menu.Item key="/profile" className="profile-menu">
                    <ProfileDropdownMenu
                        labels={labels}
                        currentUser={this.props.currentUser}
                        handleMenuClick={this.handleMenuClick}/>
                </Menu.Item>
            );
        }

        return (
            <Header className="app-header">
                <div className="container">
                    <div className="app-title">
                        <Link to="/">Dept video</Link>
                    </div>
                    <Menu
                        className="app-menu"
                        mode="horizontal"
                        selectedKeys={[this.props.location.pathname]}
                        style={{lineHeight: '64px'}}>
                        {menuItems}
                    </Menu>
                </div>
            </Header>
        );
    }
}

function ProfileDropdownMenu(props) {
    const dropdownMenu = (
        <Menu onClick={props.handleMenuClick} className="profile-dropdown-menu">
            <Menu.Item key="user-info" className="dropdown-item" disabled>
                <div className="user-full-name-info">
                    {props.currentUser.name}
                </div>
                <div className="username-info">
                    {props.currentUser.source === "Internal" ? props.currentUser.id : props.currentUser.email}
                </div>
            </Menu.Item>
            <Menu.Divider/>
            <Menu.Item key="profile" className="dropdown-item">
                <Link to={`/users/profile`}>
                    {props.labels['profile']}
                </Link>
            </Menu.Item>
            <Menu.Item key="logout" className="dropdown-item">
                {props.labels['logout']}
            </Menu.Item>
        </Menu>
    );

    return (
        <Dropdown
            overlay={dropdownMenu}
            trigger={['click']}
            getPopupContainer={() => document.getElementsByClassName('profile-menu')[0]}>
            <a className="ant-dropdown-link">
                <Icon type="user" className="nav-icon" style={{marginRight: 0}}/> <Icon type="down"/>
            </a>
        </Dropdown>
    );
}

export default translate('common')(withRouter(AppHeader));