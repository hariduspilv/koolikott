/**
 * https://github.com/alexander-elgin/ta-maxlength
 */
angular.module('koolikottApp')
    .directive('remainingCharCount', [function () {
    return {
        restrict: 'A',
        require: 'ngModel',
        link: function ($scope, $element, $attrs, $ngModel) {

            $scope.$watch(function () {
                return $ngModel.$viewValue;
            }, (newVal, oldVal) => {
                if (newVal) {
                    let diff = $attrs.remainingCharCount - stripHtml(newVal).length;
                    $scope.charactersRemaining = diff >= 0 ? diff : 0;
                    $ngModel.$setValidity('charLimit', diff > 10);
                }
            });
        }
    }
}]);
