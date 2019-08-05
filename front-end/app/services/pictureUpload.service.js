'use strict'

{
class controller extends Controller {
    upload(file) {
        return this.Upload.dataUrl(file, true).then(() =>
            this.serverCallService.makePost('rest/picture', {
                picture: file.$ngfDataUrl.substring('data:image/png;base64,'.length)
            })
        )
    }
    uploadFromUrl(url) {
        return this.serverCallService.makePut('/rest/picture/fromUrl', url)
    }
}
controller.$inject = [
    'serverCallService',
    'Upload'
]
factory('pictureUploadService', controller)
}
