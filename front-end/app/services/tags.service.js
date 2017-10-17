angular.module('koolikottApp')
    .service('tagsService', ['serverCallService', 'searchService', '$location', '$mdDialog', '$translate', TagsService]);

function TagsService(serverCallService, searchService, $location, $mdDialog, $translate) {

    return {
        searchByTag(tag) {
            searchService.clearFieldsNotInSimpleSearch();
            searchService.setSearch('tag:"' + tag + '"');
            $location.url(searchService.getURL());
        },

        addTag(tag, learningObject, successCallback, failCallback) {
            let url = "rest/learningObject/" + learningObject.id + "/tags";

            if (successCallback) {
                serverCallService.makePut(url, JSON.stringify(tag.tagName), successCallback, failCallback);
            } else {
                return serverCallService.makePut(url, JSON.stringify(tag.tagName))
                    .then(response => {
                        return response.data;
                    });
            }
        },

        reportTag(tag, learningObject, successCallback, failCallback) {
            return $mdDialog
                .show({
                    controller: ['$scope', '$mdDialog', function ($scope, $mdDialog) {
                        $scope.title = $translate.instant('TAG_TOOLTIP_REPORT_AS_IMPROPER')
                        $scope.data = {
                            reportingText: ''
                        }
                        $scope.cancel = () => {
                            $cope.data.reportingText = ''
                            $mdDialog.cancel()
                        }
                        $scope.sendReport = () => $mdDialog.hide($scope)
                        $scope.loading = true
                        $scope.submitEnabled = true

                        serverCallService
                            .makeGet('rest/learningMaterialMetadata/tagReportingReasons')
                            .then(({ data: reasons }) => {
                                if (Array.isArray(reasons)) {
                                    $scope.hideReasons = reasons.length === 1
                                    $scope.reasons = reasons.map(key => ({
                                        key,
                                        checked: reasons.length === 1
                                    }))
                                }
                                $scope.loading = false
                            })

                        $scope.characters = { used: 0, remaining: 255 }
                        $scope.$watch('data.reportingText', (newValue) => {
                            const used = newValue ? newValue.length : 0
                            $scope.characters = { used, remaining: 255 - used }
                        })
                    }],
                    templateUrl: 'directives/report/improper/improper.dialog.html',
                    clickOutsideToClose:true
                })
                .then(({ data, reasons }) => {
                    Object.assign(data, {
                        learningObject,
                        reportingReasons: reasons.reduce((reportingReasons, r) =>
                            r.checked
                                ? reportingReasons.concat({ reason: r.key })
                                : reportingReasons,
                            []
                        )
                    })
                    return successCallback
                        ? serverCallService.makePut('rest/impropers', data, successCallback, failCallback)
                        : serverCallService.makePut('rest/impropers', data).then(response => response.data)
                })
        },

        getTagUpVotes(params, successCallback, failCallback) {
            if (successCallback) {
                serverCallService.makeGet("rest/tagUpVotes/report", params, successCallback, failCallback);
            } else {
                return serverCallService.makeGet("rest/tagUpVotes/report", params)
                    .then(response => {
                       return response.data;
                    });
            }
        },

        addUpVote(params, successCallback, failCallback) {
            if (successCallback) {
                serverCallService.makePut("rest/tagUpVotes", params, successCallback, failCallback);
            } else {
                return serverCallService.makePut("rest/tagUpVotes", params)
                    .then(response => {
                        return response.data;
                    });
            }
        },

        removeUpVote(params, successCallback, failCallback) {
            let removeUpVoteUrl = "rest/tagUpVotes/" + params.tagUpVote.id;

            if (successCallback) {
                serverCallService.makeDelete(removeUpVoteUrl, params, successCallback, failCallback);
            } else {
                return serverCallService.makeDelete(removeUpVoteUrl, params)
                    .then(response => {
                        return response.data;
                    });
            }
        },

        addSystemTag(learningObjectId, params, successCallback, failCallback) {
            if (successCallback) {
                serverCallService.makeGet("rest/learningObject/" + learningObjectId + "/system_tags", params, successCallback, failCallback);
            } else {
                return serverCallService.makeGet("rest/learningObject/" + learningObjectId + "/system_tags", params)
                    .then(response => {
                        return response.data;
                    });
            }
        },

        getImpropers(params, successCallback, failCallback) {
            if (successCallback) {
                serverCallService.makeGet("rest/impropers", params, successCallback, failCallback);
            } else {
                return serverCallService.makeGet("rest/impropers", params)
                    .then(response => {
                        return response.data;
                    });
            }
        }
    }
}
