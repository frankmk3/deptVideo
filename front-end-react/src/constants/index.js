export const API_BASE_URL = process.env.REACT_APP_API_BASE_URL ? process.env.REACT_APP_API_BASE_URL : 'http://localhost:1888/v1/dept-video';

export const TIMEOUT = 20000;

export const PATH_CONTACTS = '/contacts';
export const PATH_USERS = '/users';
export const PATH_VIDEO_SEARCH = '/video/search';
export const PATH_FILTERS = '/filters';
export const PATH_UI_FORGOT_PASSWORD = '/password/forgot';
export const PATH_UI_ENTER_NEW_PASSWORD = '/password/new';
export const PATH_LOGIN = PATH_USERS + '/login';
export const PATH_CHECK_EMAIL_AVAILABILITY = PATH_USERS + '/checkEmailAvailability';
export const PATH_FORGOT_PASSWORD = PATH_USERS + '/password/forgot';
export const PATH_PASSWORD_TOKEN_VALIDATION = PATH_USERS + '/password/token/';
export const PATH_NEW_PASSWORD = PATH_USERS + '/password/new/';
export const PATH_REFRESH_TOKEN = PATH_USERS + '/refresh-token';
export const PATH_INVALIDATE = '/invalidateSessions';
export const PATH_INVALIDATE_LOCK = '?lock=';
export const PATH_CHANGE_PASSWORD = '/changePassword';

export const USER_PROPERTY_NOTIFICATION = 'action-notification';
export const USER_PROPERTY_CONFIRMATION = 'action-confirmation';
export const USER_PROPERTY_CONNECTION_NOTIFICATION = 'client-connection-notification';

export const CONFIG_PAGE_ROWS = process.env.REACT_APP_CONFIG_PAGE_ROWS ? process.env.REACT_APP_CONFIG_PAGE_ROWS : 20;

export const DATA_ACCESS_TOKEN = process.env.REACT_APP_DATA_ACCESS_TOKEN ? process.env.REACT_APP_DATA_ACCESS_TOKEN : 'accessToken';
export const DATA_USER_DATA_KEY = process.env.REACT_APP_DATA_USER_DATA_KEY ? process.env.REACT_APP_DATA_USER_DATA_KEY : 'userData';
export const DATA_USER_LAN = process.env.REACT_APP_DATA_USER_LAN ? process.env.REACT_APP_DATA_USER_LAN : 'i18nextLng';
export const DATA_RELOGIN = process.env.REACT_APP_DATA_RELOGIN ? process.env.REACT_APP_DATA_RELOGIN : 'relogin';
export const DATA_CURRENT_CLIENT = process.env.REACT_APP_DATA_CURRENT_CLIENT ? process.env.REACT_APP_DATA_CURRENT_CLIENT : 'currentClient';

export const SECURITY_ROLE_ADMIN = process.env.REACT_APP_SECURITY_ROLE_ADMIN ? process.env.REACT_APP_SECURITY_ROLE_ADMIN : 'ROLE_ADMIN';
export const SECURITY_ROLE_NONE_SET_PASSWORD = process.env.REACT_APP_SECURITY_ROLE_USER ? process.env.REACT_APP_SECURITY_ROLE_NONE_SET_PASSWORD : 'ROLE_NONE_SET_PASSWORD';
export const SECURITY_ROLE_USER = process.env.REACT_APP_SECURITY_ROLE_USER ? process.env.REACT_APP_SECURITY_ROLE_USER : 'ROLE_USER';

export const ROUTE_PUBLIC_INVITE = process.env.REACT_APP_ROUTE_PUBLIC_INVITE ? process.env.REACT_APP_ROUTE_PUBLIC_INVITE : '/public/invite';


export const PASSWORD_MIN_LENGTH = 4;
export const PASSWORD_MAX_LENGTH = 100;
export const NAME_MIN_LENGTH = 3;
export const NAME_MAX_LENGTH = 100;
export const RESOLVE_IP_DATA_URL = "https://geoip-db.com/json/";
export const SOURCE_INTERNAL = "Internal";