import React, {Component} from 'react';
import './DialogTextInputFilter.css';
import {Input, Button} from 'antd';
import {translate} from "react-i18next";

class DialogTextInputFilter extends Component {

    render() {
        return <div className="custom-filter-dropdown">
            <Input
                placeholder={this.props.placeholder}
                value={this.props.selectedKeys}
                onChange={this.props.onChange}
                onPressEnter={this.props.onSearch}
            />
            <Button type="primary" onClick={this.props.onSearch}>OK</Button>
            <Button onClick={this.props.onReset}>
                {this.props.t('Reset')}
            </Button>
        </div>
    }
}

export default translate('common') (DialogTextInputFilter);