/**
 * Created by joonas on 17.08.16.
 */


define(['angularAMD'], function (angularAMD) {

    angularAMD.factory('suggestService', function () {

        var suggestURLbase = "rest/suggest/";
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

            getURL: function(query) {
                return this.getSuggestURLbase() + "?q=" + query;
            }
        }

    })


});

