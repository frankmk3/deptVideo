import React, {Component} from 'react';
import {Redirect, withRouter} from 'react-router-dom';

class DashboardRoute extends Component {

    render() {
        return (
            this.props.isAuthenticated && this.props.isAdmin ? "users":
                <Redirect to={{pathname: '/', state: {from: this.props.location}}}/>
        );
    }
}

export default withRouter(DashboardRoute);
