/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

window.log = function () {
    log.history = log.history || [];   // store logs to an array for reference
    log.history.push(arguments);
    if (this.console) {
        console.log(Array.prototype.slice.call(arguments));
    }
};

var hoba = {};
hoba.hoba_pubKey;
hoba.hoba_privKey;
hoba.hoba_kidtype;
hoba.hoba_kid;
hoba.hoba_didtype;
hoba.hoba_did;
hoba.isInit;
hoba.challenge;
hoba.maxAge;

hoba.settings = {
    protocol: "http://",
    registerURL: "",
    userURL: "",
    uaURL: "",
    tokenURL: "",
    keyURL: "",
    challengeURL: "",
    authURL: "",
    logoutURL: ""
}


hoba.Init = function (initObj) {

    hoba.isInit = true;
    if (hoba.isRegisted()) {
        hoba.getLocalStorageData();
    }

    hoba.checkSettings(initObj)
    hoba.settings = initObj;
    hoba.checkURLs();
}

hoba.checkURLs = function () {
    for (var keys in hoba.settings) {
        if (keys != "protocolo") {
            if (hoba.settings[keys].indexOf("http://") < 0 || hoba.settings[keys].toString().indexOf("http://") < 0) {
                hoba.settings[keys] = hoba.settings.protocol + hoba.settings[keys];
            }
        }
    }
}

hoba.checkSettings = function (initObj) {
    var i = 0;
    for (var keys in initObj) {
        if (!hoba.settings.hasOwnProperty(keys)) {
            window.log("hoba.settings does not contain the key " + keys);

        } else
            i++;
    }

    if (i != 9) {
        window.log("hoba.settings has 9 fields, initObj has " + i);
    }
}

hoba.getLocalStorageData = function () {
    hoba.hoba_privKey = KEYUTIL.getKey(localStorage.getItem("hoba_privKey"));
    hoba.hoba_pubKey = KEYUTIL.getKey(localStorage.getItem("hoba_pubKey"));
    hoba.hoba_kid = localStorage.getItem("hoba_kid");
}

hoba.registration = function () {
    var response;
    if (!hoba.isInit) {
        return -3;
    }
    hoba.createRSAKeys();
    hoba.getDeviceFields();
    var pub = KEYUTIL.getPEM(hoba.hoba_pubKey);
    var register_kid = stob64(hoba.hoba_kid);
    $.ajax({
        type: "POST",
        url: hoba.settings.registerURL,
        data: {
            pub: pub,
            kidtype: hoba.hoba_kidtype,
            kid: register_kid,
            didtype: hoba.hoba_didtype,
            did: hoba.did
        },
        async: false
    }).always(function (data, status, xhr) {
        response = hoba.handleRegisterReponse(status, xhr);
    });
    return response;

}

hoba.login = function () {
    var challResponse = hoba.getChallenge(hoba.settings.challengeURL);

    if (challResponse != 0) {
        return challResponse;
    }
    var signedClientResult = hoba.getClientResult(hoba.challenge);
    return hoba.auth(signedClientResult);
}



hoba.getUserData = function () {
    var response;
    $.ajax({
        type: "GET",
        url: hoba.settings.userURL + "?kid=" + hoba.hoba_kid,
        async: false
    }).always(function (data, status, xhr) {

        if (status == "success") {
            response = data;
        } else if (data.getResponseHeader("Authenticate") == "HOBA") {

            window.log("Session Expired");
            response = -1;
        } else {
            window.log("Unknown Error");
            response = -2;
        }
    });

    return response;
}

hoba.setUserData = function (field1, field2, field3) {
    var response;

    $.ajax({
        type: "POST",
        url: hoba.settings.userURL,
        data: {
            kid: hoba.hoba_kid,
            field1: field1,
            field2: field2,
            field3: field3
        },
        async: false
    }).always(function (data, status, xhr) {
        if (status == "success") {
            response = 0;
        } else if (xhr.getResponseHeader("Authenticate") == "HOBA") {
            window.log("Session Expired");
            response = -1;
        } else {
            window.log("Unkown Error");
            response = -2;
        }
    });
    return response;
}

