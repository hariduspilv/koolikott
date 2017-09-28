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
            let confirm = $mdDialog.confirm()
                .title($translate.instant('REPORT_IMPROPER_TITLE'))
                .content($translate.instant('REPORT_IMPROPER_CONTENT') + " " + $translate.instant('REASON_IMPROPER_TAG'))
                .ok($translate.instant('BUTTON_NOTIFY'))
                .cancel($translate.instant('BUTTON_CANCEL'));

            $mdDialog.show(confirm).then(function () {
                let entity = {
                    learningObject: learningObject,
                    reason: "Tag: " + tag
                };

                if (successCallback) {
                    serverCallService.makePut("rest/admin/improper", entity, successCallback, failCallback);
                } else {
                    return serverCallService.makePut("rest/admin/improper", entity)
                        .then(response => {
                            return response.data;
                        });
                }
            });
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

        addUpVode(params, successCallback, failCallback) {
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
                serverCallService.makeGet("rest/admin/improper", params, successCallback, failCallback);
            } else {
                return serverCallService.makeGet("rest/admin/improper", params)
                    .then(response => {
                        return response.data;
                    });
            }
        }
    }
}
