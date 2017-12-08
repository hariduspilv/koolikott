angular.module('koolikottApp')
    .service('tagsService', ['serverCallService', 'searchService', '$location', '$mdDialog', '$translate', TagsService]);

function TagsService(serverCallService, searchService, $location, $mdDialog, $translate) {

    return {
        searchByTag(tag) {
            searchService.clearFieldsNotInSimpleSearch();
            searchService.setSearch('tag:"' + tag + '"');
            $location.url(searchService.getURL());
        },

        reportTag(learningObject, targetEvent) {
            return $mdDialog
                .show({
                    controller: ['$scope', '$mdDialog', function ($scope, $mdDialog) {
                        $scope.title = $translate.instant('TAG_TOOLTIP_REPORT_AS_IMPROPER')
                        $scope.data = {
                            reportingText: ''
                        }
                        $scope.cancel = () => {
                            $scope.data.reportingText = ''
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
                    clickOutsideToClose: true,
                    escapeToClose: true,
                    targetEvent,
                })
                .then(({ data, reasons }) =>
                    serverCallService
                        .makePut('rest/impropers', Object.assign(data, {
                            learningObject,
                            reportingReasons: reasons.reduce((reportingReasons, r) =>
                                r.checked
                                    ? reportingReasons.concat({ reason: r.key })
                                    : reportingReasons,
                                []
                            )
                        }))
                        .then(response => response.data)
                )
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
            if (successCallback) {
                serverCallService.makePost('rest/tagUpVotes/delete', params.tagUpVote, successCallback, failCallback);
            } else {
                return serverCallService.makePost('rest/tagUpVotes/delete', params.tagUpVote)
                    .then(response => {
                        return response.data;
                    });
            }
        },
    }
}