hoba.getConnections = function () {
    var response;
    $.ajax({
        type: "GET",
        url: hoba.settings.uaURL,
        data: {
            kid: hoba.hoba_kid
        },
        async: false
    }).always(
            function (data, status, xhr) {

                if (status == "success") {
                    response = data;
                } else if (data.getResponseHeader("Authenticate") == "HOBA") {
                    window.log("Session Expired");
                    response = -1;
                } else {
                    window.log("Unkown Error");
                    response = -2;
                }
            }
    );

    return response;
}

hoba.getToken = function (expirationTime) {
    var response;
    var origin = window.location.href;
    origin = origin.substring(0, origin.length - 1);

    $.ajax({
        type: "GET",
        url: hoba.settings.tokenURL + "?kid=" + hoba.hoba_kid + "&expiration_time=" + expirationTime,
        async: false
    }).always(
            function (data, status, xhr) {
                if (status == "success") {
                    response = origin + "?token=" + data;
                } else if (data.getResponseHeader("Authenticate") == "HOBA") {
                    window.log("Session Expired");
                    response = -1;
                } else {
                    window.log("Unkown Error");
                    response = -2;
                }
            }
    );

    return response;
}

hoba.bind = function (token) {
    var response;
    var continueBind = hoba.bindOperations();
    if (continueBind) {
        $.ajax({
            type: "POST",
            url: hoba.settings.tokenURL,
            data: {
                kid: hoba.hoba_kid,
                token: token
            },
            async: false
        }).always(
                function (data, status, xhr) {
                    if (status == "success") {
                        response = 0;
                    } else if (data.getResponseHeader("Authenticate") == "HOBA") {
                        window.log("Session Expired");
                        response = -1;
                    } else {
                        window.log("Unkown Error");
                        response = -2;
                    }
                }
        );
    } else {
        window.log("Operation canceled by user");
        response = -1;
    }

    return response;

}

hoba.bindOperations = function () {

    var resp = false;
    if (hoba.isRegisted()) {
        resp = confirm("You already have another account registed in in this browser. Do you want to associate it with another user?");
    } else {
        hoba.registration();
        resp = true;
    }
    return resp;


}

hoba.removeKid = function (kid) {

    var response;
    $.ajax({
        type: "DELETE",
        url: hoba.settings.keyURL,
        data: {
            kid: kid
        },
        async: false
    }).always(
            function (data, status, xhr) {
                if (status == "success") {
                    response = 0;
                } else if (data.getResponseHeader("Authenticate") == "HOBA") {
                    window.log("Session Expired");
                    response = -1;
                } else {
                    window.log("Unkown Error");
                    response = -2;
                }
            }
    );
    return response;
}

hoba.logout = function () {

    var response;
    $.ajax({
        type: "POST",
        url: hoba.settings.logoutURL,
        data: {
            kid: hoba.hoba_kid
        },
        async: false
    }).always(
            function (data, status, xhr) {
                if (status == "success") {
                    response = 0;
                } else if (data.getResponseHeader("Authenticate") == "HOBA") {
                    window.log("Session Expired");
                    response = -1;
                } else {
                    window.log("Unkown Error");
                    response = -2;
                }
            }
    );
    sessionStorage.clear();
    return response;
}

hoba.zapData = function () {
    var response;
    $.ajax({
        type: "DELETE",
        url: hoba.settings.userURL,
        data: {
            kid: hoba.hoba_kid
        },
        async: false
    }).always(
            function (data, status, xhr) {
                if (status == "success") {
                    response = 0;
                    localStorage.clear();
                    hoba.logout();
                } else if (data.getResponseHeader("Authenticate") == "HOBA") {
                    window.log("Session Expired");
                    response = -1;
                } else {
                    window.log("Unkown Error");
                    response = -2;
                }
            }
    );

    return response;
}



