var Applist = {
    createEvent: function(package, description, notes, startDate, endDate, action,successCallback, errorCallback) {
        cordova.exec(
            successCallback, // success callback function
            errorCallback, // error callback function
            'Applist', // mapped to our native Java class called "CalendarPlugin"
             action, // with this action name
            [{                  // and this array of custom arguments to create our entry
                "package": package,
                "description": description,
                "eventLocation": location,
                "startTimeMillis": "",
                "endTimeMillis": ""
            }]
        ); 
    }
}
module.exports = Applist;
