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
TBD

Usage
===================
TBD

Samples
===================
TBD

FAQ
===================
###Is this secure?
Yes it is, but keep in mind you must use HTTPS.  

###How it works with multi-devices and different browsers?
Good point. Your browser generates an ID and Public/private key. If you want to use in another browser (or device) you have to register again... In order to guarantee that the user remains the same, you (as a user) have to "enroll". We provide a time based token, encoded in a URL you can use in the other browser or device. As all the enrollments you only have to do this once (per browser/device)

Licence (MIT)
===================
This code has been done within [PT Inovação e Sistemas](http://www.ptinovacao.pt) project. 

Copyright (c) 2014 Fábio Gonçalves & [Ricardo Azevedo](http://pt.linkedin.com/in/razevedoper/)

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
