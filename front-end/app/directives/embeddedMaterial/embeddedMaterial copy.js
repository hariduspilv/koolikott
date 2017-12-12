'use strict'

{
class controller extends Controller {
    $onInit() {
        this.init()

        this.$scope.$watch('material', () => {
            if (this.$scope.material && this.$scope.material.id) {
                this.$scope.material.source = this.getMaterialSource(this.$scope.material)
                this.$scope.materialType = this.getMaterialIcon()
                this.audioSetup()
                this.sourceTypeAndPdfSetup()
            }
        })

        this.$rootScope.$on('fullscreenchange', () =>
            this.$scope.$apply(() =>
                this.$scope.showMaterialContent = !this.$scope.showMaterialContent
            )
        )
    }
    init(isReInit) {
        this.$scope.showMaterialContent = false
        this.$scope.canPlayVideo = false
        this.$scope.canPlayAudio = false
        this.$scope.videoType = ""
        this.$scope.audioType = ""
        this.$scope.sourceType = ""
        this.$scope.isEditPortfolioPage = this.$rootScope.isEditPortfolioPage
        this.$scope.isEditPortfolioMode = this.$rootScope.isEditPortfolioMode

        if (this.$scope.material) {
            this.$scope.material.source = this.getMaterialSource(this.$scope.material)
            this.$scope.materialType = this.getMaterialIcon()
            this.audioSetup()
            this.videoSetup()
            this.sourceTypeAndPdfSetup()
            // externalUrlProxyModification()
        } else
        // try to get material from route param and init again
        if (!isReInit) {
            console.log('embeddedMaterial.js: init AGAIN')
            this.materialService
                .getMaterialById(this.$route.current.params.id)
                .then(material => {
                    if (material) {
                        this.$scope.material = material

                        if (this.$rootScope.isEditPortfolioMode || this.authenticatedUserService.isAuthenticated())
                            this.$rootScope.selectedSingleMaterial = this.$scope.material

                        this.init(true)
                    }
                })
        }
    }
    fallbackToLink() {
        return !this.$scope.sourceType || this.$scope.sourceType === 'LINK'
    }
    videoSetup(redo) {
        let extension = this.getMaterialSource(this.$scope.material).split('.').pop()
        const video = document.createElement('video')
        video.classList.add('embedded-material__video')

        /* ogv is a subtype of ogg therefore if ogg is supported ogv is also */
        if (extension === 'ogv') {
            extension = 'ogg'
            const { source } = this.$scope.material
            this.$scope.material.source = source.substr(0, source.lastIndexOf('.')) + '.ogg'
        }

        if (video.canPlayType && video.canPlayType('video/' + extension)) {
            this.$scope.videoType = extension
            this.$scope.canPlayVideo = true

            const videoContainer = this.$element[0].querySelector('.embed-video')

            console.log('videoContainer:', videoContainer)

            if (videoContainer) {
                Object.assign(video, {
                    id: 'video',
                    src: this.$scope.material.source,
                    type: 'video/' + extension,
                    controls: true,
                    width: '100%',
                    preload: 'metadata'
                })
                videoContainer.insertBefore(video, null)
                // videoContainer.addEventListener('error', this.videoSetup.bind(this), true)
                video.load()
            } else
            if (redo) {
                console.log('embeddedMaterial.js: videoSetup AGAIN')
                // this.$timeout(() => this.videoSetup(true), 100)
            }
        }
    }
    audioSetup() {
        if (!this.$scope.canPlayVideo) {
            const extension = this.getMaterialSource(this.$scope.material).split('.').pop()
            const audio = document.createElement('audio')

            if (audio.canPlayType && audio.canPlayType('audio/' + extension)) {
                this.$scope.audioType = extension
                this.$scope.canPlayAudio = true
            }
        }
    }
    sourceTypeAndPdfSetup(redo) {
        const type = this.getEmbedType(this.$scope.material)

        switch (type) {
            case 'YOUTUBE':
            case 'SLIDESHARE':
            case 'VIDEO':
            case 'AUDIO':
            case 'PICTURE':
                this.$scope.sourceType = type
                break
            case 'EBOOK':
                if (this.isIE()) {
                    this.$scope.sourceType = 'LINK'
                    this.$scope.material.source += '?archive=true'
                    return
                }

                this.$scope.sourceType = 'EBOOK'
                this.$scope.ebookLink = `/utils/bibi/bib/i/?book=${this.$scope.material.uploadedFile.id}/${this.$scope.material.uploadedFile.name}`

                const ebookContainer = document.querySelector('.embed-ebook-' + this.$scope.material.id)

                if (ebookContainer)
                    ebookContainer.innerHTML = this.iFrameLink(this.$scope.ebookLink)
                else
                if (!redo) {
                    console.log('embeddedMaterial.js: sourceTypeAndPdfSetup AGAIN (EBOOK)')

                    this.$timeout(() => this.sourceTypeAndPdfSetup(true), 100)
                }
                break
            case 'PDF':
                const baseUrl = document.location.origin

                console.log('proxy source: ' + this.$scope.isProxySource)
                console.log('proxy source url: ' + this.proxyUrl)

                if (this.$scope.material.source.startsWith(baseUrl)) {
                    this.$scope.sourceType = 'PDF'
                    this.$scope.material.PDFLink = this.pdfjsLink(this.$scope.material.source)

                    const pdfContainer = document.querySelector('.embed-pdf-' + this.$scope.material.id)

                    if (pdfContainer) {
                        console.log('pdf element setup' + this.$scope.material.PDFLink)

                        pdfContainer.innerHTML = this.iFrameLink(this.$scope.material.PDFLink)
                    } else
                    if (!redo) {
                        console.log('embeddedMaterial.js: sourceTypeAndPdfSetup AGAIN (PDF)')

                        this.$timeout(() => this.sourceTypeAndPdfSetup(true), 100)
                    }
                } else {
                    this.$scope.isProxySource = true
                    this.$scope.sourceType = 'PDF'
                    this.$scope.fallbackType = 'LINK'

                    if (this.$scope.isProxySource) {
                        console.log('material source: ' +this.$scope.material.source)

                        this.proxyUrl = baseUrl + '/rest/material/externalMaterial?url=' + encodeURIComponent(this.$scope.material.source)
                        this.serverCallService
                            .makeHead(this.proxyUrl)
                            .then(this.probeContentSuccess.bind(this))
                    }
                }
                break
            default:
                this.embedService
                    .getEmbed(this.$scope.material.source)
                    .then(({ data: { html } }) => {
                        if (html) {
                            this.$scope.embeddedDataIframe = null
                            this.$scope.embeddedData = null
                            this.$scope.sourceType = 'NOEMBED'

                            html.contains('<iframe')
                                ? this.$scope.embeddedDataIframe = html.replace('http:', '')
                                : this.$scope.embeddedData = html.replace('http:', '')
                        } else
                        if (this.$scope.material.source) {
                            this.$scope.sourceType = 'LINK'

                            // foreign repositories need to have dop_token query param
                            if (this.$scope.material.embeddable &&
                                !this.$scope.material.linkSource &&
                                this.authenticatedUserService.isAuthenticated()
                            )
                                this.getSignedUserData()
                        }
                    })
        }
    }
    getSignedUserData() {
        this.serverCallService
            .makeGet('rest/user/getSignedUserData')
            .then(({ data }) => {
                const { source } = this.$scope.material
                this.$scope.material.linkSource =
                    `${source}${source.includes('?') ? '&' : '?'}dop_token=${encodeURIComponent(data)}`
            }, () =>
                this.$scope.material.linkSource = this.$scope.material.source
            )
    }
    probeContentSuccess({ headers }) {
        console.log('Content probing succeeded!')

        const { 'content-disposition': contentDisposition } = headers()

        if (!contentDisposition) {
            console.log('content is not dispositioned')

            this.$scope.sourceType = this.$scope.fallbackType
            return
        }

        const filename = contentDisposition.match(/filename="(.+)"/)[1]

        if (this.getEmbedType({ source: filename }) === 'PDF') {
            this.$scope.material.PDFLink = this.pdfjsLink(encodeURIComponent(this.proxyUrl))
            console.log('proxy pdf link' + this.$scope.material.PDFLink)
            const pdfContainer = document.querySelector('.embed-pdf-' + this.$scope.material.id)

            if (pdfContainer) {
                console.log("proxy pdf element setup")
                pdfContainer.innerHTML(this.iFrameLink(this.$scope.material.PDFLink))
            } else
                this.$timeout(() => this.sourceTypeAndPdfSetup(true), 100)
        } else {
            console.log('proxy is not possible')
            this.$scope.sourceType = this.$scope.fallbackType
        }
    }
    getMaterialIcon() {
        return this.iconService.getMaterialIcon(this.$scope.material.resourceTypes)
    }
    iFrameLink(link) {
        return `<iframe width="100%" height="500px" src="${link}"></iframe>`
    }
    pdfjsLink(source) {
        return `/utils/pdfjs/web/viewer.html?file=${source}`
    }
}
controller.$inject = [
    '$scope',
    '$rootScope',
    '$sce',
    '$timeout',
    '$route',
    '$location',
    '$element',
    'authenticatedUserService',
    'translationService',
    'iconService',
    'embedService',
    'serverCallService',
    'dialogService',
    'storageService',
    'materialService'
]
directive('dopEmbeddedMaterial', {
    scope: {
        material: '=material',
        hideLink: '<', // no need to display the link in portfolio chapter for it has a footer that displays it
    },
    controllerAs: '$ctrl',
    templateUrl: 'directives/embeddedMaterial/embeddedMaterial.html',
    controller
})
}
