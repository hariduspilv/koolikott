'use strict'

{
class controller extends Controller {
    $onInit() {
        this.isOpen = false
        this.pageUrl = this.$location.absUrl()
        this.pictureName = ''
        this.shareMediaPlaces = [
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
        if (this.isMaterial(this.object))
            this.setShareParams(item)
        else
        if (this.isPortfolio(this.object)) {
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
            templateUrl: 'directives/share/modal/share.modal.html',
            controller: 'shareModalController',
            controllerAs: '$ctrl',
            targetEvent,
            locals: {
                item,
                portfolio: this.object,
                setShareParams: this.setShareParams.bind(this),
                isOwner: this.isOwner.bind(this),
                isPrivate: this.isPrivate.bind(this)
            }
        })
    }
    setShareParams({ provider } = {}) {
        switch (provider) {
            case 'facebook':
                this.Socialshare.share({
                    provider,
                    attrs: {
                        socialshareUrl: this.pageUrl,
                        socialshareTitle: this.$translate.instant('READING_RECOMMENDATION') + ': ' + this.title,
                        socialshareMedia: this.$location.$$protocol + '://' + this.$location.$$host + '/rest/picture/thumbnail/lg/' + this.pictureName,
                        socialshareType: 'share',
                        socialshareVia: this.FB_APP_ID
                    }
                })
                break
            case 'twitter':
                this.Socialshare.share({
                    provider,
                    attrs: {
                        socialshareUrl: this.pageUrl,
                        socialshareText: this.$translate.instant('READING_RECOMMENDATION') + ': ' + this.title
                    }
                })
                break
            case 'email':
                this.Socialshare.share({
                    provider,
                    attrs: {
                        socialshareSubject: this.$translate.instant('READING_RECOMMENDATION') + ': ' + this.title,
                        socialshareBody: this.$translate.instant('WELCOME_READ_HERE') + ': ' + this.pageUrl
                    }
                })
                break
        }
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
]

component('dopShare', {
    bindings: {
        title: '<',
        object: '<'
    },
    templateUrl: 'directives/share/share.html',
    controller
})
}
