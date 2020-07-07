import React from 'react';
import {getContacts, updateContact} from "../../service/ContactService";
import PaginatedDataTable from "../../common/component/PaginatedDataTable";
import {Icon, Popconfirm, Table, Tooltip} from 'antd';
import DialogTextInputFilter from "../../common/component/DialogTextInputFilter";
import {translate} from "react-i18next";
import {notification} from "antd/lib/index";

class Contacts extends PaginatedDataTable {

    setAsReviewedContact = (contact) => {
        contact.reviewed = true;
        updateContact(contact).then(contactFromDb => {
            this.reloadPage();
        }).catch(error => {
            notification.error({
                message: 'Dept video',
                description: error.message || this.props.t('expressions.sorry_something_went_wrong')
            })
        });

    };

    reloadPage = () => {
        this.onPage(this.state.page, this.state.filters);
        this.setState({contactFormKey: "formContactAdmin" + new Date().getTime()});
    };

    openEditContact = (contact) => {
        this.setState({currentEditContact: contact, showManageContact: true});
    };

    closeEditContact = () => {
        if (this.state.showManageContact) {
            this.setState({showManageContact: false});
        }
    };

    editButtons = (contact) => {
        return !contact.reviewed ?
            <Popconfirm title={this.props.t('Do you want to mark as reviewed this contact?')} onConfirm={() => {
                this.setAsReviewedContact(contact);
            }} cancelText={this.props.t('Cancel')}>
                <Tooltip placement="topLeft" title={this.props.t('Check as reviewed')}>
                    <a href="#2"><Icon type="check-square" theme="twoTone"/></a>
                </Tooltip>
            </Popconfirm> :
            <Icon type="check-square" theme="outlined"/>;

    };

    componentDidUpdate() {
        const values = this.props;
        if (!this.state.propValues || this.state.propValues !== values) {
            this.setState({
                propValues: values,
                currentContact: this.props.currentContact
            });
        }
    }

    processPaginatedRequest(page, rows, filters) {
        return getContacts(page, rows, filters);
    }

    renderView() {
        const columns = [{
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
            title: this.props.t('Comment'),
            dataIndex: 'comment',
            key: 'comment',
            filterDropdown: ({setSelectedKeys, selectedKeys, confirm, clearFilters}) => (
                <DialogTextInputFilter
                    placeholder={this.props.t('Comment')}
                    selectedKeys={selectedKeys}
                    onChange={e => setSelectedKeys(e.target.value ? [e.target.value] : [])}
                    onSearch={this.handleSearch(selectedKeys, confirm)}
                    onReset={this.handleReset(clearFilters)}/>
            ),
        }, {
            title: this.props.t('Reviewed'),
            key: 'reviewed',
            render: (contact) => <div>{
                this.editButtons(contact)
            }</div>,
            width: '5em',
            align: 'center'
        }];

        return <div>
            <Table dataSource={this.state.data} columns={columns} bordered loading={this.state.loading}
                   pagination={this.getPagination()} size="small" onChange={this.handleTableChange} rowKey="id"/>
        </div>
    }
}

export default translate('common')(Contacts);