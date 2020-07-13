const functions = require('firebase-functions');
const unirest = require("unirest");


// Updated API Call to Unirest
exports.apiCall = functions.https.onCall((data) => {

  const lat = data.latitude;
  const lon = data.longitude;
  const dist = data.dist;


  const req = unirest('GET', 'https://adsbexchange-com1.p.rapidapi.com/json/lat/' + lat + '/lon/' + lon + '/dist/' + dist + '/')

  req.headers({
    "x-rapidapi-host": "adsbexchange-com1.p.rapidapi.com",
    "x-rapidapi-key": "a7f5ea86famsh90c76e420e25c01p1a76d4jsne9b1f2e24284",
    "useQueryString": true
  });

  return new Promise(function(resolve, reject) {

    req.end(function (res) {
    if (res.error) throw new Error(res.error);
    else {
      console.log(res.body);
      resolve(res.body);
      return res.body;
    }
    });
  });
});


// Get single aircraft position by registration
exports.getAircraftByReg = functions.https.onCall((data) => {

  const registration = data.registration;

  const req = unirest('GET', 'https://adsbexchange-com1.p.rapidapi.com/registration/' + registration + '/')

  req.headers({
    "x-rapidapi-host": "adsbexchange-com1.p.rapidapi.com",
    "x-rapidapi-key": "a7f5ea86famsh90c76e420e25c01p1a76d4jsne9b1f2e24284",
    "useQueryString": true
  });

  return new Promise(function(resolve, reject) {

    req.end(function (res) {
    if (res.error) throw new Error(res.error);
    else {
      console.log(res.body);
      resolve(res.body);
      return res.body;
    }
    });
  });
});


// Returns aircraft broadcasting specified squawk code
exports.getAircraftBySquawk = functions.https.onCall((data) => {

  const apiKey = data.apiKey
  const squawk = data.squawk;

  const req = unirest('GET', 'https://adsbexchange-com1.p.rapidapi.com/sqk/' + squawk + '/')

  req.headers({
      "x-rapidapi-host": "adsbexchange-com1.p.rapidapi.com",
      "x-rapidapi-key": "a7f5ea86famsh90c76e420e25c01p1a76d4jsne9b1f2e24284",
      "useQueryString": true
    });

  return new Promise(function(resolve, reject) {

    req.end(function (res) {
    if (res.error) throw new Error(res.error);
    else {
      console.log(res.body);
      resolve(res.body);
      return res.body;
    }
    });
  });
});


// Returns all military aircraft
exports.getMilitaryAircraft = functions.https.onCall((data) => {

  const req = unirest('GET', 'https://adsbexchange-com1.p.rapidapi.com/mil/')

    req.headers({
    "x-rapidapi-host": "adsbexchange-com1.p.rapidapi.com",
    "x-rapidapi-key": "a7f5ea86famsh90c76e420e25c01p1a76d4jsne9b1f2e24284",
    "useQueryString": true
  });

  return new Promise(function(resolve, reject) {

    req.end(function (res) {
    if (res.error) throw new Error(res.error);
    else {
      console.log(res.body);
      resolve(res.body);
      return res.body;
    }
    });
  });
});


// Old Request API Call
// const request = require('request');

// exports.oldApiCall = functions.https.onCall((data) => {

//  const apiKey = data.apiKey
//  const lat = data.latitude;
//  const lon = data.longitude;
//  const dist = data.dist;

//  const URL = 'https://adsbexchange-com1.p.rapidapi.com/json/lat/' + lat + '/lon/' + lon + '/dist/' + dist + '/'

//  var getJson = function(URL){
//  var options = { 
//    method: 'GET',
//    url: URL,
//    headers: {
//    'x-rapidapi-host': 'adsbexchange-com1.p.rapidapi.com',
//    'x-rapidapi-key': apiKey
//    }
//  };
//  return new Promise(function(resolve, reject) {

//    request(options, function (error, response, body) {
//    if(error) reject(error);
//    else {
//      resolve(JSON.parse(body));
//    }
//    });
//  });
//  };

//  return getJson(URL).then(function(result) {
//  console.log(result);
//  return result;
//  });
// });
