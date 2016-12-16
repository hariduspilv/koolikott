'use strict';

angular.module('koolikottApp')
.factory('fileUploadService',
[
    'serverCallService', 'Upload',
    function (serverCallService, Upload) {

        var instance = {
            upload: function (file_input, successCallback, errorCallback, finallyCallback) {

                Upload.dataUrl(file_input, true).then(function () {

                    var file = {
                        file: file_input
                    };

                    serverCallService.upload('rest/uploadedFile', file, function(response) {
                        successCallback(response.data);
                    }, errorCallback, finallyCallback);
                });
            },
            uploadReview: function (file_input, successCallback, errorCallback, finallyCallback) {

                Upload.dataUrl(file_input, true).then(function () {

                    var review = {
                        review: file_input
                    };

                    serverCallService.upload('rest/review', review, function(response) {
                        successCallback(response.data);
                    }, errorCallback, finallyCallback);
                });
            }
        };

        return instance;
    }
]);
