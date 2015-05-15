'use strict';

angular.module('dop', [
  'ngRoute',
  'dop.home'
]).
config(['$routeProvider', function($routeProvider) {
  $routeProvider.otherwise({redirectTo: '/'});
}]);