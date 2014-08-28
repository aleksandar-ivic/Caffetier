
function createCORSRequest(method, url) {
    var xhr = new XMLHttpRequest();
    if ("withCredentials" in xhr) {

        // Check if the XMLHttpRequest object has a "withCredentials" property.
        // "withCredentials" only exists on XMLHTTPRequest2 objects.
        xhr.open(method, url, true);

    } else if (typeof XDomainRequest != "undefined") {

        // Otherwise, check if XDomainRequest.
        // XDomainRequest only exists in IE, and is IE's way of making CORS requests.
        xhr = new XDomainRequest();
        xhr.open(method, url);

    } else {

        // Otherwise, CORS is not supported by the browser.
        xhr = null;

    }
    return xhr;
}



//Activni rad 
//slanje i JSON
function sendAjax()
{
    var jsonZaSlanje = {};
    jsonZaSlanje.title = "Bistro";
    jsonZaSlanje.json = JSON.stringify(canvas);
    console.log(jsonZaSlanje.json);


    var loginName = "branko";
    var password = "sifra";
    var token = "Basic " + btoa(loginName + ':' + password);

    var postData = "json=" + encodeURIComponent(jsonZaSlanje.json) + "&title=Bistro";
    var xhr = createCORSRequest('POST', 'https://178.33.216.114/servis/');
//var xhr = createCORSRequest('GET', 'https://178.33.216.114/kafici/');
//var xhr = createCORSRequest('POST', 'http://192.168.94.244/servis/');
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
//xhr.setRequestHeader("Access-Control-Allow-Origin", "*");
//xhr.setRequestHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
    xhr.setRequestHeader('Authorization', token);
    xhr.send(postData);
//xhr.send();


    console.log(xhr);

    xhr.onload = function() {
        var responseText = xhr.responseText;
        console.log(responseText);
        // process the response.
        alert(responseText);
    };

    xhr.onerror = function() {
        console.log('There was an error!');
    };
    if (!xhr) {
        throw new Error('CORS not supported');
        alert('CORS not supported');
    }
}

function getData(width, height) {

    var response = "pocetni";
    var loginName = "branko";
    var password = "sifra";
    var token = "Basic " + btoa(loginName + ':' + password);
    $.ajax({
        type: "GET",
        //url: "http://192.168.94.244/get_json/",
        url: "https://178.33.216.114/get_json/",
        async: false,
        contentType: "application/json",
        data: {
            "title": "Bistro"
        },
        xhrFields: {
            withCredentials: true
        },
        headers: {
            "Authorization": token,
            //Access-Control-Allow-Credentials": true
                    //"Access-Control-Allow-Origin": "*"      
        },
        /*beforeSend: function (xhr) {
         xhr.setRequestHeader ("Authorization", token);
         },*/
        traditional: true,
        success: function(data) {
            //alert(JSON.stringify(data));
            //console.log(JSON.stringify(data))    
            //document.getElementById("modalErrorEvidence").;
            response = data;

        },
        failure: function(errMsg) {
            //alert(errMsg);
            $('#modalErrorEvidence').modal('show');
            response = errMsg;

        },
        complete: function(jqXHR, textStatus) {
            responseText = textStatus;
        }
    });
    setUpCanvas(response, width, height);
//return response;

}

function getDataNew() {
    var response;
    var loginName = "branko";
    var password = "sifra";
    var token = "Basic " + btoa(loginName + ':' + password);
    var xhr = createCORSRequest('GET', 'https://178.33.216.114/get_json/?title=Bistro');
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.setRequestHeader('Authorization', token);
    xhr.withCredentials = true;
    xhr.send();
    xhr.onload = function() {
        response = xhr.responseText;
        statusCode = xhr.statusCode;
    };

    xhr.onerror = function() {
        alert('There was an error!');
    };
    //alert("pre vracanja " + response);*/
    return xhr.responseText;
}