hoba.getChallenge = function () {
    var response;
    $.ajax({
        type: "POST",
        url: hoba.settings.challengeURL,
        data: {kid: hoba.hoba_kid},
        async: false
    }).always(function (data, status, xhr) {
        if (status == "success") {
            var authType = xhr.getResponseHeader("Authentication");
            if (authType == "HOBA") {
                hoba.challenge = xhr.getResponseHeader("challenge");
                hoba.maxAge = xhr.getResponseHeader("max-age");
                response = 0;

            } else {
                window.log("Wrong auth type in http header");
                response - 2;
            }
        } else {
            window.log("Error getting challenge");
            response - 3;
        }
    });
    return response;
}

hoba.getClientResult = function (challenge) {
    var nonce = hoba.makeRandString();
    var tbs = hoba.getHOBATBS(challenge, nonce);
    var sig = new KJUR.crypto.Signature({"alg": "SHA1withRSA", "prov": "cryptojs/jsrsa"});
    sig.initSign(hoba.hoba_privKey);
    sig.updateString(tbs);
    var signature = sig.sign();

    var base64Sig = stob64(signature);
    var client_result = hoba.hoba_kid + "." + challenge + "." + nonce + "." + base64Sig;
    return client_result;
}

hoba.auth = function (signedClientResult) {
    var response;
    $.ajax({
        url: hoba.settings.authURL,
        type: "POST",
        headers: {
            "Authorized": signedClientResult
        },
        async: false
    }).always(
            function (data, status, xhr) {
                if (status == "success") {
                    sessionStorage.setItem("loggedin", true);
                    response = 0;
                } else {
                    sessionStorage.removeItem("loggedin");
                    window.log("Error in the authentication");
                    response = -1;
                }
            });
    return response;
}


hoba.handleRegisterReponse = function (status, xhr) {
    if (status === "success") {
        var hobareg = xhr.getResponseHeader("Hobareg-val");

        if (hobareg === "regok") {
            localStorage.setItem("hoba_privKey", KEYUTIL.getPEM(hoba.hoba_privKey, "PKCS8PRV"));
            localStorage.setItem("hoba_pubKey", KEYUTIL.getPEM(hoba.hoba_pubKey));
            localStorage.setItem("hoba_kid", hoba.hoba_kid);
            return 0;
        } else {
            window.log("Register in process");
            return -1;
        }
    } else {
        window.log("Error in registration");
        return -2;
    }
}

hoba.isRegisted = function () {
    var priv = localStorage.getItem("hoba_privKey");
    var pub = localStorage.getItem("hoba_pubKey");
    var kid = localStorage.getItem("hoba_kid");

    if (priv == null || pub == null || kid == null) {
        return false;
    }
    else {
        return true;
    }
}



hoba.isLoggin = function () {
    var loggedin = sessionStorage.getItem("loggedin");
    if (loggedin == null) {
        return false;
    }
    return Boolean(loggedin);
}



hoba.unregister = function () {
    var response = hoba.removeKid(hoba.hoba_kid)
    if (response == 0) {
        hoba.logout();
        localStorage.clear();
    }
    return response;
}

hoba.createRSAKeys = function () {
    hoba.hoba_kidtype = 2;
    var keys = KEYUTIL.generateKeypair("RSA", 1024);
    hoba.hoba_pubKey = keys.pubKeyObj;
    hoba.hoba_privKey = keys.prvKeyObj;
    var pub = KEYUTIL.getPEM(hoba.hoba_pubKey);
    hoba.hoba_kid = getKeyID(pub);
}

hoba.getDeviceFields = function () {
    hoba.hoba_didtype = navigator.userAgent;
    hoba.hoba_did = new Fingerprint().get();
}

hoba.makeRandString = function ()
{
    var text = "";
    var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    for (var i = 0; i < 20; i++)
        text += possible.charAt(Math.floor(Math.random() * possible.length));
    return stob64(text);
}

