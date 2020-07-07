import React, {Component} from 'react';
import './Detail.css';
import {Button, Card, Col, Drawer, Form, Icon, message, PageHeader, Row, Skeleton, Tag, Tooltip} from 'antd';
import {translate} from "react-i18next";
import ReactPlayer from 'react-player';
import {EmailShareButton, FacebookShareButton, LinkedinShareButton, TwitterShareButton,} from 'react-share';
import {CopyToClipboard} from 'react-copy-to-clipboard';

import Contact from '../../user/contact/Contact';

const {Meta} = Card;

class DetailApp extends Component {

    onClose = () => {
        if (this.props.onClose) {
            this.props.onClose();
        }
    };
    getVideoUrl = (video) => {
        let videoUrl = "#";
        if (video.site === "YouTube") {
            videoUrl = "https://www.youtube.com/watch?v=" + video.key
        }
        return videoUrl;
    };
    onCloseDrawer = () => {
        this.setState({visible: false});
    };

    constructor(props) {
        super(props);

        this.state = {
            isLoading: false,
            visible: false,
            video: props.video,
            activeVideo: null,
            playing: false
        };
    }

    componentDidMount() {
        if (this.state.video.videos && this.state.video.videos.length) {
            this.setState({activeVideo: this.getVideoUrl(this.state.video.videos[0])});
        }
    }

    render() {
        const videoInfo = this.state.video;
        const iconStyle = {fontSize: '28px', color: '#08c'};
        const player = this.state.activeVideo ?
            <div className="social-share hover-action">
                <ReactPlayer url={this.state.activeVideo} playing={this.state.playing} controls/>
                <div className="social-share-action">
                <FacebookShareButton url={this.state.activeVideo}>
                    <Tooltip placement="top" title={this.props.t("Share on Facebook")}>
                        <Icon style={iconStyle}
                              type="facebook"
                              theme="outlined"/>
                    </Tooltip>
                </FacebookShareButton>
                <TwitterShareButton url={this.state.activeVideo}><Tooltip placement="top"
                                                                          title={this.props.t("Share on Twitter")}>
                    <Icon style={iconStyle}
                          type="twitter"
                          theme="outlined"/>
                </Tooltip>
                </TwitterShareButton>
                <EmailShareButton url={this.state.activeVideo}><Tooltip placement="top"
                                                                        title={this.props.t("Share by email")}>
                    <Icon style={iconStyle}
                          type="mail" theme="outlined"/>
                </Tooltip>
                </EmailShareButton>
                <LinkedinShareButton url={this.state.activeVideo}>
                    <Tooltip placement="top" title={this.props.t("Share on Linkedin")}>
                        <Icon style={iconStyle}
                              type="linkedin"
                              theme="outlined"/>
                    </Tooltip>
                </LinkedinShareButton>
                <CopyToClipboard text={this.state.activeVideo}
                                 onCopy={() => message.success(this.props.t('Copied to clipboard'))}>
                    <Tooltip placement="top" title={this.props.t("Copy to clipboard")}>
                        <Icon style={iconStyle}
                              type="copy"
                              theme="outlined"/>
                    </Tooltip>
                </CopyToClipboard>
            </div></div> : <Skeleton paragraph={{rows: 10}}/>;
        const videos = videoInfo.videos ? videoInfo.videos.map((video) =>
            <Col xs={24} sm={12} md={8} lg={6} key={"video" + video.key}>
                <div className={"hover-action"} onClick={() => {
                    this.setState({"activeVideo": this.getVideoUrl(video), "playing":true})
                }}>
                    <img alt={video.title}
                         src={video.thumbnails && Object.keys(video.thumbnails).length ? video.thumbnails[Object.keys(video.thumbnails)[0]].url : "/default.jpg"}
                         height={150}/>
                    <h4>{video.title}</h4>
                </div>
            </Col>
        ) : null;
        return (
            <div>
                <Row key={"details-key" + videoInfo.id}>
                    <PageHeader onBack={() => this.onClose()} title={videoInfo.title} subTitle={videoInfo.tagline}/>
                    <Col xs={24} sm={12} md={8}><Card
                        hoverable
                        style={{width: 220}}
                        cover={
                            <img alt={videoInfo.title}
                                 src={videoInfo.posters && videoInfo.posters.length && videoInfo.posters[0] ? videoInfo.posters[0] : "/default.jpg"}
                            />
                        }
                    >
                        <Meta title={videoInfo.title}/>
                    </Card></Col>
                    <Col xs={24} sm={12} md={16}> {player}</Col>
                    <Col span={24}><h4
                        style={{marginRight: 10, display: 'inline'}}>{this.props.t('Request new trailer')}:</h4>
                        <Button type="primary" shape="circle" icon="video-camera" size="default" onClick={() => {
                            this.setState({visible: true})
                        }}/>
                    </Col>
                    <Col span={24}><h4>{this.props.t('Videos')}:</h4> {videos}</Col>
                    <Col span={24}><h4 style={{
                        marginRight: 10,
                        display: 'inline'
                    }}>{this.props.t('Overview')}:</h4> {videoInfo.overview}</Col>
                    <Col span={24}><br/></Col>
                    <Col span={24}>
                        <div>
                            <h4 style={{marginRight: 10, display: 'inline'}}>{this.props.t('Genres')}:</h4>
                            {videoInfo.genres.map(genre => (
                                <Tag key={genre}>
                                    {genre}
                                </Tag>
                            ))}
                        </div>
                    </Col>
                </Row>

                <Drawer
                    title="Trailer request"
                    visible={this.state.visible}
                    placement="right"
                    onClose={this.onCloseDrawer}
                    width={520}
                >
                    <Contact name={this.state.video.title} onSend={this.onCloseDrawer}/>
                </Drawer>
            </div>
        );
    }
}

const Detail = Form.create()(translate('common')(DetailApp));
export default Detail;