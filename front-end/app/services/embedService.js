define(['angularAMD'], function (angularAMD) {
    angularAMD.factory('embedService', ['serverCallService', '$http', function (serverCallService, $http) {
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
