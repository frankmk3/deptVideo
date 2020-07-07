import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './app/App';
import registerServiceWorker from './registerServiceWorker';
import { BrowserRouter as Router } from 'react-router-dom';

import i18next from 'i18next';
import {I18nextProvider} from 'react-i18next';
import LngDetector from 'i18next-browser-languagedetector';
import common_en from "./translations/en/common.json";

i18next.use(LngDetector).init({
    interpolation: { escapeValue: false },  // React already does escaping
    // lng: 'en',                              // language to use
    resources: {
        en: {
            common: common_en               // 'common' is our custom namespace
        },
    },
});

ReactDOM.render(
    <Router>
        <I18nextProvider i18n={i18next}>
        <App />
        </I18nextProvider>
    </Router>, 
    document.getElementById('root')
);

registerServiceWorker();
