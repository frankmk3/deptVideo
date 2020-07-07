import {
    API_BASE_URL,
    DATA_USER_DATA_KEY,
    PATH_CHANGE_PASSWORD,
    PATH_CHECK_EMAIL_AVAILABILITY,
    PATH_FILTERS,
    PATH_FORGOT_PASSWORD,
    PATH_INVALIDATE,
    PATH_INVALIDATE_LOCK,
    PATH_LOGIN,
    PATH_NEW_PASSWORD,
    PATH_PASSWORD_TOKEN_VALIDATION,
    PATH_REFRESH_TOKEN,
    PATH_USERS,
    SECURITY_ROLE_ADMIN,
    SECURITY_ROLE_NONE_SET_PASSWORD
} from '../constants/index';
import {getFiltersAsQuery} from "../util/SearchUtils";
import {getFingerPrint, request, run} from "../util/APIUtils";
import {DATA_ACCESS_TOKEN, DATA_USER_LAN} from "../constants";

export function login(loginRequest) {
    return buildLoginRequest(API_BASE_URL + PATH_LOGIN, loginRequest);
}

function buildLoginRequest(url, loginRequest) {
    return getFingerPrint().then(response => {
        loginRequest.fingerPrint = response;
        loginRequest.fingerPrint.data.properties = getUserLoginProperties();
        return executeLogin(url, loginRequest);
    });
}

function getUserLoginProperties() {
    return Object.keys(localStorage).filter(key => key.startsWith("P_")).map(key => {
        return {"key": key, "value": localStorage[key]}
    });
}

function executeLogin(url, loginRequest) {
    return request({
        url: url,
        method: 'POST',
        body: JSON.stringify(loginRequest)
    }, [403])
}

export function refreshToken() {
    run({
        url: API_BASE_URL + PATH_REFRESH_TOKEN,
        method: 'POST'
    }, [400, 401, 403, 404, 406, 500]).then(response => {
        if (response.ok) {
            response.text().then(text => {
                localStorage.setItem(DATA_ACCESS_TOKEN, text);
            })
        }
    });
}

export function invalidateSessions(user, lock, callback) {
    run({
        url: API_BASE_URL + PATH_USERS + '/' + user.id + PATH_INVALIDATE + PATH_INVALIDATE_LOCK + lock,
        method: 'PUT'
    }, [400, 401, 403, 404, 406, 500]).then(response => {
        if (response.ok) {
            response.text().then(callback);
        }
    });
}

export function setUserLocalInfo(user) {
    localStorage.setItem(DATA_USER_DATA_KEY, JSON.stringify(user));
}

export function getUsers(page, size, filters) {
    return request({
        url: API_BASE_URL + PATH_USERS + '?page=' + page
        + '&order=email:ASC&size=' + size
        + '&q=' + getFiltersAsQuery(filters),
        method: 'GET'
    }, []);
}

export function getUserProfile() {
    return JSON.parse(localStorage.getItem(DATA_USER_DATA_KEY));
}

export function getUserProfileProperties() {
    const profile = getUserProfile();
    if (profile && profile.properties) {
        return profile.properties;
    }
    return {};
}

export function getUserProfileProperty(key, defaultValue) {
    return getUserProfileProperties()[key] !== undefined ? getUserProfileProperties()[key] : defaultValue;
}

export function setUserProfileProperty(key, value, removeOnEmpty = false) {
    const profile = getUserProfile();
    let properties = getUserProfileProperties();
    properties[key] = value;
    if (removeOnEmpty && !value) {
        delete properties[key];
    }
    profile.properties = properties;
    return profile;
}

export function getUserFilters() {
    return request({
        url: API_BASE_URL + PATH_USERS + PATH_FILTERS,
        method: 'GET'
    }, []);
}

export function hasRole(user, role) {
    return user && user.role && user.role === role;
}

export function isAdmin(user) {
    return hasRole(user, SECURITY_ROLE_ADMIN);
}

export function isAdminCurrentUser() {
    return isAdmin(getUserProfile());
}

export function isSetPasswordRequired() {
    return hasRole(getUserProfile(), SECURITY_ROLE_NONE_SET_PASSWORD);
}


export function saveUserDetails(userdetails) {
    localStorage.setItem(DATA_ACCESS_TOKEN, userdetails.token);
    setUserLocalInfo(userdetails);
    if (userdetails.lang) {
        localStorage.setItem(DATA_USER_LAN, userdetails.lang);
    }
    if (userdetails.properties) {
        let properties = userdetails.properties;
        Object.keys(properties).forEach(key => localStorage.setItem("P_" + key, properties[key]))
    }
}

export function getCurrentLanguage() {
    return localStorage.getItem(DATA_USER_LAN);
}

export function updateUser(user) {
    if (user.id) {
        return request({
            url: API_BASE_URL + PATH_USERS + "/" + user.id ,
            method: 'PUT',
            body: JSON.stringify(user)
        }, []);
    } else {
        user.id = user.email;
        return request({
            url: API_BASE_URL + PATH_USERS,
            method: 'POST',
            body: JSON.stringify(user)
        }, []);
    }
}

export function changeUserPassword(userId, currentPassword, pass) {
    const changeUserPassword = {userId: userId, oldPass: currentPassword, pass: pass};
    return request({
        url: API_BASE_URL + PATH_USERS + "/" + userId + PATH_CHANGE_PASSWORD,
        method: 'PATCH',
        body: JSON.stringify(changeUserPassword)
    }, [403]);
}

export function updateOwnUserInfo(user) {
    return request({
        url: API_BASE_URL + PATH_USERS,
        method: 'PUT',
        body: JSON.stringify(user)
    }, []);
}

export function deleteUser(id) {
    return run({
        url: API_BASE_URL + PATH_USERS + '/' + id,
        method: 'DELETE'
    }, []);
}

export function checkEmailAvailability(email) {
    return request({
        url: API_BASE_URL + PATH_CHECK_EMAIL_AVAILABILITY + "?email=" + email,
        method: 'GET'
    });
}

export function forgotPasswordRequest(passwordChangeRequest) {
    return request({
        url: API_BASE_URL + PATH_FORGOT_PASSWORD,
        method: 'POST',
        body: JSON.stringify(passwordChangeRequest)
    });
}

export function passwordTokenValidation(token) {
    return request({
        url: API_BASE_URL + PATH_PASSWORD_TOKEN_VALIDATION + token,
        method: 'POST',
        body: JSON.stringify(token)
    });
}

export function changePassword(passwordChangeRequest, token) {
    return request({
        url: API_BASE_URL + PATH_NEW_PASSWORD + token,
        method: 'POST',
        body: JSON.stringify(passwordChangeRequest)
    });
}
