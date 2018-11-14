'use strict'

angular.module('koolikottApp')
.controller('abstractStaticPageController',
[
    '$scope', "serverCallService", 'translationService', '$sce',
    function($scope, serverCallService, translationService, $sce) {
        function getPage(pageLanguage) {

            serverCallService.makeGet("rest/page", {
                'name': $scope.pageName,
                'language': pageLanguage
            }, getPageSuccess, getPageFail);
        }

        function getPageSuccess(data) {
            if (isEmpty(data)) {
                console.log('No data returned.');
            } else {
                $scope.pageContent = $sce.trustAsHtml(data.content);
            }
        }

        function getPageFail(data, status) {
            console.log('Getting page failed.')
        }

        $scope.$watch(function() {
            return translationService.getLanguage();
        }, function(language) {
            getPage(language);
        }, true);

        getPage(translationService.getLanguage());
    }
]);
