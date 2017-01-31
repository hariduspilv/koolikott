/**
 * https://github.com/alexander-elgin/ta-maxlength
 * with some modifications
 */
angular.module('koolikottApp')
    .directive('taMaxlength', ['$timeout', 'textAngularManager', function ($timeout, textAngularManager) {
    return {
        restrict: 'A',
        link: function ($scope, element, attrs) {
            let editor, maxLength = parseInt(attrs.taMaxlength);

            let getTruncatedContent = function(content) {
                return $.truncate(content, {
                    // maxLength - newlines
                    length: maxLength - countOccurrences('</p>', content) + 1,
                    ellipsis: ''
                });
            };

            let getEditor = function() {
                return editor.scope.displayElements.text[0];
            };

            let getContentLength = function() {
                return angular.element(getEditor()).text().length;
            };

            let isNavigationKey = function(keyCode) {
                return ((keyCode >= 33) && (keyCode <= 40)) || ([8, 46].indexOf(keyCode) !== -1);
            };

            let isCopying = function(event) {
                return event.ctrlKey && ([65, 67, 88].indexOf(event.keyCode) !== -1);
            };

            let getLength = function () {
                // text length + newlines
                return getContentLength() + countOccurrences('</p>', angular.element(getEditor())[0].innerHTML);
            };

            $scope.$watch(function() {
                let editorInstance = textAngularManager.retrieveEditor(attrs.name);

                if((editorInstance !== undefined) && (editor === undefined)) {
                    editor = editorInstance;

                    getEditor().addEventListener('keydown', function(e) {
                        if(!isNavigationKey(e.keyCode) && !isCopying(e) && (getLength() >= maxLength)) {
                            e.preventDefault();
                            return false;
                        }
                    });
                }

                return editorInstance === undefined ? '' : editor.scope.html;
            }, function(modifiedContent) {
                if(getLength() > maxLength) {
                    $timeout(function() {
                        editor.scope.html = getTruncatedContent(modifiedContent);
                    });
                }
            });
        }
    };
}]);
