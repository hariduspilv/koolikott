'use strict'

angular.module('koolikottApp').directive('dopTaxonSelector', function () {
    return {
        scope: {
            taxon: '=',
            disableEducationalContext: '=',
            isDomainRequired: '=',
            touched: '=',
            isSearch: '=?',
            markRequired: '='
        },
        bindToController: true,
        controllerAs: 'ctrl',
        templateUrl: 'directives/taxonSelector/taxonSelector.html',
        controller: ['$scope', 'serverCallService', '$rootScope', '$timeout', 'metadataService', '$filter', 'taxonService',
            function ($scope, serverCallService, $rootScope, $timeout, metadataService, $filter, taxonService) {
                let EDUCATIONAL_CONTEXTS;
                let ctrl = this;

                // get educational contexts
                if (!EDUCATIONAL_CONTEXTS) {
                    metadataService.loadEducationalContexts(getEducationalContextsSuccess);
                } else {
                    init();
                }

                function init() {
                    setEducationalContexts();
                    buildTaxonPath();
                    $timeout(addTaxonPathListeners());

                    ctrl.basicEducationDomainSubjects = EDUCATIONAL_CONTEXTS.basicEducationDomainSubjects;
                    ctrl.secondaryEducationDomainSubjects = EDUCATIONAL_CONTEXTS.secondaryEducationDomainSubjects;
                    $timeout(function () {
                        ctrl.isReady = true;
                    });
                }

                ctrl.reset = function (parentTaxon) {
                    ctrl.taxon = parentTaxon;
                };

                ctrl.selectEducationalContext = function (educationalContext) {
                    ctrl.selectTaxon(educationalContext);
                    if (ctrl.touched) {
                        ctrl.touched.trigger = true;
                    }

                    $rootScope.dontCloseSearch = true;
                    $timeout(function () {
                        $rootScope.dontCloseSearch = false;
                    }, 500);
                };

                ctrl.getTopics = function () {
                    if (!ctrl.taxonPath) return;
                    var path = ctrl.taxonPath;

                    if (path.subject && path.subject.topics && path.subject.topics.length > 0) return path.subject.topics;
                    if (path.domain && path.domain.topics && path.domain.topics.length > 0) return path.domain.topics;
                    if (path.module && path.module.topics && path.module.topics.length > 0) return path.module.topics;
                };

                ctrl.isBasicOrSecondaryEducation = function () {
                    if (ctrl.taxonPath.educationalContext) {
                        return ctrl.taxonPath.educationalContext.name === 'BASICEDUCATION'
                            || ctrl.taxonPath.educationalContext.name === 'SECONDARYEDUCATION';
                    } else {
                        return false;
                    }

                };

                ctrl.translateTaxon = function (taxon) {
                    return $filter('translate')(taxon.level.toUpperCase().substr(1) + "_" + taxon.name.toUpperCase());
                };

                $scope.$on('taxonSelector:clear', function (e, value) {
                    ctrl.taxon = value;
                });

                $scope.$on('detailedSearch:prefillTaxon', (e, taxon) => {
                   ctrl.taxon = taxon;
                   buildTaxonPath();
                });

                ctrl.selectTaxon = function (taxon) {
                    ctrl.taxon = taxon;
                };

                function addTaxonPathListeners() {
                    //Triggers on taxon reset
                    $scope.$watch(function () {
                        if (ctrl.taxon) return ctrl.taxon;
                    }, function (newTaxon, oldTaxon) {
                        if (oldTaxon && newTaxon !== oldTaxon) {
                            buildTaxonPath();
                        }
                    }, false);

                    $scope.$watch('ctrl.markRequired', function (markRequired) {
                        if (markRequired) {
                            $scope.taxonForm.educationalContext.$touched = true;
                            $scope.taxonForm.domain.$touched = true;
                            $scope.taxonForm.domainAndSubject.$touched = true;
                            $scope.taxonForm.secondaryEducationDomainAndSubject.$touched = true
                        }
                    }, true);
                }

                function getEducationalContextsSuccess(educationalContexts) {
                    if (!isEmpty(educationalContexts)) {
                        EDUCATIONAL_CONTEXTS = educationalContexts;
                        buildDomainSubjects(educationalContexts);
                        init();
                    }
                }

                function buildDomainSubjects(educationalContexts) {
                    for (var i = 0; i < educationalContexts.length; i++) {
                        if (educationalContexts[i].name === 'BASICEDUCATION') {
                            EDUCATIONAL_CONTEXTS.basicEducationDomainSubjects = getDomainsAndSubjects(educationalContexts[i]);
                        } else if (educationalContexts[i].name === 'SECONDARYEDUCATION') {
                            EDUCATIONAL_CONTEXTS.secondaryEducationDomainSubjects = getDomainsAndSubjects(educationalContexts[i]);
                        }
                    }
                }

                function getDomainsAndSubjects(educationalContext) {
                    var results = [];
                    //sort Domains alphabetically
                    var domains = sortTaxonAlphabetically("DOMAIN_", educationalContext.domains);

                    // for every Domain add it to the list and its children.
                    for (var j = 0; j < domains.length; j++) {
                        var domain = domains[j];
                        results.push(domain);
                        //Sort subjects
                        var subjects = sortTaxonAlphabetically("SUBJECT_", domain.subjects);

                        // Merge the second array into the first one
                        Array.prototype.push.apply(results, subjects);
                    }

                    return results;
                }

                function sortTaxonAlphabetically(type, taxons) {
                    return taxons.sort(function (a, b) {
                        if ($filter('translate')(type + a.name) < $filter('translate')(type + b.name)) return -1;
                        if ($filter('translate')(type + a.name) > $filter('translate')(type + b.name)) return 1;
                        return 0;
                    });
                }

                function setEducationalContexts() {
                    ctrl.educationalContexts = EDUCATIONAL_CONTEXTS;
                    ctrl.educationalContexts.sort(function (context1, context2) {
                        return context1.id - context2.id;
                    });
                }

                function buildTaxonPath() {
                    ctrl.taxonPath = {};
                    if (ctrl.taxon) {
                        ctrl.taxonPath.educationalContext = taxonService.getEducationalContext(ctrl.taxon);
                        ctrl.taxonPath.domain = taxonService.getDomain(ctrl.taxon);
                        ctrl.taxonPath.subject = taxonService.getSubject(ctrl.taxon);

                        if (ctrl.taxonPath.subject) {
                            ctrl.taxonPath.domainSubject = ctrl.taxonPath.subject;
                        } else if (ctrl.taxonPath.domain) {
                            ctrl.taxonPath.domainSubject = ctrl.taxonPath.domain;
                        }

                        ctrl.taxonPath.specialization = taxonService.getSpecialization(ctrl.taxon);
                        ctrl.taxonPath.module = taxonService.getModule(ctrl.taxon);
                        ctrl.taxonPath.topic = taxonService.getTopic(ctrl.taxon);
                        ctrl.taxonPath.subtopic = taxonService.getSubtopic(ctrl.taxon);
                    }
                }
            }]
    };
});
