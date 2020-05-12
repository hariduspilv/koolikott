'use strict'

{
class controller extends Controller {
    $onInit() {
        this.isOpen = false
        this.pictureName = ''
        this.shareMediaPlaces = [
            { provider: 'url' },
            { provider: 'email', icon: 'icon-mail-squared' },
            { provider: 'twitter', icon: 'icon-twitter-squared' },
            { provider: 'facebook', icon: 'icon-facebook-squared' }
        ]

        this.$timeout(() => {
            if (this.object && this.object.picture)
                this.pictureName = this.object.picture.name

        })
    }

    isVisible() {
        if (this.object && this.object.deleted || this.$rootScope.isEditPortfolioPage)
            return false

        if (this.$rootScope.isViewMaterialPage)
            return true

        if (this.object) {
            if (this.isPublic() ||
                this.isNotListed() ||
                this.isOwner() ||
                this.authenticatedUserService.isAdmin() ||
                this.authenticatedUserService.isModerator()
            )
                return true
            else if (this.isPrivate())
                return false
        }

        return false
    }
    isPublic() {
        return this.object.visibility === 'PUBLIC'
    }
    isPrivate() {
        console.log()
        return this.object.visibility === 'PRIVATE'
    }
    isNotListed() {
        return this.object.visibility === 'NOT_LISTED'
    }
    isOwner () {
        if (!this.authenticatedUserService.isAuthenticated())
            return false

        if (this.object && this.object.creator)
            return this.object.creator.id === this.authenticatedUserService.getUser().id
    }
    share($event, item) {
        if (this.isPortfolio(this.object) || this.isMaterial(this.object)) {
            if (
                (!this.isOwner() && !this.isPublic()) ||
                (this.isOwner() && this.isPrivate())
            ) {
                $event.preventDefault()
                this.showWarningDialog($event, item)
            } else
                this.setShareParams(item)
        }
    }
    showWarningDialog(targetEvent, item) {
        this.$mdDialog.show({
            templateUrl: '/directives/share/modal/share.modal.html',
            controller: 'shareModalController',
            controllerAs: '$ctrl',
            locals: {
                learningObject: this.object
            }
        })
    }
    setShareParams({ provider } = {}) {
        this.pageUrl = window.location.origin + window.location.pathname
        if (this.slug) {
            this.pageUrl += '#' + this.slug
        }
        switch (provider) {
            case 'facebook':
                this.Socialshare.share({
                    provider,
                    attrs: {
                        socialshareUrl: this.pageUrl,
                        socialshareType: 'share',
                        socialshareVia: this.FB_APP_ID
                    }
                })

                if(this.pageUrl.contains('kogumik')){
                    gTagCaptureEventWithLabel('share', 'teaching portfolio', 'Facebook')
                } else {
                    gTagCaptureEventWithLabel('share', 'teaching material', 'Facebook')
                }

                break
            case 'twitter':
                this.Socialshare.share({
                    provider,
                    attrs: {
                        socialshareUrl: this.pageUrl,
                        socialshareText: this.$translate.instant('READING_RECOMMENDATION') + ': ' + this.title
                    }
                })

                if (this.pageUrl.contains('kogumik')){
                    gTagCaptureEventWithLabel('share', 'teaching portfolio', 'Twitter')
                } else if (this.pageUrl.contains('oppematerjal')){
                    gTagCaptureEventWithLabel('share', 'teaching material', 'Twitter')
                }

                break
            case 'email':
                this.Socialshare.share({
                    provider,
                    attrs: {
                        socialshareSubject: this.$translate.instant('READING_RECOMMENDATION') + ': ' + this.title,
                        socialshareBody: this.$translate.instant('WELCOME_READ_HERE') + ': ' + this.pageUrl
                    }
                })

                if (this.pageUrl.contains('kogumik')){
                    gTagCaptureEventWithLabel('share', 'teaching portfolio', 'E-mail')
                } else if (this.pageUrl.contains('oppematerjal')){
                    gTagCaptureEventWithLabel('share', 'teaching material', 'E-mail')
                }

                break
            case 'url':
                this.copyToClipboard(this.pageUrl)
                if (!this.slug) {
                    this.toastService.show('COPY_PERMALINK_SUCCESS')
                } else {
                    this.toastService.show('COPY_CHAPTER_LINK')
                }

                if (this.pageUrl.contains('kogumik')){
                    gTagCaptureEventWithLabel('copy', 'teaching portfolio', 'link')
                } else if (this.pageUrl.contains('oppematerjal')){
                    gTagCaptureEventWithLabel('copy', 'teaching material', 'link')
                }

                break
        }
    }

    copyToClipboard(pageUrl) {
        const el = document.createElement('textarea');
        el.value = pageUrl;
        document.body.appendChild(el);
        el.select();
        document.execCommand('copy');
        document.body.removeChild(el);
    }

    addStyle() {
        const element = document.getElementsByClassName('card-menus')
        element[0].style.cssText = "z-index: 10"
    }

    removeStyle() {
        const element = document.getElementsByClassName('card-menus')
        element[0].style.cssText = "z-index: 1"
        this.isOpen = false
    }
}
controller.$inject = [
    '$rootScope',
    '$location',
    '$mdDialog',
    '$timeout',
    '$translate',
    'authenticatedUserService',
    'Socialshare',
    'FB_APP_ID',
    'toastService'
]

component('dopShare', {
    bindings: {
        title: '<',
        object: '<',
        slug: '<'
    },
    templateUrl: '/directives/share/share.html',
    controller
})
}
