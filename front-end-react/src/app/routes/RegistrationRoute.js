import React, {Component} from 'react';
import {Redirect, withRouter} from 'react-router-dom';

class RegistrationRoute extends Component {

    render() {
        return (
            !this.props.isAuthenticated ?
                this.props.component
                :
                <Redirect to='/'/>
        );
    }
}

export default withRouter(RegistrationRoute);
