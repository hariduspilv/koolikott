/**
* Created by joonas on 17.08.16.
*/
'use strict';

angular.module('koolikottApp')
.factory('suggestService',
[
    '$http', 'serverCallService',
    function ($http, serverCallService) {

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

            suggest: function(query, url) {
                if (query == null) {
                    return [];
                }

                return serverCallService.makeGet(url, {q: query})
                .then(function(response) {
                    return response.data.alternatives || [];
                });
            }
        }

    }
]);
