import {NAME_MAX_LENGTH, NAME_MIN_LENGTH, PASSWORD_MAX_LENGTH, PASSWORD_MIN_LENGTH} from "../constants";

export function validateEmail(email) {
    if (!email) {
        return {
            validateStatus: 'error',
            errorMsg:  'This fields is required'
        }
    }

    const EMAIL_REGEX = RegExp('[^@ ]+@[^@ ]+\\.[^@ ]+');
    if (!EMAIL_REGEX.test(email)) {
        return {
            validateStatus: 'error',
            errorMsg: 'Email not valid'
        }
    }

    return {
        validateStatus: "success",
        errorMsg: null
    }
}

export function validatePass(pass) {
    if (pass.length < PASSWORD_MIN_LENGTH) {
        return {
            validateStatus: 'error',
            errorMsg: "expressions.password_short",
            errorMsgArgs:  {"PASSWORD_MIN_LENGTH": PASSWORD_MIN_LENGTH}
        }
    } else if (pass.length > PASSWORD_MAX_LENGTH) {
        return {
            validationStatus: 'error',
            errorMsg: "expressions.password_long",
            errorMsgArgs:  {"PASSWORD_MAX_LENGTH": PASSWORD_MAX_LENGTH}
        }
    } else {
        return {
            validateStatus: 'success',
            errorMsg: null,
        };
    }
}

export function validateConfirmPass(pass, originalPassword) {
    if (pass && originalPassword && pass !== originalPassword) {
        return {
            validateStatus: 'error',
            errorMsg: "Password don't match"
        }
    } else {
        return {
            validateStatus: 'success',
            errorMsg: null,
        };
    }
}

export function validateName(name) {
    if (name.length < NAME_MIN_LENGTH) {
        return {
            validateStatus: 'error',
            errorMsg: "expressions.name_short",
            errorMsgArgs:  {"NAME_MIN_LENGTH": NAME_MIN_LENGTH}
        }
    } else if (name.length > NAME_MAX_LENGTH) {
        return {
            validationStatus: 'error',
            errorMsg: "expressions.name_long",
            errorMsgArgs:  {"NAME_MAX_LENGTH": NAME_MAX_LENGTH}
        }
    } else {
        return {
            validateStatus: 'success',
            errorMsg: null,
        };
    }
}