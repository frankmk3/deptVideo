import React, {Component} from 'react';
import {getVideos} from '../../service/SearchService';
import './Search.css';
import Detail from '../detail/Detail';
import {Badge, Card, Col, Empty, Form, Icon, Input, Pagination, Spin} from 'antd';
import {translate} from "react-i18next";
import {withRouter} from 'react-router-dom';
import queryString from 'query-string'
import {LazyLoadImage} from 'react-lazy-load-image-component';

const FormItem = Form.Item;
const {Search: SearchInput} = Input;
const {Meta} = Card;

class SearchApp extends Component {

    searchVideos = (text, page) => {
        if (text) {
            this.props.history.push("?q=" + text + "&page=" + page);
            this.setState({
                isLoading: true,
                renderDetail: false,
                currentVideo: {},
                search: text,
                page: page,
                init: true
            });
            getVideos(page - 1, 20, text).then(response => {
                const content = [];
                if (response && response.content) {
                    response.content.forEach(function (entry) {
                        content.push(entry)
                    });

                    this.setState({
                        searchResult: content,
                        page: response.number + 1,
                        total: response.totalElements
                    });
                }
            }).finally(() => {
                this.setState({
                    isLoading: false
                })
            });
        }
    };

    constructor(props) {
        super(props);
        const values = queryString.parse(this.props.location.search);
        const search = values.q;
        const page = values.page ? parseInt(values.page, 10) : 1;
        this.state = {
            search: search,
            searchResult: [],
            isLoading: false,
            page: page,
            total: 0,
            init: false,
        };
    }


    componentDidMount() {
        if (this.state.search && !this.state.init) {
            this.searchVideos(this.state.search, this.state.page)
        }
    }

    render() {
        const {form} = this.props;
        const {getFieldDecorator} = form;
        const loader = <Spin spinning={this.state.isLoading} className="loader"/>;

        const existElements = this.state.searchResult && this.state.searchResult.length;
        const pager = existElements && !this.state.renderDetail ?
            <Pagination style={{marginBottom:"10px"}} defaultCurrent={this.state.page} total={this.state.total} pageSize={20}
                        onChange={(page, pageSize) => {
                            this.searchVideos(this.state.search, page);
                        }}/> : null;
        const details = this.state.renderDetail ? <Detail key={"details" + this.state.video.id} onClose={() => {
            this.setState({"renderDetail": false, "video": {}})
        }} video={this.state.video} className="loader"/> : null;
        let videoCount = 0;
        const searchResults = !this.state.isLoading && existElements && !this.state.renderDetail ?
            this.state.searchResult.map((videoInfo) =>
                <Col onClick={() => {
                    this.setState({"renderDetail": true, "video": videoInfo})
                }} xs={24} sm={12} md={8} lg={6} key={"video" + (videoCount++)}> <Card
                    hoverable
                    style={{width: 220}}
                    cover={
                        <LazyLoadImage alt={videoInfo.title}
                                       src={videoInfo.posters && videoInfo.posters.length && videoInfo.posters[0] ? videoInfo.posters[0] : "/default.jpg"}
                                       height={327}/>}
                    actions={[<Badge count={videoInfo.videos.length} showZero> <Icon type="youtube"/> </Badge>]}
                >
                    <Meta title={videoInfo.title}/>
                </Card>
                </Col>
            ) : !this.state.renderDetail ? <Empty/> : null;
        return (
            <div>

                <Form onSubmit={this.handleSubmit} className="search-form">
                    <FormItem>
                        {getFieldDecorator('search', {
                            rules: [{required: true, message: this.props.t('This field is required')}],
                            initialValue: this.state.search
                        })(
                            <SearchInput placeholder={this.props.t('Input search text')} onSearch={(value) => {
                                this.searchVideos(value, 1)
                            }} enterButton disabled={this.state.isLoading}/>
                        )}
                    </FormItem>
                </Form>
                {pager}
                {loader}
                {details}
                {searchResults}
            </div>
        );
    }
}

const Search = Form.create()(translate('common')(withRouter(SearchApp)));
export default Search;