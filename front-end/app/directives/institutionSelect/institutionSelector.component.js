'use strict'

{
class controller extends Controller {
    $onInit() {
        this.$scope.userProfile = [{}]
        this.getEHISInstitutionAreas()
/*        this.$scope.$on('detailedSearch:prefillTaxon', (evt, taxon) => {
           this.institution = institution
           this.buildInstitutionPath()
        })*/
        this._previousinstitution = this.institution
        this._previousMarkRequired = this.markRequired
        /*if (this.institution) {
            this.$scope.userProfile.area = this.institution.area
        }*/
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
            this.buildInstitutionPath()

        if (institutionChanged)
            this._previousinstitution = this.institution

    }


    getEHISInstitutionAreas() {
        this.serverCallService.makeGet('rest/ehisInstitution/areas')
            .then(response => {
                this.$scope.institutionAreas = response.data
                if (this.institution) {
                    this.$scope.userProfile.area = this.institution.area
                    /*this.serverCallService.makeGet('rest/ehisInstitution/institutions/?area=' + this.$scope.userProfile.area)
                        .then( (response) => {
                            this.$scope.institutions = response.data
                            this.$scope.userProfile.selectedSchools = this.institution
                        })*/

                }
            })
    }

    handleAreaChange() {
        // if (!this.$scope.userProfile.selectedSchools) {
            this.$scope.userProfile.selectedSchools = [{}]
            this.serverCallService.makeGet('rest/ehisInstitution/institutions/?area=' + this.$scope.userProfile.area)
                .then(response => {
                    this.$scope.institutions = response.data
                })
        // }
    }

    reset(parentTaxon) {
        this.taxon = parentTaxon;
    };

    isBasicOrSecondaryEducation() {
        if (this.taxonPath.educationalContext) {
            const { name } = this.taxonPath.educationalContext
            return name === 'PRESCHOOLEDUCATION' || name === 'SECONDARYEDUCATION'
        }
    }
    selectInstitution(institution) {
        this.institution = institution
    }
    buildDomainSubjects(edCtxs) {
        for (let i = 0; i < edCtxs.length; i++) {
            edCtxs[i].name === 'BASICEDUCATION'
                ? edCtxs.basicEducationDomainSubjects = this.getDomainsAndSubjects(edCtxs[i])
                : edCtxs[i].name === 'SECONDARYEDUCATION' && (
                    edCtxs.secondaryEducationDomainSubjects = this.getDomainsAndSubjects(edCtxs[i])
                )
        }
        return edCtxs
    }
    getDomainsAndSubjects({ domains }) {
        const results = []
        domains = this.sortTaxonAlphabetically('DOMAIN_', domains)

        // for every Domain add it to the list and its children.
        for (let j = 0; j < domains.length; j++) {
            results.push(domains[j])
            ;[].push.apply(
                results,
                this.sortTaxonAlphabetically('SUBJECT_', domains[j].subjects)
            )
        }

        return results
    }
    buildInstitutionPath() {
        this.taxonPath = {}

        if (this.institution) {
            this.taxonPath.educationalContext = this.taxonService.getEducationalContext(this.taxon)
            this.taxonPath.domain = this.taxonService.getDomain(this.taxon)
            this.taxonPath.subject = this.taxonService.getSubject(this.taxon)

            if (this.taxonPath.subject) {
                this.taxonPath.domainSubject = this.taxonPath.subject
            } else if (this.taxonPath.domain){
                this.taxonPath.domainSubject = this.taxonPath.domain
            }

            this.taxonPath.specialization = this.taxonService.getSpecialization(this.taxon)
            this.taxonPath.module = this.taxonService.getModule(this.taxon)
            this.taxonPath.topic = this.taxonService.getTopic(this.taxon)
            this.taxonPath.subtopic = this.taxonService.getSubtopic(this.taxon)
        }
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
        taxon: '=',
        disableEducationalContext: '=',
        isDomainRequired: '=',
        touched: '=',
        isSearch: '=?',
        markRequired: '=',
        isStatisticsSelect: '<',
        isProfileSelect: '<',
        onStatisticsMultiselect: '&',
        clearSelection: '<'
    },
    templateUrl: 'directives/institutionSelect/institutionSelector.html',
    controller
})
}
