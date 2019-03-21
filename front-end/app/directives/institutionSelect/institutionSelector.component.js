'use strict'

{
class controller extends Controller {
    $onInit() {
        this.$scope.userProfile = [{}]
        this.getEHISInstitutionAreas()
        this._previousinstitution = this.institution
    }

    $onChanges(changes) {
        if (changes.clearSelection.currentValue)
            this.institution = {}
    }
    $doCheck() {
        const institutionChanged =
            (this.institution && !this._previousinstitution) ||
            (!this.institution && this._previousinstitution) ||
            !_.isEqual(this.institution, this._previousinstitution)

        if (institutionChanged)
            this._previousinstitution = this.institution

    }

    clearSearchTerm() {
        this.$scope.searchTerm = ''
    }

    getEHISInstitutionAreas() {
        this.serverCallService.makeGet('rest/ehisInstitution/areas')
            .then(response => {
                this.$scope.institutionAreas = response.data
                if (this.institution) {
                    this.$scope.userProfile.area = this.institution.area
                    this.serverCallService.makeGet('rest/ehisInstitution/institutions/?area=' + this.$scope.userProfile.area)
                        .then((response) => {
                            this.$scope.institutions = response.data
                            this.$scope.userProfile.institution = this.institution
                        })
                }
            })
    }

    handleAreaChange() {
            this.$scope.userProfile.selectedSchools = [{}]
            this.serverCallService.makeGet('rest/ehisInstitution/institutions/?area=' + this.$scope.userProfile.area)
                .then(response => {
                    this.$scope.institutions = response.data
                })
    }

    reset(parentTaxon) {
        this.taxon = parentTaxon;
    };

    selectInstitution(institution) {
        this.institution = institution
    }
}
controller.$inject = [
    '$scope',
    '$rootScope',
    '$timeout',
    '$filter',
    'metadataService',
    'taxonService',
    'serverCallService'
]
component('dopInstitutionSelector', {
    bindings: {
        institution: '=',
        clearSelection: '<'
    },
    templateUrl: 'directives/institutionSelect/institutionSelector.html',
    controller
})
}