hoba.getHOBATBS = function (challenge, nonce) {
    var origin = window.location.href;
    origin = origin.substring(0, origin.length - 1);

    origin = origin.substring(0, origin.lastIndexOf("/") + 1);

    var alg = "1";

    var kid_tbs = stob64(hoba.hoba_kid);

    var challenge_tbs = challenge;
    var tbs = nonce + " " + alg + " " + origin + " " + kid_tbs + " " + challenge;
    return tbs;
}

hoba.getLinkToken = function () {
    var r = true;

    var token = window.location.search.substring(1);
    token = token.replace("token=", "");

    if (token != null && token != "") {
        return token;
    }
    return -1;
}

function getKeyID(string) {

    function RotateLeft(lValue, iShiftBits) {
        return (lValue << iShiftBits) | (lValue >>> (32 - iShiftBits));
    }

    function AddUnsigned(lX, lY) {
        var lX4, lY4, lX8, lY8, lResult;
        lX8 = (lX & 0x80000000);
        lY8 = (lY & 0x80000000);
        lX4 = (lX & 0x40000000);
        lY4 = (lY & 0x40000000);
        lResult = (lX & 0x3FFFFFFF) + (lY & 0x3FFFFFFF);
        if (lX4 & lY4) {
            return (lResult ^ 0x80000000 ^ lX8 ^ lY8);
        }
        if (lX4 | lY4) {
            if (lResult & 0x40000000) {
                return (lResult ^ 0xC0000000 ^ lX8 ^ lY8);
            } else {
                return (lResult ^ 0x40000000 ^ lX8 ^ lY8);
            }
        } else {
            return (lResult ^ lX8 ^ lY8);
        }
    }

    function F(x, y, z) {
        return (x & y) | ((~x) & z);
    }
    function G(x, y, z) {
        return (x & z) | (y & (~z));
    }
    function H(x, y, z) {
        return (x ^ y ^ z);
    }
    function I(x, y, z) {
        return (y ^ (x | (~z)));
    }

    function FF(a, b, c, d, x, s, ac) {
        a = AddUnsigned(a, AddUnsigned(AddUnsigned(F(b, c, d), x), ac));
        return AddUnsigned(RotateLeft(a, s), b);
    }
    ;
    function GG(a, b, c, d, x, s, ac) {
        a = AddUnsigned(a, AddUnsigned(AddUnsigned(G(b, c, d), x), ac));
        return AddUnsigned(RotateLeft(a, s), b);
    }
    ;
    function HH(a, b, c, d, x, s, ac) {
        a = AddUnsigned(a, AddUnsigned(AddUnsigned(H(b, c, d), x), ac));
        return AddUnsigned(RotateLeft(a, s), b);
    }
    ;
    function II(a, b, c, d, x, s, ac) {
        a = AddUnsigned(a, AddUnsigned(AddUnsigned(I(b, c, d), x), ac));
        return AddUnsigned(RotateLeft(a, s), b);
    }
    ;
    function ConvertToWordArray(string) {
        var lWordCount;
        var lMessageLength = string.length;
        var lNumberOfWords_temp1 = lMessageLength + 8;
        var lNumberOfWords_temp2 = (lNumberOfWords_temp1 - (lNumberOfWords_temp1 % 64)) / 64;
        var lNumberOfWords = (lNumberOfWords_temp2 + 1) * 16;
        var lWordArray = Array(lNumberOfWords - 1);
        var lBytePosition = 0;
        var lByteCount = 0;
        while (lByteCount < lMessageLength) {
            lWordCount = (lByteCount - (lByteCount % 4)) / 4;
            lBytePosition = (lByteCount % 4) * 8;
            lWordArray[lWordCount] = (lWordArray[lWordCount] | (string.charCodeAt(lByteCount) << lBytePosition));
            lByteCount++;
        }
        lWordCount = (lByteCount - (lByteCount % 4)) / 4;
        lBytePosition = (lByteCount % 4) * 8;
        lWordArray[lWordCount] = lWordArray[lWordCount] | (0x80 << lBytePosition);
        lWordArray[lNumberOfWords - 2] = lMessageLength << 3;
        lWordArray[lNumberOfWords - 1] = lMessageLength >>> 29;
        return lWordArray;
    }
    ;
    function WordToHex(lValue) {
        var WordToHexValue = "", WordToHexValue_temp = "", lByte, lCount;
        for (lCount = 0; lCount <= 3; lCount++) {
            lByte = (lValue >>> (lCount * 8)) & 255;
            WordToHexValue_temp = "0" + lByte.toString(16);
            WordToHexValue = WordToHexValue + WordToHexValue_temp.substr(WordToHexValue_temp.length - 2, 2);
        }
        return WordToHexValue;
    }
    ;
    function Utf8Encode(string) {
        string = string.replace(/\r\n/g, "\n");
        var utftext = "";
        for (var n = 0; n < string.length; n++) {

            var c = string.charCodeAt(n);
            if (c < 128) {
                utftext += String.fromCharCode(c);
            }
            else if ((c > 127) && (c < 2048)) {
                utftext += String.fromCharCode((c >> 6) | 192);
                utftext += String.fromCharCode((c & 63) | 128);
            }
            else {
                utftext += String.fromCharCode((c >> 12) | 224);
                utftext += String.fromCharCode(((c >> 6) & 63) | 128);
                utftext += String.fromCharCode((c & 63) | 128);
            }

        }

        return utftext;
    }
    ;
    var x = Array();
    var k, AA, BB, CC, DD, a, b, c, d;
    var S11 = 7, S12 = 12, S13 = 17, S14 = 22;
    var S21 = 5, S22 = 9, S23 = 14, S24 = 20;
    var S31 = 4, S32 = 11, S33 = 16, S34 = 23;
    var S41 = 6, S42 = 10, S43 = 15, S44 = 21;
    string = Utf8Encode(string);
    x = ConvertToWordArray(string);
    a = 0x67452301;
    b = 0xEFCDAB89;
    c = 0x98BADCFE;
    d = 0x10325476;
    for (k = 0; k < x.length; k += 16) {
        AA = a;
        BB = b;
        CC = c;
        DD = d;
        a = FF(a, b, c, d, x[k + 0], S11, 0xD76AA478);
        d = FF(d, a, b, c, x[k + 1], S12, 0xE8C7B756);
        c = FF(c, d, a, b, x[k + 2], S13, 0x242070DB);
        b = FF(b, c, d, a, x[k + 3], S14, 0xC1BDCEEE);
        a = FF(a, b, c, d, x[k + 4], S11, 0xF57C0FAF);
        d = FF(d, a, b, c, x[k + 5], S12, 0x4787C62A);
        c = FF(c, d, a, b, x[k + 6], S13, 0xA8304613);
        b = FF(b, c, d, a, x[k + 7], S14, 0xFD469501);
        a = FF(a, b, c, d, x[k + 8], S11, 0x698098D8);
        d = FF(d, a, b, c, x[k + 9], S12, 0x8B44F7AF);
        c = FF(c, d, a, b, x[k + 10], S13, 0xFFFF5BB1);
        b = FF(b, c, d, a, x[k + 11], S14, 0x895CD7BE);
        a = FF(a, b, c, d, x[k + 12], S11, 0x6B901122);
        d = FF(d, a, b, c, x[k + 13], S12, 0xFD987193);
        c = FF(c, d, a, b, x[k + 14], S13, 0xA679438E);
        b = FF(b, c, d, a, x[k + 15], S14, 0x49B40821);
        a = GG(a, b, c, d, x[k + 1], S21, 0xF61E2562);
        d = GG(d, a, b, c, x[k + 6], S22, 0xC040B340);
        c = GG(c, d, a, b, x[k + 11], S23, 0x265E5A51);
        b = GG(b, c, d, a, x[k + 0], S24, 0xE9B6C7AA);
        a = GG(a, b, c, d, x[k + 5], S21, 0xD62F105D);
        d = GG(d, a, b, c, x[k + 10], S22, 0x2441453);
        c = GG(c, d, a, b, x[k + 15], S23, 0xD8A1E681);
        b = GG(b, c, d, a, x[k + 4], S24, 0xE7D3FBC8);
        a = GG(a, b, c, d, x[k + 9], S21, 0x21E1CDE6);
        d = GG(d, a, b, c, x[k + 14], S22, 0xC33707D6);
        c = GG(c, d, a, b, x[k + 3], S23, 0xF4D50D87);
        b = GG(b, c, d, a, x[k + 8], S24, 0x455A14ED);
        a = GG(a, b, c, d, x[k + 13], S21, 0xA9E3E905);
        d = GG(d, a, b, c, x[k + 2], S22, 0xFCEFA3F8);
        c = GG(c, d, a, b, x[k + 7], S23, 0x676F02D9);
        b = GG(b, c, d, a, x[k + 12], S24, 0x8D2A4C8A);
        a = HH(a, b, c, d, x[k + 5], S31, 0xFFFA3942);
        d = HH(d, a, b, c, x[k + 8], S32, 0x8771F681);
        c = HH(c, d, a, b, x[k + 11], S33, 0x6D9D6122);
        b = HH(b, c, d, a, x[k + 14], S34, 0xFDE5380C);
        a = HH(a, b, c, d, x[k + 1], S31, 0xA4BEEA44);
        d = HH(d, a, b, c, x[k + 4], S32, 0x4BDECFA9);
        c = HH(c, d, a, b, x[k + 7], S33, 0xF6BB4B60);
        b = HH(b, c, d, a, x[k + 10], S34, 0xBEBFBC70);
        a = HH(a, b, c, d, x[k + 13], S31, 0x289B7EC6);
        d = HH(d, a, b, c, x[k + 0], S32, 0xEAA127FA);
        c = HH(c, d, a, b, x[k + 3], S33, 0xD4EF3085);
        b = HH(b, c, d, a, x[k + 6], S34, 0x4881D05);
        a = HH(a, b, c, d, x[k + 9], S31, 0xD9D4D039);
        d = HH(d, a, b, c, x[k + 12], S32, 0xE6DB99E5);
        c = HH(c, d, a, b, x[k + 15], S33, 0x1FA27CF8);
        b = HH(b, c, d, a, x[k + 2], S34, 0xC4AC5665);
        a = II(a, b, c, d, x[k + 0], S41, 0xF4292244);
        d = II(d, a, b, c, x[k + 7], S42, 0x432AFF97);
        c = II(c, d, a, b, x[k + 14], S43, 0xAB9423A7);
        b = II(b, c, d, a, x[k + 5], S44, 0xFC93A039);
        a = II(a, b, c, d, x[k + 12], S41, 0x655B59C3);
        d = II(d, a, b, c, x[k + 3], S42, 0x8F0CCC92);
        c = II(c, d, a, b, x[k + 10], S43, 0xFFEFF47D);
        b = II(b, c, d, a, x[k + 1], S44, 0x85845DD1);
        a = II(a, b, c, d, x[k + 8], S41, 0x6FA87E4F);
        d = II(d, a, b, c, x[k + 15], S42, 0xFE2CE6E0);
        c = II(c, d, a, b, x[k + 6], S43, 0xA3014314);
        b = II(b, c, d, a, x[k + 13], S44, 0x4E0811A1);
        a = II(a, b, c, d, x[k + 4], S41, 0xF7537E82);
        d = II(d, a, b, c, x[k + 11], S42, 0xBD3AF235);
        c = II(c, d, a, b, x[k + 2], S43, 0x2AD7D2BB);
        b = II(b, c, d, a, x[k + 9], S44, 0xEB86D391);
        a = AddUnsigned(a, AA);
        b = AddUnsigned(b, BB);
        c = AddUnsigned(c, CC);
        d = AddUnsigned(d, DD);
    }

    var temp = WordToHex(a) + WordToHex(b) + WordToHex(c) + WordToHex(d);
    return temp.toLowerCase();
}