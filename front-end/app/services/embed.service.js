'use strict';

angular.module('koolikottApp')
.factory('embedService',
[
    '$http', function ($http) {
        const noEmbedUrl = "https://noembed.com/embed?url=";
        return {
            getEmbed: function (link, callback) {
                if(link) {
                    link = link.toLowerCase();
                    $http.get(noEmbedUrl + link).then(callback).catch(function() {
                        // Catch the exception, material is not embeddable
                    })
                }
            }
        };
    }
]);
