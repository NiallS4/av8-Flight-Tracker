const functions = require('firebase-functions');
const request = require('request');

// ADS-B Exchange API Call
exports.apiCall = functions.https.onCall((data) => {

  const apiKey = data.apiKey
  const lat = data.latitude;
  const lon = data.longitude;
  const dist = data.dist;

  const URL = 'https://adsbexchange-com1.p.rapidapi.com/json/lat/' + lat + '/lon/' + lon + '/dist/' + dist + '/'

  var getJson = function(URL){
    var options = { 
      method: 'GET',
      url: URL,
      headers: {
      'x-rapidapi-host': 'adsbexchange-com1.p.rapidapi.com',
      'x-rapidapi-key': apiKey
      }
    };
    return new Promise(function(resolve, reject) {

      request(options, function (error, response, body) {
        if(error) reject(error);
        else {
          resolve(JSON.parse(body));
        }
      });
    });
  };

  return getJson(URL).then(function(result) {
    console.log(result);
    return result;

  });

});

// Get single aircraft position by registration
exports.getAircraftByReg = functions.https.onCall((data) => {

  const apiKey = data.apiKey
  const registration = data.registration;

  const URL = 'https://adsbexchange-com1.p.rapidapi.com/registration/' + registration + '/'

  var getJson = function(URL){
    var options = { 
      method: 'GET',
      url: URL,
      headers: {
      'x-rapidapi-host': 'adsbexchange-com1.p.rapidapi.com',
      'x-rapidapi-key': apiKey
      }
    };
    return new Promise(function(resolve, reject) {

      request(options, function (error, response, body) {
        if(error) reject(error);
        else {
          resolve(JSON.parse(body));
        }
      });
    });
  };

  return getJson(URL).then(function(result) {
    console.log(result);
    return result;

  });

});

// Returns aircraft broadcasting specified squawk code
exports.getAircraftBySquawk = functions.https.onCall((data) => {

  const apiKey = data.apiKey
  const squawk = data.squawk;

  const URL = 'https://adsbexchange-com1.p.rapidapi.com/sqk/' + squawk + '/'

  var getJson = function(URL){
    var options = { 
      method: 'GET',
      url: URL,
      headers: {
      'x-rapidapi-host': 'adsbexchange-com1.p.rapidapi.com',
      'x-rapidapi-key': apiKey
      }
    };
    return new Promise(function(resolve, reject) {

      request(options, function (error, response, body) {
        if(error) reject(error);
        else {
          resolve(JSON.parse(body));
        }
      });
    });
  };

  return getJson(URL).then(function(result) {
    console.log(result);
    return result;

  });

});

// Returns all military aircraft
exports.getMilitaryAircraft = functions.https.onCall((data) => {

  const apiKey = data.apiKey

  const URL = 'https://adsbexchange-com1.p.rapidapi.com/mil/'

  var getJson = function(URL){
    var options = { 
      method: 'GET',
      url: URL,
      headers: {
      'x-rapidapi-host': 'adsbexchange-com1.p.rapidapi.com',
      'x-rapidapi-key': apiKey
      }
    };
    return new Promise(function(resolve, reject) {

      request(options, function (error, response, body) {
        if(error) reject(error);
        else {
          resolve(JSON.parse(body));
        }
      });
    });
  };

  return getJson(URL).then(function(result) {
    console.log(result);
    return result;

  });

});