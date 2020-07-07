import React from 'react';
import {deleteUser, getUserFilters, getUsers} from "../../service/UsersService";
import PaginatedDataTable from "../../common/component/PaginatedDataTable";
import {Button, Divider, Icon, Popconfirm, Table, Tooltip} from 'antd';
import DialogTextInputFilter from "../../common/component/DialogTextInputFilter";
import UsersManage from "./UsersManage";
import {translate} from "react-i18next";

class Users extends PaginatedDataTable {

    reloadPage = (user) => {
        this.onPage(this.state.page, this.state.filters);
        this.setState({userFormKey: "formUserAdmin" + new Date().getTime()});
    };

    openEditUser = (user) => {
        this.setState({currentEditUser: user, showManageUser: true});
    };

    closeEditUser = () => {
        if (this.state.showManageUser) {
            this.setState({showManageUser: false});
        }
    };

    userRoles = (roles) => {
        return roles ? roles.filter(r => r)
            .map(r => r.replace('ROLE_', ''))
            .map(r => r.toLowerCase())
            .map(r => r.charAt(0).toUpperCase() + r.slice(1))
            .join(', ') : '';
    };

    deleteUser = (user) => {
        this.setState({isLoading: true});
        deleteUser(user.id).then(() => {
            this.reloadPage();
        });
    };

    editButtons = (user) => {
        const key = user.id;
        const userDeleteLink = this.state.currentUser && this.state.currentUser.id !== user.id ?
            <Popconfirm title={this.props.t('Do you want to delete this user?')} onConfirm={() => {
                this.deleteUser(user)
            }} cancelText={this.props.t('Cancel')}>
                <Tooltip placement="topLeft" title={this.props.t('Delete')}>
                    <a href="#2"><Icon type="delete" theme="twoTone"/></a>
                </Tooltip>
            </Popconfirm> :
            <Icon type="delete" theme="outlined"/>;
        return <div className="edit-user-actions">
                <span onClick={(e) => this.openEditUser(user)} key={"edit" + key}>
                <Tooltip placement="topLeft" title={this.props.t('Edit')}>
                    <a href="#edit">
                        <Icon type="edit" theme="twoTone"/>
                    </a>
                </Tooltip>
            </span>
            <Divider type="vertical"/>
            {userDeleteLink}
        </div>
    };

    userRole(user) {
        return user ? this.userRoles([user.role]) : '';
    }

    componentDidUpdate() {
        const values = this.props;
        if (!this.state.propValues || this.state.propValues !== values) {
            this.setState({
                propValues: values,
                currentUser: this.props.currentUser
            });
        }
    }

    processPaginatedRequest(page, rows, filters) {
        return getUsers(page, rows, filters);
    }

    loadFilters() {
        return getUserFilters();
    }

    handleChange(value, key) {
        this.setState({
            [key]: value
        }, () => {
            this.resetAll(this.onPage(0));
        });
    }

    renderView() {
        const columns = [{
            dataIndex: 'enabled',
            key: 'enabled',
            render: enabled => <Icon style={{color: enabled ? '#008' : 'red'}} type={enabled ? 'check' : 'close'}/>,
            width: '2em',
            align: 'center'
        }, {
            title: this.props.t('Email'),
            dataIndex: 'email',
            key: 'email',
            filterDropdown: ({setSelectedKeys, selectedKeys, confirm, clearFilters}) => (
                <DialogTextInputFilter
                    placeholder={this.props.t('Email')}
                    selectedKeys={selectedKeys}
                    onChange={e => setSelectedKeys(e.target.value ? [e.target.value] : [])}
                    onSearch={this.handleSearch(selectedKeys, confirm)}
                    onReset={this.handleReset(clearFilters)}/>
            ),
        }, {
            title: this.props.t('Name'),
            dataIndex: 'name',
            key: 'name',
            filterDropdown: ({setSelectedKeys, selectedKeys, confirm, clearFilters}) => (
                <DialogTextInputFilter
                    placeholder={this.props.t('Name')}
                    selectedKeys={selectedKeys}
                    onChange={e => setSelectedKeys(e.target.value ? [e.target.value] : [])}
                    onSearch={this.handleSearch(selectedKeys, confirm)}
                    onReset={this.handleReset(clearFilters)}/>
            ),
        }, {
            title: this.props.t('Permissions'),
            key: 'roles',
            render: user => {
                return this.userRole(user);
            }
        }, {
            key: 'operations',
            render: (user) => <div>{
                this.editButtons(user)
            }</div>,
            width: '5em',
            align: 'center'
        }];

        return <div>
            <div className="action-separator">
                <Button key="inviteAddBtn"
                        type="primary"
                        className="mrg-right"
                        onClick={() => {
                            this.openEditUser({})
                        }}>
                    <Icon type="plus"/>
                    {this.props.t('Add')}
                </Button>
            </div>
            <UsersManage
                user={this.state.currentEditUser ? this.state.currentEditUser : {}}
                groups={this.state.groups}
                actions={this.state.actions}
                currentUser={this.state.currentUser}
                key={this.state.userFormKey ? this.state.userFormKey : 'formUserAdmin'} isAdmin={this.state.isAdmin}
                showEdit={this.state.showManageUser}
                onSave={this.reloadPage} onDelete={this.reloadPage} onClose={this.closeEditUser}/>

            <Table dataSource={this.state.data} columns={columns} bordered loading={this.state.loading}
                   pagination={this.getPagination()} size="small" onChange={this.handleTableChange} rowKey="id"/>
        </div>
    }
}

export default translate('common')(Users);