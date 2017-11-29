'use strict';

{
class controller extends Controller {
    async upload(file_input) {
        try {
            let response = await this.makeUpload('rest/uploadedFile', file_input);

            return response;
        } catch(e) {
            throw e;
        }
    }
    async uploadReview(file_input) {
        try {
            let response = await this.makeUpload('rest/review', file_input, true);

            return response;
        } catch(e) {
            throw e;
        }
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
