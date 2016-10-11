define(['angularAMD'], function (angularAMD) {
    angularAMD.factory('embedService', ['$http', function ($http) {
        const noEmbedUrl = "https://noembed.com/embed?url=";
        var callback;

        return {
            getEmbed: function (link, cb) {
                callback = cb;
                $http.get(noEmbedUrl + link).then(callback);
            }
        };
    }]);
});
