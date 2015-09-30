define(['app'], function(app)
{
    app.directive('vaInteger', [
        function() {
            return {
              require: '?ngModel',
              scope: {
                "inputPattern": '@'
              },
              link: function(scope, element, attrs, ngModelCtrl) {

                var regexp = null;

                if (scope.inputPattern !== undefined) {
                  regexp = new RegExp(scope.inputPattern, "g");
                }

                if(!ngModelCtrl) {
                  return;
                }

                ngModelCtrl.$parsers.push(function(val) {
                  if (regexp) {
                    var clean = val.replace(regexp, '');
                    if (val !== clean) {
                      ngModelCtrl.$setViewValue(clean);
                      ngModelCtrl.$render();
                    }
                    return clean;
                  }
                  else {
                    return val;
                  }

                });

                element.bind('keypress', function(event) {
                    if (event.keyCode === 32 || event.charCode === 32) {
                        return false;
                    }
                });
              }
            }
        }
    ]);
});