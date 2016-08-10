define([
    'angularAMD',
    'services/serverCallService'
], function (angularAMD) {
    var EDUCATIONAL_CONTEXTS;

    angularAMD.directive('dopTaxonSelector', ['serverCallService', 'metadataService', function () {
        return {
            scope: {
                taxon: '=',
                disableEducationalContext: '=',
                isAddPortfolioView: '=',
                isAddMaterialView: '=',
                touched: '='
            },
            bindToController: true,
            controller: function ($scope, serverCallService, $rootScope, $timeout, metadataService, $filter) {
                var self = this;
                // get educational contexts
                if (!EDUCATIONAL_CONTEXTS) {
                    metadataService.loadEducationalContexts(getEducationalContextsSuccess)
                } else {
                    init();
                }

                function init() {
                    setEducationalContexts();
                    buildTaxonPath();
                    addTaxonPathListeners();

                    self.basicEducationDomainSubjects = EDUCATIONAL_CONTEXTS.basicEducationDomainSubjects;
                    self.secondaryEducationDomainSubjects = EDUCATIONAL_CONTEXTS.secondaryEducationDomainSubjects;
                    $timeout(function () {
                        self.isReady = true;
                    });
                    self.topicRequired = self.isAddMaterialView ? isTopicNotSet() : false;
                }

                function isTopicNotSet() {
                    return !$rootScope.selectedTopics || $rootScope.selectedTopics.length === 0;
                }

                function removeTopic(originalId) {
                    if ($rootScope.selectedTopics) {
                        $rootScope.selectedTopics = $rootScope.selectedTopics
                            .filter(function (topic) {
                                return topic.id !== originalId;
                            });
                    }
                }

                self.reset = function (parentTaxon, original) {
                    self.taxon = parentTaxon;
                    if (original) {
                        removeTopic(original.topic.id);
                    }
                };

                self.selectEducationalContext = function () {
                    if(self.touched) {
                        self.touched.trigger = true;
                    }

                    $rootScope.dontCloseSearch = true;
                    $timeout(function () {
                        $rootScope.dontCloseSearch = false;
                    }, 500);
                };

                self.getTopics = function () {
                    if (!self.taxonPath) return;
                    var path = self.taxonPath;

                    if (path.subject && path.subject.topics && path.subject.topics.length > 0) return path.subject.topics;
                    if (path.domain && path.domain.topics && path.domain.topics.length > 0) return path.domain.topics;
                    if (path.module && path.module.topics && path.module.topics.length > 0) return path.module.topics;
                };

                self.isBasicOrSecondaryEducation = function () {
                    if (self.taxonPath.educationalContext) {
                        return self.taxonPath.educationalContext.name === 'BASICEDUCATION'
                            || self.taxonPath.educationalContext.name === 'SECONDARYEDUCATION';
                    } else {
                        return false;
                    }

                };

                self.translateTaxon = function (taxon) {
                    return $filter('translate')(taxon.level.toUpperCase().substr(1) + "_" + taxon.name.toUpperCase());
                };

                function addTaxonPathListeners() {
                    /*
                     * The order of the watchers is important and should be the same as the tree.
                     */

                    //Triggers on taxon reset
                    $scope.$watch(function () {
                        if (self.taxon) return [self.taxon.id, self.taxon.level];
                    }, function (newTaxon, oldTaxon) {
                        if (newTaxon && oldTaxon && newTaxon[0] !== oldTaxon[0]) {
                            buildTaxonPath();

                            //When choosing parent taxon or setting to null, old topic needs to be removed
                            if (newTaxon[1] !== $rootScope.taxonUtils.constants.SUBTOPIC || !newTaxon[0]) {
                                removeTopic(oldTaxon[0]);
                            }

                            if (!self.topicRequired && !self.taxonPath.topic && isTopicNotSet() && self.isAddMaterialView) {
                                self.topicRequired = true;
                            }
                        }
                    }, true);

                    //EducationalContext
                    $scope.$watch(function () {
                            if (self.taxonPath && self.taxonPath.educationalContext)
                                return self.taxonPath.educationalContext.id;
                        }
                        , function (newEducationalContext, oldEducationalContext) {
                            if (newEducationalContext !== undefined && newEducationalContext !== oldEducationalContext) {
                                self.taxon = self.taxonPath.educationalContext;
                            }
                        }, true);

                    //Domain
                    $scope.$watch(function () {
                            if (self.taxonPath && self.taxonPath.domain)
                                return self.taxonPath.domain.id;
                        }
                        , function (newDomain, oldDomain) {
                            if (newDomain !== undefined && newDomain !== oldDomain) {
                                self.taxon = self.taxonPath.domain;
                            }
                        }, true);

                    //subject
                    $scope.$watch(function () {
                        if (self.taxonPath && self.taxonPath.subject)
                            return self.taxonPath.subject.id;
                    }, function (newSubject, oldSubject) {
                        if (newSubject !== undefined && newSubject !== oldSubject) {
                            self.taxon = self.taxonPath.subject;
                        }
                    }, true);

                    //domainSubject
                    $scope.$watch(function () {
                        if (self.taxonPath && self.taxonPath.domainSubject)
                            return self.taxonPath.domainSubject.id;
                    }, function (newDomainSubject, oldDomainSubject) {
                        if (newDomainSubject !== undefined && newDomainSubject !== oldDomainSubject) {
                            self.taxon = self.taxonPath.domainSubject;
                        }
                    }, true);

                    // specialization
                    $scope.$watch(function () {
                        if (self.taxonPath && self.taxonPath.specialization)
                            return self.taxonPath.specialization.id;
                    }, function (newSpecialization, oldSpecialization) {
                        if (newSpecialization !== undefined && newSpecialization !== oldSpecialization) {

                            self.taxon = self.taxonPath.specialization;
                        }
                    }, true);

                    // module
                    $scope.$watch(function () {
                        if (self.taxonPath && self.taxonPath.module)
                            return self.taxonPath.module.id;
                    }, function (newModule, oldModule) {
                        if (newModule !== undefined && newModule !== oldModule) {
                            self.taxon = self.taxonPath.module;
                        }
                    }, true);

                    // topic
                    $scope.$watch(function () {
                        if (self.taxonPath && self.taxonPath.topic)
                            return self.taxonPath.topic.id;
                    }, function (newTopic, oldTopic) {
                        if (newTopic !== undefined && newTopic !== oldTopic) {
                            self.taxon = self.taxonPath.topic;
                            if ($rootScope.selectedTopics && !containsObjectWithId($rootScope.selectedTopics, self.taxonPath.topic.id)) {
                                $rootScope.selectedTopics.push(self.taxonPath.topic);
                            }
                        }
                    }, true);

                    // subtopic
                    $scope.$watch(function () {
                        if (self.taxonPath && self.taxonPath.subtopic)
                            return self.taxonPath.subtopic.id;
                    }, function (newSubtopic, oldSubtopic) {
                        if (newSubtopic !== undefined && newSubtopic !== oldSubtopic) {
                            self.taxon = self.taxonPath.subtopic;
                            var topic = $rootScope.taxonUtils.getTopic(self.taxon);

                            if ($rootScope.selectedTopics && !containsObjectWithId($rootScope.selectedTopics, topic.id)) {
                                $rootScope.selectedTopics.push(topic);
                            }
                        }
                    }, true);
                }

                function containsObjectWithId(array, id) {
                    var res = false;
                    array.forEach(function (element) {
                        if (element.id === id) res = true;
                    });

                    return res;
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
                    self.educationalContexts = EDUCATIONAL_CONTEXTS;
                    self.educationalContexts.sort(function (context1, context2) {
                        return context1.id - context2.id;
                    });
                }

                function buildTaxonPath() {
                    self.taxonPath = {};
                    self.taxonPath.educationalContext = $rootScope.taxonUtils.getEducationalContext(self.taxon);
                    self.taxonPath.domain = $rootScope.taxonUtils.getDomain(self.taxon);
                    self.taxonPath.subject = $rootScope.taxonUtils.getSubject(self.taxon);

                    if (self.taxonPath.subject) {
                        self.taxonPath.domainSubject = self.taxonPath.subject;
                    } else {
                        self.taxonPath.domainSubject = self.taxonPath.domain;
                    }

                    self.taxonPath.specialization = $rootScope.taxonUtils.getSpecialization(self.taxon);
                    self.taxonPath.module = $rootScope.taxonUtils.getModule(self.taxon);
                    self.taxonPath.topic = $rootScope.taxonUtils.getTopic(self.taxon);
                    self.taxonPath.subtopic = $rootScope.taxonUtils.getSubtopic(self.taxon);
                }
            },
            controllerAs: 'ctrl',
            templateUrl: 'directives/taxonSelector/taxonSelector.html'

        };
    }]);
});
