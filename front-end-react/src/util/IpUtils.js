import {RESOLVE_IP_DATA_URL} from '../constants';

export function getGeoDataInformation() {
    return fetch(RESOLVE_IP_DATA_URL)
        .then(response => response.json())
        .then(data => {return data}).catch((error)=>{return null;});
}