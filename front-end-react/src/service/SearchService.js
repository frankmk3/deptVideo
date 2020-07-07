import {
    API_BASE_URL,
    PATH_VIDEO_SEARCH
} from '../constants/index';

import {request} from "../util/APIUtils";

export function getVideos(page, size, query) {
    return request({
        url: API_BASE_URL + PATH_VIDEO_SEARCH + '?page=' + page
        // + '&order=email:ASC&size=' + size
        + '&q=' +query,
        method: 'GET'
    }, []);
}
