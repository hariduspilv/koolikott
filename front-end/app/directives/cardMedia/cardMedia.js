'use strict'

angular.module('koolikottApp')
.directive('dopCardMedia',
[
  function () {
    return {
      scope: {
        learningObject: '=',
        isAuthenticated: '='
      },
      templateUrl: 'directives/cardMedia/cardMedia.html',
      controller: function () {
        
      }
    }
  }

]);
