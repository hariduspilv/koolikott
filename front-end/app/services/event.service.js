angular.module('koolikottApp')
    .service('eventService', ['$rootScope', EventService]);

function EventService($rootScope) {

    function subscribe($scope, eventName, callback) {
        let deregistrator = $rootScope.$on(eventName, callback);
        $scope.$on('$destroy', deregistrator);
    }

    function notify(eventName) {
        $rootScope.$emit(eventName);
    }

    return {
        subscribe: subscribe,
        notify: notify
    }
}
