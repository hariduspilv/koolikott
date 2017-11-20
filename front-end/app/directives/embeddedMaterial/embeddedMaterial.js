'use strict'

{
class controller extends Controller {
    $onInit() {
        this.init()

        this.$scope.removeMaterial = this.removeMaterial.bind(this)
        this.$scope.moveItem = this.moveItem.bind(this)
        this.$scope.listItemUp = this.listItemUp.bind(this)
        this.$scope.listItemDown = this.listItemDown.bind(this)
        this.$scope.navigateToMaterial = this.navigateToMaterial.bind(this)
        this.$scope.fallbackToLink = this.fallbackToLink.bind(this)

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
    removeMaterial($event, material) {
        $event.preventDefault()
        $event.stopPropagation()

        this.dialogService.showDeleteConfirmationDialog(
            'PORTFOLIO_DELETE_MATERIAL_CONFIRM_TITLE',
            'PORTFOLIO_DELETE_MATERIAL_CONFIRM_MESSAGE',
            () => this.$scope.contentRow.learningObjects.splice(
                this.$scope.contentRow.learningObjects.indexOf(material),
                1
            )
        )
    }
    moveItem(origin, destination) {
        if (this.$scope.contentRow && this.$scope.contentRow.learningObjects.length < 2)
            this.$scope.chapter.contentRows.splice(origin, 1)

        this.$scope.chapter.contentRows.splice(destination, 0, {
            learningObjects: [this.$scope.material]
        })
    }
    listItemUp() {
        this.$scope.moveItem(this.$scope.rowIndex, this.$scope.rowIndex - 1)
    }
    listItemDown() {
        this.$scope.moveItem(this.$scope.rowIndex, this.$scope.rowIndex + 1)
    }
    navigateToMaterial(material, $event) {
        $event.preventDefault()
        this.storageService.setMaterial(material)
        this.$location.path('/material').search({ id: material.id })
    }
    fallbackToLink() {
        return this.$scope.isEditPortfolioMode || !this.$scope.sourceType || this.$scope.sourceType === 'LINK'
    }
    videoSetup(redo) {
        let extension = this.getMaterialSource(this.$scope.material).split('.').pop()
        const video = document.createElement('video')

        /* ogv is a subtype of ogg therefore if ogg is supported ogv is also */
        if (extension === 'ogv') {
            extension = 'ogg'
            const { source } = this.$scope.material
            this.$scope.material.source = source.substr(0, source.lastIndexOf('.')) + '.ogg'
        }

        if (video.canPlayType && video.canPlayType('video/' + extension)) {
            this.$scope.videoType = extension
            this.$scope.canPlayVideo = true

            const videoContainer = document.querySelector('.embed-video-' + this.$scope.material.id)

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
                videoContainer.addEventListener('error', this.videoSetup.bind(this), true)
                video.load()
            } else
            if (!redo) {
                console.log('embeddedMaterial.js: videoSetup AGAIN')
                this.$timeout(() => this.videoSetup(true), 100)
            }
        }
    }
    audioSetup() {
        if (!this.$scope.canPlayVideo) {
            const extension = this.getMaterialSource(this.$scope.material).split('.').pop()
            const video = document.createElement('audio')

            if (video.canPlayType && video.canPlayType('audio/' + extension)) {
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

                    console.log('proxy tree')

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
                    .then(({ data }) => {
                        if (data && data.html) {
                            this.$scope.embeddedDataIframe = null
                            this.$scope.embeddedData = null
                            this.$scope.sourceType = 'NOEMBED'

                            data.html.contains('<iframe')
                                ? this.$scope.embeddedDataIframe = data.html.replace('http:', '')
                                : this.$scope.embeddedData = data.html.replace('http:', '')
                        } else
                        if (this.$scope.material.source)
                            this.$scope.iframeSource = this.$sce.trustAsResourceUrl(
                                this.$scope.material.source.replace('http:', '')
                            )
                    })
        }
    }
    probeContentSuccess({ headers }) {
        console.log('Content probing succeeded!')
        console.log('Content probing succeeded! twice is the charm')

        const { 'content-disposition': contentDisposition } = headers()

        if (!contentDisposition) {
            console.log('content is not dispositioned')

            this.$scope.sourceType = this.$scope.fallbackType
            return
        }

        const filename = contentDisposition.match(/filename="(.+)"/)[1]

        if (this.getEmbedType({ source: filename }) === 'PDF') {
            console.log('it is pdf baby')

            this.$scope.material.PDFLink = this.pdfjsLink(encodeURIComponent(this.proxyUrl))

            console.log('proxy pdf link' + this.$scope.material.PDFLink)

            const pdfContainer = document.querySelector('.embed-pdf-' + this.$scope.material.id)

            if (pdfContainer) {
                console.log("proxy pdf element setup")

                pdfContainer.innerHTML(this.iFrameLink(this.$scope.material.PDFLink))
            } else
                this.$timeout(() => this.sourceTypeAndPdfSetup(true), 100)
        } else {
            console.log('everything is very sad')

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
        chapter: '=',
        objIndex: '=',
        rowIndex: '=',
        contentRow: '=',
        embeddable: '='
    },
    controllerAs: '$ctrl',
    templateUrl: 'directives/embeddedMaterial/embeddedMaterial.html',
    controller
})
}
