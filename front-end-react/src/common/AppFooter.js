import React, {Component} from 'react';
import {withRouter} from 'react-router-dom';
import './AppFooter.css';
import {translate} from "react-i18next";


class AppFooter extends Component {

    render() {
        return <div></div>
    }
}

export default translate('common')(withRouter(AppFooter));