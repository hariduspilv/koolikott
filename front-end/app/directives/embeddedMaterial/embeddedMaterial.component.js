'use strict'

{
class controller extends Controller {
    $onChanges({ material }) {
        if (material.currentValue && material.currentValue !== material.previousValue) {
            this.init(material.currentValue)
        }
    }
    init({ id, source, uploadedFile, embeddable }) {
        const materialSource = this.getMaterialSource(this.material)
        this.$scope.materialSource = materialSource
        let ext = materialSource.split('.').pop()

        const type = this.getEmbeddedMaterialType(this.material)
        switch (type) {
            case 'YOUTUBE':
            case 'SLIDESHARE':
            case 'PICTURE':
                this.$scope.sourceType = type
                break
            case 'VIDEO':
                this.$scope.sourceType = type

                // ogv is a subtype of ogg therefore if ogg is supported ogv is also
                if (ext === 'ogv')
                    ext = 'ogg'

                this.$scope.extName = ext
                this.$scope.canPlayVideo = this.canPlayType('video', ext)
                break
            case 'AUDIO':
                this.$scope.sourceType = type
                this.$scope.extName = ext
                this.$scope.canPlayAudio = this.canPlayType('audio', ext)
                break
            case 'EBOOK':
                if (this.isIE()) {
                    this.$scope.sourceType = 'LINK'
                    this.$scope.materialSource += '?archive=true'
                    return
                }
                this.$scope.sourceType = 'EBOOK'
                this.$scope.ebookLink = `/utils/bibi/bib/i/?book=${uploadedFile.id}/${uploadedFile.name}`
                break
            case 'PDF':
                if (this.isInternalPDF(materialSource)) {
                    this.$scope.sourceType = 'PDF'
                    this.$scope.pdfLink = this.pdfjsLink(materialSource)
                } else
                    this.serverCallService
                        .makeHead('/rest/material/externalMaterial?url=' + encodeURIComponent(materialSource))
                        .then(({ headers }) => {
                            const { 'content-disposition': contentDisposition } = headers()
                            const proxyType = contentDisposition && this.getEmbeddedMaterialType({
                                source: contentDisposition.match(/filename="(.+)"/)[1]
                            })

                            if (proxyType !== 'PDF')
                                return this.$scope.sourceType = 'LINK'

                            this.$scope.sourceType = 'PDF'
                            this.$scope.pdfLink = this.pdfjsLink(
                                encodeURIComponent('/rest/material/externalMaterial?url=' + materialSource)
                            )   
                        })
                break
            default:
                this.$http
                    .get('https://noembed.com/embed?url=' + materialSource)
                    .then(({ data: { html } }) => {
                        if (html) {
                            this.$scope.sourceType = 'NOEMBED'
                            this.$scope.noEmbedIsIframe = html.contains('<iframe')
                            this.$scope.noEmbedData = html.replace('http:', '')
                        } else
                        if (materialSource) {
                            this.$scope.sourceType = 'LINK'

                            // foreign repositories need to have dop_token query param
                            if (embeddable &&
                                !this.$scope.linkSource &&
                                this.authenticatedUserService.isAuthenticated()
                            )
                                this.serverCallService
                                    .makeGet('rest/user/getSignedUserData')
                                    .then(({ data }) =>
                                        this.$scope.materialSource =
                                            `${materialSource}${materialSource.includes('?') ? '&' : '?'}dop_token=${encodeURIComponent(data)}`
                                    )
                        }
                    })
        }
    }
    isInternalPDF(materialSource) {
        return materialSource.startsWith(document.location.origin) || (
            document.location.origin === 'http://0.0.0.0:3001' &&
            materialSource.includes('netgroupdigital.com')
        )
    }
    canPlayType(type, ext) {
        const media = document.createElement(type)
        return typeof media.canPlayType === 'function' && media.canPlayType(type+'/'+ext)
    }
    pdfjsLink(source) {
        return `/utils/pdfjs/web/viewer.html?file=${source}`
    }
}
controller.$inject = [
    '$scope',
    '$http',
    'authenticatedUserService',
    'serverCallService',
]
component('dopEmbeddedMaterial', {
    bindings: {
        material: '<'
    },
    templateUrl: 'directives/embeddedMaterial/embeddedMaterial.html',
    controller
})
}
