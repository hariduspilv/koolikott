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

            setSuggest: function (query) {
                suggest.query = query;
            },

            getSuggestQueryURL: function () {
                var suggestURL = "?q=";
                if (suggest.query){
                    suggestURL += suggest.query;
                }

                return suggestURL;
            },

            getSuggestURLbase: function() {
                return suggestURLbase;
            },

            getURL: function() {
                return this.getSuggestURLbase() + this.getSuggestQueryURL();
            }
        }

    })


});
/*

What i need:

 md-item-text="item.Title" -> Suggestion.term
 md-items="item in ctrl.data" -> suggestion in suggest.data
 md-search-text-change="ctrl.querySearch(ctrl.searchText)" -> suggest.doSuggest()
 md-search-text="ctrl.searchText" -> suggest.searchText
 md-selected-item="ctrl.selectedItem"  -> do we really need this?
 md-no-cache="ctrl.noCache"
 md-input-maxlength="30" ->
 md-input-minlength="2" -> minimum number of chars needed to initiate the suggestion requests
 md-input-name="autocompleteField"

*/

