'use strict'

{
class controller extends Controller {
    $onInit() {
        this.metadataService.loadEducationalContexts(educationalContexts => {
            if (educationalContexts) {
                this.educationalContexts = this.buildDomainSubjects(educationalContexts)
                this.educationalContexts.sort((c1, c2) =>  c1.id - c2.id)
                this.buildTaxonPath()
            }
        })
        this.$scope.$on('taxonSelector:clear', (evt, value) => this.taxon = value)
        this.$scope.$on('detailedSearch:prefillTaxon', (evt, taxon) => {
           this.taxon = taxon
           this.buildTaxonPath()
        })
        this.translateTaxon = this.translateTaxon.bind(this)
        this._previousTaxon = this.taxon
        this._previousMarkRequired = this.markRequired

        if (this.isStatisticsSelect && typeof this.onStatisticsMultiselect === 'function') {
            const onMultiSelect = (taxons) => taxons && this.onStatisticsMultiselect({ taxons })
            this.$scope.$watch(() => this.taxonPath && this.taxonPath.domain, onMultiSelect, true)
            this.$scope.$watch(() => this.taxonPath && this.taxonPath.domainSubject, onMultiSelect, true)
        }
    }

    $onChanges(changes) {
        if (changes.clearSelection.currentValue)
            this.taxon = {}
    }
    $doCheck() {
        const taxonChanged =
            (this.taxon && !this._previousTaxon) ||
            (!this.taxon && this._previousTaxon) ||
            !_.isEqual(this.taxon, this._previousTaxon)

        if (taxonChanged)
            this.buildTaxonPath()

        if (taxonChanged)
            this._previousTaxon = this.taxon

        if (this.markRequired != this._previousMarkRequired) {
            if (this.markRequired) {
                this.$scope.taxonForm.educationalContext.$touched = true
                this.$scope.taxonForm.domain.$touched = true
                this.$scope.taxonForm.domainAndSubject.$touched = true
                this.$scope.taxonForm.secondaryEducationDomainAndSubject.$touched = true
            }
            this._previousMarkRequired = this.markRequired
        }
    }
    selectEducationalContext(educationalContext) {
        this.selectTaxon(educationalContext)

        if (this.touched)
            this.touched.trigger = true

        this.$rootScope.dontCloseSearch = true
        this.$timeout(() => this.$rootScope.dontCloseSearch = false, 500)
    }
    getTopics() {
        if (this.taxonPath) {
            const path = this.taxonPath

            return path.subject && path.subject.topics && path.subject.topics.length > 0
                ? path.subject.topics
                : path.domain && path.domain.topics && path.domain.topics.length > 0
                    ? path.domain.topics
                    : path.module && path.module.topics && path.module.topics.length > 0
                        ? path.module.topics
                        : null
        }
    }
    reset(parentTaxon) {
        this.taxon = parentTaxon;
    };

    isPreschoolorHobbyactivity() {
        if (this.taxonPath.educationalContext) {
            const { name } = this.taxonPath.educationalContext
            return name === 'PRESCHOOLEDUCATION' || name === 'NONFORMALEDUCATION'
        }

    }
    isBasicOrSecondaryEducation() {
        if (this.taxonPath.educationalContext) {
            const { name } = this.taxonPath.educationalContext
            return name === 'PRESCHOOLEDUCATION' || name === 'SECONDARYEDUCATION'
        }
    }
    translateTaxon(taxon) {
        return this.$filter('translate')(taxon.level.toUpperCase().substr(1) + '_' + taxon.name.toUpperCase())
    }
    selectTaxon(taxon) {
        this.taxon = taxon
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
    sortTaxonAlphabetically(type, taxons) {
        const t = ({ name }) => this.$filter('translate')(type + name)

        return taxons.sort((a, b) =>
            t(a) < t(b)
                ? -1
                : t(a) > t(b)
                    ? 1
                    : 0
        )
    }
    buildTaxonPath() {
        this.taxonPath = {}

        if (this.taxon) {
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
    'taxonService'
]
component('dopTaxonSelector', {
    bindings: {
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
    templateUrl: 'directives/taxonSelector/taxonSelector.html',
    controller
})
}
