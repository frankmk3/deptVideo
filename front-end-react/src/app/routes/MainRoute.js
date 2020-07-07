import React, {Component} from 'react';
import {withRouter} from 'react-router-dom';
import Login from '../../user/login/Login';
import Search from '../../video/search/Search';

class MainRoute extends Component {

    render() {
        return (
            this.props.isAuthenticated ?
                <div>
                   <Search/>
                </div>
                : <Login onLogin={this.props.onLogin}/>
        );
    }
}

export default withRouter(MainRoute);
