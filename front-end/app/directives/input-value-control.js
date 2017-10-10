'use strict'

angular.module('koolikottApp').directive('dopInputValueControl', () => ({
    require: '?ngModel',
    scope: {
        inputPattern: '@'
    },
    link($scope, $element, $attr, $ctrl) {
        if ($ctrl) {
            $ctrl.$parsers.push(val => {
                if (typeof $scope.inputPattern === 'undefined')
                    return val

                var clean = val.replace(new RegExp($scope.inputPattern, 'g'), '')
                
                if (val !== clean) {
                    $ctrl.$setViewValue(clean)
                    $ctrl.$render()
                }
                
                return clean
            })
            $element.bind('keypress', (evt) => {
                if (evt.keyCode === 32 || evt.charCode === 32)
                    evt.preventDefault()
            })
        }
    }
}))
