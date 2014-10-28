/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var hoba_authentication = {};
hoba_authentication.hoba_pubKey;
hoba_authentication.hoba_privKey;
hoba_authentication.hoba_kidtype;
hoba_authentication.hoba_kid;
hoba_authentication.hoba_didtype;
hoba_authentication.hoba_did;
hoba_authentication.isInit;
hoba_authentication.challenge;
hoba_authentication.maxAge;



function hobaInit(serverurl) {

    hoba_authentication.hoba_server = serverurl;
    hoba_authentication.isInit = true;
    if (hobaIsRegisted()) {
        getLocalStorageData();
    }
}

function getLocalStorageData() {
    hoba_authentication.hoba_privKey = KEYUTIL.getKey(localStorage.getItem("hoba_privKey"));
    hoba_authentication.hoba_pubKey = KEYUTIL.getKey(localStorage.getItem("hoba_pubKey"));
    hoba_authentication.hoba_kid = localStorage.getItem("hoba_kid");
}
/*
 * 
 * return -3 dados não inicializados;
 * return -2 servidor não encontrado
 * return -1 probemas durante o registo
 * return  0 registo com sucesso 
 */
function hobaRegistration(registerURL) {
    var response;
    if (!hoba_authentication.isInit) {
        return -3;
    }
    createRSAKeys();
    getDeviceFields();
    var pub = KEYUTIL.getPEM(hoba_authentication.hoba_pubKey);
    var register_kid = stob64(hoba_authentication.hoba_kid);
    $.ajax({
        type: "POST",
        url: registerURL,
        data: {
            pub: pub,
            kidtype: hoba_authentication.hoba_kidtype,
            kid: register_kid,
            didtype: hoba_authentication.hoba_didtype,
            did: hoba_authentication.did
        },
        async: false
    }).always(function (data, status, xhr) {
        response = handleRegisterReponse(status, xhr);
    });
    return response;

}

function hobaLogin(challengeURL, authURL) {
    var challResponse = hobaGetChallenge(challengeURL);

    if (challResponse != 0) {
        return challResponse;
    }
    var signedClientResult = getClientResult(hoba_authentication.challenge);
    return hobaAuth(signedClientResult,authURL);
}

function hobaGetUserData(userURL) {
    var response;
    $.ajax({
        type: "GET",
        url: userURL + "?kid=" + hoba_authentication.hoba_kid,
        async: false
    }).always(function (data, status, xhr) {

        if (status == "success") {
            response = data;
        } else {
            response = -1;
        }
    });

    return response;
}

function hobaSetUserData(field1, field2, field3, userURL) {
    var response;

    $.ajax({
        type: "POST",
        url: userURL,
        data: {
            kid: hoba_authentication.hoba_kid,
            field1: field1,
            field2: field2,
            field3: field3
        },
        async: false
    }).always(function (data, status, xhr) {
        if (status == "success") {
            response = 0;
        } else {
            response = -1;
        }
    });
    return response;
}

function hobaGetConnections(uaURL) {
    var response;
    $.ajax({
        type: "GET",
        url: uaURL ,
        data: {
            kid: hoba_authentication.hoba_kid
        },
        async: false
    }).always(
            function (data, status, xhr) {

                if (status == "success") {
                    response = data;
                } else {
                    response = -1;
                }
            }
    );

    return response;
}

function hobaGetToken(expirationTime, tokenURL) {
    var response;
    var origin = window.location.href;
    origin = origin.substring(0, origin.length - 1);

    $.ajax({
        type: "GET",
        url: tokenURL + "?kid=" + hoba_authentication.hoba_kid + "&expiration_time=" + expirationTime,
        async: false
    }).always(
            function (data, status, xhr) {
                if (status == "success") {
                    response = origin + "?token=" + data;
                } else {
                    response = -1;
                }
            }
    );

    return response;
}

