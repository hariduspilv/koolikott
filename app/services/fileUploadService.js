define([
    'angularAMD',
    'ng-file-upload',
    'services/serverCallService'
], function (angularAMD) {
    var instance;

    angularAMD.factory('fileUploadService', ['serverCallService', 'Upload',
        function (serverCallService, Upload) {

            instance = {
                upload: function (file_input, successCallback, errorCallback, finallyCallback) {

                    Upload.dataUrl(file_input, true).then(function () {

                        var file = {
                            file: file_input
                        };

                        serverCallService.upload('rest/uploadedFile', file, function(response) {
                            successCallback(response.data);
                        }, errorCallback, finallyCallback);
                    });
                }
            };

            return instance;
        }
    ]);
});
