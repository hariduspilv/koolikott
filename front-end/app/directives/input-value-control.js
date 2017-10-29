'use strict'

directive('dopInputValueControl', {
    require: '?ngModel',
    scope: {
        inputPattern: '@'
    },
    link($scope, $element, $attr, $ctrl) {
        if ($ctrl) {
            $ctrl.$parsers.push(value => {
                if (typeof $scope.inputPattern === 'undefined')
                    return value

                const clean = value.replace(new RegExp($scope.inputPattern, 'g'), '')
                
                if (value !== clean) {
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
})
