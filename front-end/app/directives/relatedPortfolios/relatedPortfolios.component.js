'use strict'

{
    class controller extends Controller {
        $onInit() {

            const itemCount = 10;
            this.$scope.limit = itemCount;
            this.$scope.startFrom = 0;
            this.$scope.relatedPortfolios = []

            this.getMaterialRelatedPortfolios()

            this.$scope.showFirstButton = () => {
                return this.$scope.limit > itemCount;
            };

            this.$scope.showNextButton = () => {
                return this.numberOfDisplayed() < this.$scope.relatedPortfolios.length
            };

            this.$scope.reset = () => {
                this.$scope.startFrom = 0;
                this.$scope.limit = itemCount;
            };

            this.$scope.showNextItems = () => {
                this.$scope.limit += itemCount;
            };
        }

        $onChanges({learningObject}) {
            if (learningObject && learningObject.currentValue !== learningObject.previousValue)
                this.getMaterialRelatedPortfolios()
        }

        numberOfDisplayed() {
            return this.$scope.startFrom + this.$scope.limit;
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
            return this.authenticatedUserService.isAuthenticated() &&
            portfolio && portfolio.creator ? portfolio.creator.id === this.authenticatedUserService.getUser().id
                : false
        }

        userCanClickPortfolio(portfolio) {
            if (this.$scope.isAdmin || portfolio.visibility === 'PUBLIC' || this.isOwner(portfolio)) {
                return 'auto';
            }
            return 'none';
        }

    }

    controller.$inject = [
        '$scope',
        'materialService',
        'authenticatedUserService'
    ]

    component('dopRelatedPortfolios', {
        controller,
        template: `
              <div data-ng-if="relatedPortfolios.length > 0" class="portfolio-material" data-layout="column">
            <span data-translate="PORTFOLIO_RELATED_PORTFOLIOS"></span>
            <div data-ng-repeat="portfolio in relatedPortfolios | limitTo : limit : startFrom " flex data-layout="row">
              <a
                data-ng-style="{'pointer-events':$ctrl.userCanClickPortfolio(portfolio)}"
                target="_blank"
                data-ng-href="{{'/portfolio?id=' + portfolio.id}}">{{portfolio.title}}<span class="span-margin-left" data-ng-if="$ctrl.userCanClickPortfolio(portfolio) === 'none'" data-translate="PORTFOLIO_PRIVATE"></span></a>
            </div>
            <button
              data-ng-show="showNextButton()" data-ng-click="showNextItems()"><span class="span-margin-left" data-translate="PORTFOLIO_SHOW_MORE"></span>
            </button>
            <button
              data-ng-show="showFirstButton()" data-ng-click="reset()"><span class="span-margin-left" data-translate="PORTFOLIO_SHOW_LESS"></span>
            </button>
          </div>
        `,
        bindings: {
            learningObject: '<'
        }
    })
}

