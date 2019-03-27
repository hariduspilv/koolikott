'use strict'

{
    class controller extends Controller {
        $onInit() {

            this.$scope.limit = 2;
            this.$scope.startFrom = 0;

            this.getMaterialRelatedPortfolios()
            this.$scope.showNextItemButton = () => {
                return (this.$scope.limit < this.$scope.relatedPortfolios.length && (this.$scope.startFrom + this.$scope.limit < this.$scope.relatedPortfolios.length))
            };
            this.$scope.showNextItems = () => {
                if (this.$scope.startFrom + this.$scope.limit > this.$scope.relatedPortfolios.length - this.$scope.limit) {
                    this.$scope.startFrom = this.$scope.relatedPortfolios.length - this.$scope.limit;
                } else
                    this.$scope.startFrom += this.$scope.limit;
            };
            this.$scope.showFirstItemButton = () => {
                return this.$scope.startFrom > 0;
            };

            this.$scope.showFirstItems = () => {
                this.$scope.startFrom = 0;
            };

            this.$scope.isAdmin = this.authenticatedUserService.isAdmin()

        }

        $onChanges({learningObject}) {
            console.log(this.learningObject)
            this.getMaterialRelatedPortfolios()
        }

        getMaterialRelatedPortfolios() {
            if (this.learningObject && this.learningObject.id) {
                this.materialService.getRelatedPortfolios(this.learningObject.id)
                    .then(response => {
                        this.$scope.relatedPortfolios = response.data;
                    })
            }
        }

        isAdmin() {
            return this.authenticatedUserService.isAdmin()
        }

        isOwner(portfolio) {
            return !this.authenticatedUserService.isAuthenticated() ? false : portfolio && portfolio.creator ? portfolio.creator.id === this.authenticatedUserService.getUser().id
                    : false
        }

        notAdmin(portfolio) {
            if (portfolio.visibility === 'PUBLIC') {
                return 'auto'
            } else {
                if (this.isOwner(portfolio))
                    return 'auto'
                else
                    return 'none'
            }
        }

        userHasAccessToPortfolio(portfolio) {
            if (this.$scope.isAdmin)
                return 'auto'
            else {
                this.notAdmin()

                // return this.isOwner(portfolio) ? 'auto' : 'none'
            }
        }
    }

    controller.$inject = [
        '$scope',
        '$rootScope',
        '$location',
        '$mdDialog',
        'dialogService',
        'materialService',
        'authenticatedUserService'
    ]

    component('dopRelatedPortfolios', {
        controller,
        template: `
              <div class="portfolio-material" data-layout="column">
            <span data-translate="SEOTUD_KOGUMIKUD"></span>
            <div data-ng-repeat="portfolio in relatedPortfolios | limitTo : limit : startFrom ">
              <a
                data-ng-style="{'pointer-events':$ctrl.userHasAccessToPortfolio(portfolio)}"
                target="_blank"
                data-ng-href="{{'/portfolio?id=' + portfolio.id}}">{{portfolio.title}}</a>
            </div>
            <button
              data-ng-show="$ctrl.showNextItemButton()" data-ng-click="$ctrl.showNextItems()">Show more
            </button>
            <button
              data-ng-show="$ctrl.showFirstItemButton()" data-ng-click="$ctrl.showFirstItems()">Show less
            </button>
          </div>
        `,
        bindings : {
            learningObject: '<'
        }
    })
}
