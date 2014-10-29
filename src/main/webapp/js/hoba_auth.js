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
    authURL: ""
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

    if (i != 8) {
        window.log("hoba.settings has 8 fields, initObj has " + i);
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
        url:  hoba.settings.registerURL,
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
        url:  hoba.settings.userURL + "?kid=" + hoba.hoba_kid,
        async: false
    }).always(function (data, status, xhr) {

        if (status == "success") {
            response = data;
        } else {
            window.log("Error getting user data");
            response = -1;
        }
    });

    return response;
}

hoba.setUserData = function (field1, field2, field3) {
    var response;

    $.ajax({
        type: "POST",
        url:  hoba.settings.userURL,
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
        } else {
            window.log("Error seting userData");
            response = -1;
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
                } else {
                    window.log("Error geting connections");
                    response = -1;
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
        url:  hoba.settings.tokenURL + "?kid=" + hoba.hoba_kid + "&expiration_time=" + expirationTime,
        async: false
    }).always(
            function (data, status, xhr) {
                if (status == "success") {
                    response = origin + "?token=" + data;
                } else {
                    window.log("Erro getting token");
                    response = -1;
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
            url:  hoba.settings.tokenURL,
            data: {
                kid: hoba.hoba_kid,
                token: token
            },
            async: false
        }).always(
                function (data, status, xhr) {
                    if (status == "success") {
                        response = 0;
                    } else {
                        window.log("Erro binding")
                        response = -1;
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
        url:  hoba.settings.keyURL,
        data: {
            kid: kid
        },
        async: false
    }).always(
            function (data, status, xhr) {
                if (status == "success") {
                    response = 0;
                } else {

                    response = -1;
                }
            }
    );
    return response;
}

hoba.zapData = function () {
    var response;
    $.ajax({
        type: "DELETE",
        url:  hoba.settings.userURL,
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
                } else {
                    window.log("Error zapping user data");
                    response = -1;
                }
            }
    );

    return response;
}



hoba.getChallenge = function () {
    var response;
    $.ajax({
        type: "POST",
        url:  hoba.settings.challengeURL,
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
            window.log("Erro getting challenge");
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

hoba.logout = function () {
    sessionStorage.clear();
}

hoba.unregister = function (keyURL) {
    var response = hoba.removeKid(hoba.hoba_kid)
    hoba.logout();
    localStorage.clear();
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