'use strict'

{
    class controller extends Controller {
        $onInit() {
            this.eventService.subscribe(this.$scope, 'taxonService:mapInitialized', this.getTaxonObject.bind(this))
            this.eventService.subscribe(this.$scope, 'portfolio:reloadTaxonObject', this.getTaxonObject.bind(this))
            this.eventService.subscribe(this.$scope, 'material:reloadTaxonObject', this.getTaxonObject.bind(this));
        }

        $onChanges( {learningObject}  ) {
            if (learningObject.currentValue !== learningObject.previousValue)
            this.getTaxonObject()
        }

        getTaxonObject() {
            if (this.learningObject && this.learningObject.taxons)
                this.$scope.taxonObject = this.taxonGroupingService.getTaxonObject(this.learningObject.taxons)

            if (this.$scope.taxonObject) {
                this.$scope.isVocationalOnly = _.every(this.$scope.taxonObject.educationalContexts, o => o === 'VOCATIONALEDUCATION')
            }
        }

    }

    controller.$inject = [
        '$location',
        'authenticatedUserService',
        'eventService',
        '$scope',
        'taxonGroupingService',
    ]

    component('dopTaxonMeta', {
        bindings: {
            learningObject: '<'
        },
        controller,
        templateUrl: 'directives/taxonMeta/taxonMeta.html'
    })
}
