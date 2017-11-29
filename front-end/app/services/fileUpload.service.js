'use strict';

{
class controller extends Controller {
    upload(file_input) {
        return this.makeUpload('rest/uploadedFile', file_input);
    }
    uploadReview(file_input) {
        return this.makeUpload('rest/review', file_input, true);
    }
    makeUpload(url, file_input, isReview) {
        return new Promise((resolve, reject) => {
            let uploaded = this.Upload.dataUrl(file_input, true).then(() => {
                let file = {};

                if (isReview) {
                    file.review = file_input;
                } else {
                    file.file = file_input;
                }

                return this.serverCallService.upload(url, file).then(response => {
                    return response;
                });
            });

            resolve(uploaded);
        });
    }
}
controller.$inject = [
    'serverCallService',
    'Upload'
]
factory('fileUploadService', controller)
}
