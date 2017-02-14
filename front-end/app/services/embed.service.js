'use strict';

angular.module('koolikottApp')
.factory('embedService',
[
    '$http', function ($http) {
        const noEmbedUrl = "https://noembed.com/embed?url=";
        var callback;

        return {
            getEmbed: function (link, cb) {
                if(link) {
                    callback = cb;
                    link = link.toLowerCase();
                    $http.get(noEmbedUrl + link).then(callback).catch(function() {
                        // Catch the exception, material is not embeddable
                    })
                }
            }
        };
    }
]);