function sendCredentials(username, password) {
    var responseText;
    var username = username;
    var password = password;
    //var loginName = "branko";
    //var password = "sifra";
    //var token = "Basic " + btoa(loginName + ':' + password);
    var token = "Basic " + btoa(username + ':' + password);
    /*var xhr = createCORSRequest('GET', 'https://178.33.216.114/login/');
     var xhr = createCORSRequest('POST', 'http://192.168.94.244/login/');
     //xhr.setRequestHeader('Content-Type', 'text/html; charset=UTF-8');
     //xhr.setRequestHeader("Access-Control-Allow-Origin", "*");
     xhr.setRequestHeader('Content-Type', 'application/json');
     //xhr.withCredentials = true;
     xhr.setRequestHeader('Authorization', token);
     xhr.send();
     
     xhr.onload = function() {
     responseText = xhr.responseText;
     statusCode = xhr.statusCode;
     console.log(responseText);
     // process the response.
     alert(responseText);
     };
     
     xhr.onerror = function() {
     console.log('There was an error!');
     };
     if (!xhr) {
     throw new Error('CORS not supported');
     alert('CORS not supported');
     }
     
     return xhr.statusCode;*/

    $.ajax({
        type: "POST",
        //url: "http://192.168.94.244/login/",
        url: "https://178.33.216.114/login/",
        async: false,
        contentType: "application/json",
        xhrFields: {
            withCredentials: true
        },
        headers: {
            "Authorization": token,
            //"Access-Control-Allow-Origin": "*"      
        },
        beforeSend: function(xhr) {
            xhr.setRequestHeader("Authorization", token);
        },
        traditional: true,
        success: function(data) {
            //alert(data.statusCode);
            responseText = data;
        },
        failure: function(errMsg) {
            //alert(errMsg);
            responseText = errMsg;
        }
        /*complete: function(jqXHR, textStatus){
         responseText = textStatus;
         }*/
    });
    return responseText;
}

function sendLogout() {
    var responseText;
    /*var loginName = "branko";
     var password = "sifra";
     var token = "Basic " + btoa(loginName + ':' + password);*/
    var token = createAuthToken();
    $.ajax({
        type: "POST",
        //url: "http://192.168.94.244/logout/",
        url: "https://178.33.216.114/logout/",
        async: false,
        contentType: "application/json",
        xhrFields: {
            withCredentials: true
        },
        headers: {
            "Authorization": createAuthToken(),
            //"Access-Control-Allow-Origin": "*"      
        },
        beforeSend: function(xhr) {
            xhr.setRequestHeader("Authorization", token);
        },
        traditional: true,
        success: function(data) {
            //alert(data);
            responseText = data;
        },
        failure: function(errMsg) {
            //alert(errMsg);
            responseText = errMsg;
        }
        /*complete: function(jqXHR, textStatus){
         responseText = textStatus;
         }*/
    });
    return responseText;
}

function sendCoordinates(position) {
    var responseText;
    var position = position;
    var loginName = "branko";
    var password = "sifra";
    var token = "Basic " + btoa(loginName + ':' + password);
    $.ajax({
        type: "POST",
        url: "https://178.33.216.114/location/",
        async: false,
        contentType: "application/x-www-form-urlencoded",
        data: {
            "latitude": position.coords.latitude,
            "longitude": position.coords.longditude,
            "title": "Bistro"
        },
        xhrFields: {
            withCredentials: true
        },
        headers: {
            "Authorization": token,
            //"Access-Control-Allow-Origin": "*"      
        },
        beforeSend: function(xhr) {
            xhr.setRequestHeader("Authorization", token);
        },
        traditional: true,
        success: function(data) {
            //alert(data);
            responseText = data;
        },
        failure: function(errMsg) {
            //alert(errMsg);
            responseText = errMsg;
        }
        /*complete: function(jqXHR, textStatus){
         responseText = textStatus;
         }*/
    });
    return responseText;
}

function createAuthToken() {

    var user = localStorage.getItem("username");
    var pass = localStorage.getItem("password");
    var token = "Basic " + btoa(user + ':' + pass);

    return token;
}