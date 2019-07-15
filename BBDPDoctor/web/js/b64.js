function utf8_to_b64(input) {
    return window.btoa(unescape(encodeURIComponent(input)));
}

function b64_to_utf8(input) {
    return decodeURIComponent(escape(window.atob(input)));
}