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
            let response = await this.makeUpload('rest/review', file_input);

            return response;
        } catch(e) {
            throw e;
        }
    }
    makeUpload(url, file_input) {
        return new Promise((resolve, reject) => {
            let uploaded = this.Upload.dataUrl(file_input, true).then(() => {
                var file = {
                    file: file_input
                };

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