function hobaBind(token, tokenURL) {
    var response;
    $.ajax({
        type: "POST",
        url: tokenURL,
        data: {
            kid: hoba_authentication.hoba_kid,
            token: token
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

function hobaRemoveKid(kid, keyURL) {

    var response;
    $.ajax({
        type: "DELETE",
        url: keyURL,
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

function hobaZapData(kid, userURL) {

    var response;
    $.ajax({
        type: "DELETE",
        url: userURL,
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



function hobaGetChallenge(challengeURL) {
    var response;
    $.ajax({
        type: "POST",
        url: challengeURL,
        data: {kid: hoba_authentication.hoba_kid},
        async: false
    }).always(function (data, status, xhr) {
        if (status == "success") {
            var authType = xhr.getResponseHeader("Authentication");
            if (authType == "HOBA") {
                hoba_authentication.challenge = xhr.getResponseHeader("challenge");
                hoba_authentication.maxAge = xhr.getResponseHeader("max-age");
                response = 0;

            } else {
                response - 2;
            }
        } else {
            response - 3;
        }
    });
    return response;
}

function getClientResult(challenge) {
    var nonce = makeRandString();
    var tbs = getHOBATBS(challenge, nonce);
    var sig = new KJUR.crypto.Signature({"alg": "SHA1withRSA", "prov": "cryptojs/jsrsa"});
    sig.initSign(hoba_authentication.hoba_privKey);
    sig.updateString(tbs);
    var signature = sig.sign();

    var base64Sig = stob64(signature);
    var client_result = hoba_authentication.hoba_kid + "." + challenge + "." + nonce + "." + base64Sig;
    return client_result;
}

function hobaAuth(signedClientResult, authURL) {
    var response;
    $.ajax({
        url: authURL,
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
                    response = -1;
                }
            });
    return response;
}


function handleRegisterReponse(status, xhr) {
    if (status === "success") {
        var hobareg = xhr.getResponseHeader("Hobareg-val");

        if (hobareg === "regok") {
            localStorage.setItem("hoba_privKey", KEYUTIL.getPEM(hoba_authentication.hoba_privKey, "PKCS8PRV"));
            localStorage.setItem("hoba_pubKey", KEYUTIL.getPEM(hoba_authentication.hoba_pubKey));
            localStorage.setItem("hoba_kid", hoba_authentication.hoba_kid);
            return 0;
        } else {
            return -1;
        }
    } else {
        return -2;
    }
}

function hobaIsRegisted() {
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



function hobaIsLoggin() {
    var loggedin = sessionStorage.getItem("loggedin");
    if (loggedin == null) {
        return false;
    }

    return Boolean(loggedin);
}

function hobaLogout() {
    sessionStorage.clear();
}

function hobaUnregister(keyURL) {
    var response = hobaRemoveKid(hoba_authentication.hoba_kid, keyURL)
    hobaLogout();
    localStorage.clear();
    return response;
}

function createRSAKeys() {
    hoba_authentication.hoba_kidtype = 2;
    var keys = KEYUTIL.generateKeypair("RSA", 1024);
    hoba_authentication.hoba_pubKey = keys.pubKeyObj;
    hoba_authentication.hoba_privKey = keys.prvKeyObj;
    var pub = KEYUTIL.getPEM(hoba_authentication.hoba_pubKey);
    hoba_authentication.hoba_kid = getKeyID(pub);
}

function getDeviceFields() {
    hoba_authentication.hoba_didtype = navigator.userAgent;
    hoba_authentication.hoba_did = new Fingerprint().get();
}

function makeRandString()
{
    var text = "";
    var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    for (var i = 0; i < 20; i++)
        text += possible.charAt(Math.floor(Math.random() * possible.length));
    return stob64(text);
}

function getHOBATBS(challenge, nonce) {
    var origin = window.location.href;
    origin = origin.substring(0, origin.length - 1);

    origin = origin.substring(0, origin.lastIndexOf("/") + 1);

    var alg = "1";

    var kid_tbs = stob64(hoba_authentication.hoba_kid);
    
    var challenge_tbs = challenge;
    var tbs = nonce + " " + alg + " " + origin + " " + kid_tbs + " " + challenge;
    return tbs;
}

function hobaGetLinkToken() {
    var r = true;

    var token = window.location.search.substring(1);
    token = token.replace("token=", "");

    if (token != null && token != "") {
        return token;
    }
    return -1;
}