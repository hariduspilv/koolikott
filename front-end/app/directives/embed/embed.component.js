'use strict'

{
class controller extends Controller {
    $onChanges({ data }) {
        if (data.currentValue && data.currentValue !== data.previousValue) {
            this.init(data.currentValue)
        }
    }
    init({ uploadedFile, embeddable, embedSource }) {
        const isMaterial = this.isMaterial(this.data)
        const type = isMaterial ? this.getEmbeddedMaterialType(this.data) : this.getEmbeddedMediaType(this.data)
        const url = isMaterial ? this.getMaterialSource(this.data) : this.data.url

        this.$scope.url = url
        this.$scope.deleted = this.data.deleted
        this.$scope.private = this.data.visibility === 'PRIVATE'
        this.$scope.materialTitle = this.getCurrentMaterialTitle()
        this.$scope.hasInvalidLicense = this.isLicenseTypeInvalid()

        this.$scope.isAdminOrModerator = this.authenticatedUserService.isAdmin() || this.authenticatedUserService.isModerator()

        switch (type) {
            case 'YOUTUBE':
            case 'PICTURE':
                this.$scope.type = type
                break
            case 'VIDEO':
            case 'AUDIO':
                this.$scope.type = type
                this.$scope.extName = this.getExtension(url)
                this.$scope.canPlay = this.canPlayType(type.toLowerCase(), this.$scope.extName)
                break
            case 'SLIDESHARE':
                if (!isMaterial)
                    return console.error('Slideshare url is not embeddable as media file', url)

                this.$scope.type = type
                break
            // case 'EBOOK'://TODO to fix embbeded epub issue.probably smth with url
            //     if (this.isIE()) {
            //         this.$scope.type = 'LINK'
            //         this.$scope.url += '?archive=true'
            //         return
            //     }
            //     this.$scope.type = 'EBOOK'
            //     this.$scope.url = `/utils/bibi/bib/i/?book=${uploadedFile.id}/${uploadedFile.name}`
            //     break
            case 'PDF':
                if (!isMaterial)
                    return console.error('E-books and PDFs are not embeddable as media files', url)

                if (this.isInternalPDF(url)) {
                    this.$scope.type = 'PDF'
                    this.$scope.url = this.getPDFJSURL(url)
                } else
                    this.serverCallService
                        .makeHead(`/rest/material/externalMaterial?id=${this.data.id}&url=${encodeURIComponent(url)}`)
                        .then(({ headers }) => {
                            const { 'content-disposition': contentDisposition } = headers()
                            const proxyType = contentDisposition && this.getEmbeddedMaterialType({
                                source: contentDisposition.match(/filename="(.+)"/)[1]
                            })

                            if (proxyType !== 'PDF')
                                return this.$scope.type = 'LINK'

                            this.$scope.type = 'PDF'
                            this.$scope.url = this.getPDFJSURL(
                                encodeURIComponent(`/rest/material/externalMaterial?id=${this.data.id}&url=${url}`)
                            )
                        })
                break
            case 'EMBEDSOURCE':
                if (!isMaterial)
                    return console.error('This is no EMBEDSOURCE material')

                this.$scope.type = type
                this.$scope.embedSource = embedSource
                break
            case 'SOUNDCLOUD':
            default:
                if (!isMaterial && type !== 'SOUNDCLOUD')
                    return console.error('This url is not embeddable as media file', url)

                this.$http
                    .get('https://noembed.com/embed?url=' + url)
                    .then(({ data: { html } }) => {
                        if (html) {
                            this.$scope.type = 'NOEMBED'
                            this.$scope.noEmbedIsIframe = html.contains('<iframe')
                            this.$scope.noEmbedData = html.replace('http:', '')
                        } else
                        if (url) {
                            this.$scope.type = 'LINK'

                            // foreign repositories need to have dop_token query param
                            if (embeddable &&
                                !this.$scope.linkSource &&
                                this.authenticatedUserService.isAuthenticated()
                            )
                                this.serverCallService
                                    .makeGet('rest/user/getSignedUserData')
                                    .then(({ data }) => {
                                        this.$scope.displayUrl = angular.copy(this.$scope.url);
                                        this.$scope.url =
                                            `${url}${url.includes('?') ? '&' : '?'}dop_token=${encodeURIComponent(data)}`
                                        }
                                    )
                        }
                    })
        }
    }
    getExtension(url) {
        const ext = url.split('.').pop()
        // ogv is a subtype of ogg therefore if ogg is supported ogv is also
        return ext === 'ogv' ? 'ogg' : ext
    }
    isInternalPDF(url) {
        return url.startsWith(document.location.origin) || (
            document.location.origin === 'http://0.0.0.0:3001' &&
            url.includes('netgroupdigital.com')
        )
    }
    canPlayType(type, ext) {
        const media = document.createElement(type)
        return typeof media.canPlayType === 'function' && media.canPlayType(type+'/'+ext)
    }
    getPDFJSURL(url) {
        return `/utils/pdfjs/web/viewer.html?file=${url}`
    }

    getCurrentMaterialTitle() {
        return this.replaceSpaces(this.getCorrectLanguageTitle(this.data))
    }

    isLicenseTypeInvalid() {
        const licenseType = this.data.licenseType.id
        return licenseType === 1 || licenseType === 7 || licenseType === 4
    }
}
controller.$inject = [
    '$scope',
    '$rootScope',
    '$http',
    'authenticatedUserService',
    'serverCallService',
    'translationService',
]
component('dopEmbed', {
    bindings: {
        data: '<',
        hideLink: '<',
    },
    templateUrl: 'directives/embed/embed.html',
    controller
})
}
