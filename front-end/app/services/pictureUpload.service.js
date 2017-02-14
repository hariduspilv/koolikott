'use strict';

angular.module('koolikottApp')
.factory('pictureUploadService',
[
    'serverCallService', 'Upload',
    function (serverCallService, Upload) {

        var instance = {
            upload: function (file, successCallback, errorCallback, finallyCallback) {

                Upload.dataUrl(file, true).then(function () {
                    var dataBase64 = file.$ngfDataUrl.substring('data:image/png;base64,'.length);

                    var picture = {
                        picture: dataBase64
                    };

                    serverCallService.upload('rest/picture', picture, function(response) {
                        successCallback(response.data);
                    }, errorCallback, finallyCallback);
                });
            },

            uploadFromUrl: function (url) {
                return serverCallService.makePut("/rest/picture/fromUrl", url)
                    .then(response => {
                        return response.data;
                    })
            }
        };

        return instance;
    }
]);
