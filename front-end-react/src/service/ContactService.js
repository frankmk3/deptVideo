import {API_BASE_URL, PATH_CONTACTS} from '../constants/index';
import {getFiltersAsQuery} from "../util/SearchUtils";
import {request} from "../util/APIUtils";


export function getContacts(page, size, filters) {
    return request({
        url: API_BASE_URL + PATH_CONTACTS + '?page=' + page
        + '&size=' + size
        + '&q=' + getFiltersAsQuery(filters),
        method: 'GET'
    }, []);
}

export function saveContact(contact) {
    return request({
        url: API_BASE_URL + PATH_CONTACTS,
        method: 'POST',
        body: JSON.stringify(contact)
    }, []);
}


export function updateContact(contact) {
    return request({
        url: API_BASE_URL + PATH_CONTACTS + "/" + contact.id,
        method: 'PUT',
        body: JSON.stringify(contact)
    }, []);
}
