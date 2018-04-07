'use strict';
{
class controller extends Controller {

    getSuggestURLbase() {
        return "rest/suggest/";
    }

    getSuggestSystemTagURLbase() {
        return "rest/suggest/tag";
    }

    getURL(query) {
        return this.getSuggestURLbase() + "?q=" + query;
    }

    suggest(query, url) {
        if (query == null) {
            return [];
        }

        return this.serverCallService.makeGet(url, {q: query})
            .then(function(response) {
                return response.data;
            });
    }
}

controller.$inject = [
    'serverCallService',
]
factory('suggestService', controller)
}
