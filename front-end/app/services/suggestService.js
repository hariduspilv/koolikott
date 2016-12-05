/**
 * Created by joonas on 17.08.16.
 */


define(['angularAMD'], function (angularAMD) {

    angularAMD.factory('suggestService', ['$http', function ($http) {

        var suggestURLbase = "rest/suggest/";
        var suggestSystemTagURLbase = "rest/suggest/tag";
        var suggest = {
            query: '',
            data: '',
            selectedItem: '',
            searchText: ''
        };

        return {
            getSuggestURLbase: function() {
                return suggestURLbase;
            },

            getSuggestSystemTagURLbase: function() {
                return suggestSystemTagURLbase;
            },

            getURL: function(query) {
                return this.getSuggestURLbase() + "?q=" + query;
            },

            getTagURL: function(query) {
                return this.getSuggestSystemTagURLbase() + "?q=" + query;
            },

            suggestTags: function(query) {
                if (query == null) {
                    return [];
                }

                return $http
                    .get(this.getTagURL(query), {cache: true})
                    .then(function (response) {
                        return response.data.alternatives || [];
                    });
            }
        }

    }])


});

