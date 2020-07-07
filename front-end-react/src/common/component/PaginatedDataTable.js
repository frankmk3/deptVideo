import React, {Component} from 'react';
import NotFound from '../../common/NotFound';
import ServerError from '../../common/ServerError';
import {CONFIG_PAGE_ROWS} from "../../constants";
import {Spin} from 'antd';
import {refreshToken} from "../../service/UsersService";

class PaginatedDataTable extends Component {

    handleSearch = (selectedKeys, confirm) => () => {
        confirm();
        this.setState({customFilterEntries: selectedKeys});
    };

    handleReset = clearFilters => () => {
        clearFilters();
        this.setState({customFilterEntries: ''});
    };

    constructor(props) {
        super(props);
        this.state = {
            isLoading: false,
            page: 0,
            rows: CONFIG_PAGE_ROWS,
            total: 0,
            customFilterEntries: [],
            editVisible: false
        };
        this.onPage = this.onPage.bind(this);
        this.handleTableChange = this.handleTableChange.bind(this);
    }

    resetAll(callback) {
        this.setState({
            isLoading: false,
            page: 0,
            rows: CONFIG_PAGE_ROWS,
            total: 0,
            customFilterEntries: [],
            editVisible: false
        }, () => callback);
    }

    handleTableChange(pagination, filters, sorters) {
        this.setState({
            filters: filters,
            sorters: sorters,
        });
        let current = pagination && pagination.current ? pagination.current : 1;
        this.onPage(current - 1, filters)
    }

    onPage(page, filters) {
        this.setState({
            loading: true
        });

        this.processPaginatedRequest(page, this.state.rows, filters).then(response => {
            const content = [];
            if (response && response.content) {
                response.content.forEach(function (entry) {
                    content.push(entry)
                });

                this.setState({
                    data: content,
                    page: page,
                    total: response.totalElements,
                    loading: false,
                    isLoading: false
                });
            }
        });
    }

    getPagination() {
        return {
            pageSize: this.state.rows,
            total: this.state.total,
            showTotal: (total, range) => `${range[0]}-${range[1]} ${this.props.t('of')} ${total} ${this.props.t('items')}`
        }
    }

    componentDidMount() {
        this.setState({
            isLoading: true
        });
        if (this.loadFilters) {
            this.loadFilters().then(response => {
                this.setState({
                    filterSource: response
                });
            });
        }
        this.onPage(0);
        refreshToken();
    }

    render() {
        if (this.state.isLoading) {
            return <Spin size="large"/>;
        }

        if (this.state.notFound) {
            return <NotFound/>;
        }

        if (this.state.serverError) {
            return <ServerError/>;
        }

        return <div className="profile">
            {
                this.state.data ? (this.renderView()) : <p>Without results for now...</p>
            }
        </div>
    }
}

export default PaginatedDataTable;