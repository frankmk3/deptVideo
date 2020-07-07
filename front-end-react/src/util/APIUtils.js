import {DATA_ACCESS_TOKEN, TIMEOUT} from '../constants';
import {Modal} from "antd/lib/index";
import Fingerprint2 from 'fingerprintjs2';
import {getGeoDataInformation} from "./IpUtils";

function timeout(promise, time = TIMEOUT) {
    return new Promise(function (resolve, reject) {
        setTimeout(function () {
            reject(new Error("timeout"))
        }, time);
        promise.then(resolve, reject)
    })
}

export const request = (options, skipErrorCodes) => {
    const headers = new Headers({
        'Content-Type': 'application/json',
    });

    if (localStorage.getItem(DATA_ACCESS_TOKEN)) {
        headers.append('Authorization', localStorage.getItem(DATA_ACCESS_TOKEN))
    }

    const defaults = {headers: headers};
    options = Object.assign({}, defaults, options);

    return timeout(fetch(options.url, options))
        .then(response => {
            if (!response.ok) {
                return processErrors(response, skipErrorCodes);
            }
            return response.json()
                .then(json => {
                    return json;
                })
        })
        .catch(error => {
            processErrors(error, skipErrorCodes);
        });
};

export const run = (options, skipErrorCodes) => {
    const headers = new Headers({
        'Content-Type': 'application/json',
    });

    if (localStorage.getItem(DATA_ACCESS_TOKEN)) {
        headers.append('Authorization', localStorage.getItem(DATA_ACCESS_TOKEN))
    }

    const defaults = {headers: headers};
    options = Object.assign({}, defaults, options);

    return timeout(fetch(options.url, options));
};


export const download = (options, skipErrorCodes) => {
    const headers = new Headers({
        'Content-Type': 'application/json',
    });

    if (localStorage.getItem(DATA_ACCESS_TOKEN)) {
        headers.append('Authorization', localStorage.getItem(DATA_ACCESS_TOKEN))
    }

    const defaults = {headers: headers};
    options = Object.assign({}, defaults, options);

    let a = document.createElement("a");
    document.body.appendChild(a);
    a.style = "display: none";
    return timeout(fetch(options.url, options))
        .then(response => {
            if (!response.ok) {
                return processErrors(response, skipErrorCodes);
            }
            return response.blob().then(blob => {
                let urlObject = window.URL.createObjectURL(blob);
                a.href = urlObject;
                a.download = options.fileName;
                a.click();
                window.URL.revokeObjectURL(urlObject);
                return blob;
            })
        })
        .catch(error => {
            processErrors(error, skipErrorCodes);
        });
};

export function showError(content, title = 'Error') {
    Modal.error({
        title: title,
        content: content
    });
}

export function showInfo(content, title = 'Info') {
    Modal.info({
        title: title,
        content: content
    });
}

function processErrors(error, skipErrorCodes) {
    if (error.status === 401 && !skipErrorCodes.includes(401)) {
        window.app.handleLogout();
    } else if (error.status === 403 && !skipErrorCodes.includes(403)) {
        showError('Sorry! You don\'t have access to this resource')
    } else if (error.status === 500 && !skipErrorCodes.includes(500)) {
        showError('Sorry! Something went wrong in the server. ' + error.message)
    } else {
        throw error;
    }
}

export function getFingerPrint() {
    const options = {
        excludes: {
            screenResolution: true,
            availableScreenResolution: true,
            webgl: true,
            canvas: true,
            pixelRatio: true,
            enumerateDevices: true,
            hasLiedBrowser: true,
            hasLiedOs: true,
            hasLiedResolution: true,
            hasLiedLanguages: true,
            adBlock: true,
            audio: true,
            fontsFlash: true,
            touchSupport: true,
            plugins: true,
            fonts: true
        },
        extraComponents: [
            {
                key: 'countryName', getData: function (done, options) {
                    getGeoDataInformation().then(geoDataInformation => {
                        done(geoDataInformation?geoDataInformation.country_name:"");
                    })
                }
            }
        ]
    };

    return new Promise(function (resolve, reject) {
        Fingerprint2.get(options, function (components) {
            const values = components.map(function (component) {
                return component.value
            });
            const hash = Fingerprint2.x64hash128(values.join(''), 31);

            let componentMap = {};
            components.map(component => {
              return componentMap[component.key] = component.value;
            });

            resolve({
                'hash': hash,
                'data': componentMap
            });
        });
    });
}