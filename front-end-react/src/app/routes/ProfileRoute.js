import React, {Component} from 'react';
import {Redirect, withRouter} from 'react-router-dom';
import Profile from '../../user/profile/Profile';

class ProfileRoute extends Component {

    render() {
        return (
            this.props.isAuthenticated ?
                <Profile isAuthenticated={this.props.isAuthenticated}
                         currentUser={this.props.currentUser} {...this.props}  />
                :
                <Redirect to={{pathname: '/', state: {from: this.props.location}}}/>
        );
    }
}

export default withRouter(ProfileRoute);
