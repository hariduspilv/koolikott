'use strict'

{
class controller extends Controller {
    constructor(...args) {
        super(...args)

        if (this.locals.isPrivate() && (
                this.locals.isOwner() ||
                this.authenticatedUserService.isAdmin() ||
                this.authenticatedUserService.isModerator()
            )
        ) {
            this.showButtons = true
            this.title = this.$translate.instant('THIS_IS_PRIVATE')
            this.context = this.$translate.instant('SHARE_PRIVATE_PORTFOLIO')
            this.ariaLabel = this.$translate.instant('THIS_IS_PRIVATE')
        } else {
            this.title = this.$translate.instant('THIS_IS_UNLISTED')
            this.context = this.$translate.instant('THINK_AND_SHARE')
            this.ariaLabel = this.$translate.instant('THIS_IS_UNLISTED')
        }
    }
    updatePortfolio(state) {
        const portfolioClone = angular.copy(this.locals.portfolio)
        portfolioClone.visibility = state

        this.serverCallService
            .makePost('rest/portfolio/update', portfolioClone)
            .then(({ data }) => {
                if (data) {
                    this.locals.portfolio.visibility = data.visibility
                    this.toastService.show('PORTFOLIO_SAVED')
                }
            })
        this.locals.setShareParams(this.locals.item)
        this.$mdDialog.cancel()
    }
    back() {
        this.$mdDialog.cancel()
    }
    success() {
        this.locals.setShareParams(this.locals.item)
        this.$mdDialog.cancel()
    }
}
controller.$inject = [
    '$mdDialog',
    '$translate',
    'locals',
    'authenticatedUserService',
    'serverCallService',
    'toastService'
]
angular.module('koolikottApp').controller('shareModalController', controller)
}
