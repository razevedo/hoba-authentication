hoba-authentication
===================

Overview
===================

*hoba-authentication* is a clean and simple implementation of the HTTP Origin-Bound Authentication (HOBA) [IETF draft](https://tools.ietf.org/html/draft-ietf-httpauth-hoba-05 "HTTP Origin-Bound Authentication (HOBA) IETF draft"). 

From the IETF Draft description:
>HTTP Origin-Bound Authentication (HOBA) is a design for an HTTP authentication method with credentials that are not vulnerable to phishing attacks, and that does not require any server-side password database.  The design can also be used in Javascript-based authentication embedded in HTML.HOBA is an alternative to HTTP authentication schemes that require passwords.


Motivation
===================
Every time you setup a new service that needs user’s identification and authentication you consciously, or unconsciously, have to think in two important aspects. Decide the authentication scheme and reduce to a minimum the need for a registry (not only because of this, but also because of this, services decided to use Social Login to register and authenticate their users – OAuth and, recently, OpenID Connect gain momentum!). 
Authentication schemes have always been a debatable ground, since the _beginning of times_... How to identify and authenticate the users? How secure should the authentication scheme be? How to simplify the registry process?
Everybody hate the Username and Password scheme but has been difficult to find a solution that solves it. 

>HOBA tries to solve this by using the concepts of digital signatures as an authentication mechanism.  HOBA also adds useful features such as credential management and session logout.  In HOBA, the client creates a new public-private key pair for each host ("web-origin" [RFC6454]) to which it authenticates.  These keys are used in HOBA for HTTP clients to authenticate themselves to servers in the HTTP protocol or in a Javascript authentication program. 

Are you sure it is not secure? Think twice, are you really sure you need higher authentication assurance for your service? 
We believe the problem is not with the username/password scheme itself, or the use of strong authentication mechanisms, the problem is to understand the degree of assurance the service really needs. It could happen you need strong authentication mechanisms, but in most of the cases a simple approach is enough.

How it works
===================
This project uses a client with javascript and a server with a rest api. The hoba_authentication.js methods are described int the Usage section. The rest api is as follows:

POST hoba/register
------------------
Registers an UA in the hoba database.

**Parameters**

* *pub*: UA generated Public Key;
* *kidtype*: The key type;
* *kide*: The Public key ID;
* *didtype*: Device Type;
* *did*: Device id;

**Returns**

*HTTP Header*: 

* Hobareg-val

POST hoba/getchal
------------------
Allows the UA to get a challenge to be used for authentication

**Parameters**

* *kid*: The Public Key ID

**Returns**

*HTTP Header*: 
* Authentication = HOBA
* challenge = [generated_challenge]
* max-age = [expiration_time]

POST hoba/auth
------------------
Authenticates an UA

**Parameters**

* *Authorized*: HOBA-RES

**Returns**

*HTTP Response Status*

* On success: 200
* On Failure: 400

DELETE hoba/key
------------------
Deltes an UA key ensuring UA unregistration

**Parameters**

* *kid*: Public Key ID

**Returns**

*HTTP Response Status*

* On success: 200
* On Failure: 500

DELETE hoba/user
------------------
Deltes all the user data

**Parameters**

* *kid*: Public Key ID

**Returns**

*HTTP Response Status*


* On success: 200
* On Failure: 500

GET hoba/user
------------------
Gets the user data

**Parameters**

* *kid*: Public Key ID

**Returns**

List containing all the connections: {"idUser":1,"field1":"","field2":"","field3":""}


GET hoba/token
------------------
Gets a token that can be used for binding another UA

**Parameters**

* *kid*: Public Key ID

**Returns**

*Token*

POST hoba/token
------------------
Verifies the token validity and binds the UA

**Parameters**

* *token*: The server generatated token
* *kid*: Public Key ID

**Returns**

*HTTP Response Status*

* On success: 200
* On Failure: 400

POST hoba/uas
------------------
Returns the several several UAs where the same user as been connected

**Parameters**

* *kid*: Public Key ID

**Returns**

List containing all the connections: ["deviceType": "", "ipAddress": "", "date": "", "kid": ""]



Usage
===================
Download the js libraries fingerprint.js, hoba_auth-min.js, jsrsasign-4.7.0-all-min.js, qrcode.min.js and include them in your html.
To use the hoba_auth-min.js you need to start by initializing the object with the server endpoints urls.

```html
  var serverURL = "server_url:server_port/hoba-authentication/hoba/";
    var initObj = {
    protocol: "http://",
    registerURL: serverURL + "register",
    userURL: serverURL + "user",
    uaURL: serverURL + "uas",
    tokenURL: serverURL + "token",
    keyURL: serverURL + "key",
    challengeURL: serverURL + "getchal",
    authURL: serverURL + "auth"
  }

  hoba.Init(initObj);
```

After initializing you can register the User Agent (UA) hoba.registration() and for logging in the hoba.login(). This two methods will return a 0 on success and a value less than 0 on error.

```html
  hoba.registration();
  response = hoba.login();
```

The user can also logout, unregister his UA or even delete all his data from the server.

```html
  hoba.logout();
  hoba.unregister();
  hoba.zapData();
```

We also provide methods for adding and getting user info.

```html
  var field1;
  var field2;
  var field3;
  hoba.setUserData(field1, field2, field3);
  
  hoba.getUserData();
```
As for login and registration both of these methods will return a value less than 0 on erro, but getUserData will return a object with the user info on success.

```html
Object {idUser: 1, field1: "field", field2: "field2", field3: "field3"} 
```

If you want you to use the same acount in a different UA you can generate an authentication token. The Application will generate a link with a token. You can copy this link to another UA and associate it to a previous user account.
The link can be generated with:

```html
var token = hoba.getToken(expirationTime);
```

The method hoba.getToken(expirationTime) requires a parameter, expiration time. This parameter indicates how much time will the token be valid. If the expiration time is 0 the token will be valid for olny one time.

After you pasted into another UA you can do the following proccess to get the token and bind the account.

```html
var token = hoba.getLinkToken();
if (token != -1) {
  hoba.bind(token);
}
```

Finaly we provide two methods for checking if the UA is registered with hoba and if the user has logged in

```html
hoba.isRegisted();
hoba.isLoggin();
```

These two method will return true or false.


Samples
===================

We provide a fully functional example. To test our sample you need a tomcat server (we have tested with tomcat 8.0.12) and postgresql with a database created called hoba. Then you can clone our project and simply run it.
In the following code block is a simple hoba client in html with javascript. This sample only implements some basic methods: registration, logging in, logout, unregister and zap data.

```html
<!DOCTYPE html>
<html>
    <head>
        <title>hoba-authentication sample</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <script src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
        <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.9.0/jquery-ui.min.js"></script>
        <script language="JavaScript" type="text/javascript" src="js/jsrsasign-4.7.0-all-min.js"></script>

        <script type="text/javascript" src="js/fingerprint.js"></script>


        <script type="text/javascript" src="js/qrcode.min.js"></script>
        <script type="text/javascript" src="js/hoba_auth-min.js"></script>
    </head>
    <body>
        <h1>hoba-authentication sample</h1>
        <button onclick="register()">Register</button>
        <button onclick="login()">Login</button>
        <button onclick="logout()">Logout</button>
        <button onclick="unregister()">Unregister</button>
        <button onclick="zapData()">Zap Data</button>
        <div id="response"></div>

        <script type="text/javascript">
            var serverURL = "localhost:8080/hoba-authentication/hoba/";

            var initObj = {
                protocol: "http://",
                registerURL: serverURL + "register",
                userURL: serverURL + "user",
                uaURL: serverURL + "uas",
                tokenURL: serverURL + "token",
                keyURL: serverURL + "key",
                challengeURL: serverURL + "getchal",
                authURL: serverURL + "auth"
            }

            $(document).ready(function () {
                hoba.Init(initObj);
            });
            function register() {
                if (hoba.registration() == 0) {
                    $('#response').text("Registration successfull");
                } else {
                    $('#response').text("Error in registration");
                }
            }
            function login() {
                if (hoba.login() == 0) {
                    $('#response').text("Login Successfull");
                } else {
                    $('#response').text("Error logging in");
                }
            }
            function logout() {
                $('#response').text("Logout");
                hoba.logout();

            }
            function unregister() {
                if (hoba.unregister() == 0) {
                    $('#response').text("Unregister successfull");
                } else {
                    $('#response').text("Error unregistering");
                }
            }
            function  zapData() {
                if (hoba.zapData() == 0) {
                    $('#response').text("Zaping data successfull");
                } else {
                    $('#response').text("Error zapping data");
                }
            }
        </script>
    </body>
</html>

```

FAQ
===================
###Is this secure?
Yes it is, but keep in mind you must use HTTPS.  

###How it works with multi-devices and different browsers?
Good point. Your browser generates an ID and Public/private key. A time based token, encoded in a URL is provided by the server, so the user can use it when accessing the service form other browser or device (a QR code with the URL is also available). As all the enrollments you only have to do this once (per browser/device).

###But there is another opensource implementation and it comes from more trustful source...
yes, there is another implementation [www.hoba.ie](https://hoba.ie/faq.html). We decided to build our own implementation because we think that the actual implementation is not so modular as it could be, so we decided to create something others may use more simply.... not sure if we did, but we tried!

_for more questions and not to repeat what is already well described go here [www.hoba.ie/faq.html](https://hoba.ie/faq.html)_

######This code has been done within a [PT Inovação e Sistemas](http://www.ptinovacao.pt) project. 

Licence (MIT)
===================
Copyright (c) 2014 Fábio Gonçalves & [Ricardo Azevedo](http://pt.linkedin.com/in/razevedoper/)

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
