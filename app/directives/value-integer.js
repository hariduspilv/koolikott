define(['app'], function(app)
{
    app.directive('vaInteger', [
        function() {
            return {
                require : '?ngModel',
                scope : {
                    integers : '='
                },
                link : function(scope, element, attrs, ngModelCtrl) {
                    if (!ngModelCtrl) {
                        return;
                    }

                    ngModelCtrl.$parsers.push(function(val) {
                        var clean = val.replace(/[^0-9]/g, '');
                        if (clean.length > scope.integers) {
                            clean = clean.slice(0, scope.integers);
                        }

                        if (val !== clean) {
                            ngModelCtrl.$setViewValue(clean);
                            ngModelCtrl.$render();
                        }

                        return clean;
                    });

                    element.bind('keypress', function(event) {
                        if (event.keyCode === 32 || event.charCode === 32) {
                            return false;
                        }
                    });
                }
            };
        }
    ]);
});