'use strict'

{
    const STATISTICS_STATE_MAP = {
        expertStatistics: [
            'EXPERT_STATISTICS', // title translation key
            'expertStatistics', // rest URI (after 'rest/admin/')
        ],
    };

    class controller extends Controller {
        constructor(...args) {
            super(...args);


            this.viewPath = this.$location.path().replace(/^\/dashboard\//, '');
            const [titleTranslationKey] = STATISTICS_STATE_MAP[this.viewPath] || [];

            this.$scope.titleTranslationKey = titleTranslationKey;

        }
    }

    controller.$inject = [
        '$scope',
        '$location',
    ];
    angular.module('koolikottApp').controller('statisticsController', controller)
}
