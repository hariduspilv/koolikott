'use strict'

angular.module('koolikottApp')
.directive('dopSlideshare',
[
    'serverCallService',
    function(serverCallService) {
        return {
            scope: {
                source: '=',
                width: '=',
                height: '=',
                failCallback: '&'
            },
            templateUrl: 'directives/slideshare/slideshare.html',
            controller: ['$scope', function($scope) {
                getSlideshareData();

                function getSlideshareData() {
                    $.ajax({
                        type: "GET",
                        url: 'https://www.slideshare.net/api/oembed/2?url=http://www.slideshare.net/lyndadotcom/code-drivesworld12&format=jsonp',
                        dataType: 'jsonp',
                        success: function (data) {
                            getSlideshareDataSuccess(data);
                        }
                    });
                }

                function getSlideshareDataSuccess(data) {
                    if (!isEmpty(data)) {
                        var embedCodeRegex = /(?:embed_code\/key\/)((\w|-)+)(?:\")/;
                        var embedCode = data.html.match(embedCodeRegex)[1];
                        $scope.embedLink = "https://www.slideshare.net/slideshow/embed_code/key/" + embedCode;
                    } else {
                        log("Slideshare data is empty. ");
                        $scope.failCallback();
                    }
                }

                // function getSlideshareDataFail(data) {
                //     log("Failed to get slideshare data. ");
                //     $scope.failCallback();
                // }
            }]
        };
    }
]);
