define(['app'], function (app) {  
    app.controller('baseImporperController', ['$scope', '$sce', '$templateRequest', '$compile', 'translationService', function ($scope, $sce, $templateRequest, $compile, translationService) {
        var collection = null;
       
        $scope.filter = {
            options: {
              debounce: 500
            }
        };

        $scope.query = {
            filter: '',
            order: 'bySubmittedAt',
        };

        function getImproperSuccess(improper) {
            if (isEmpty(improper)) {
                log('No data returned by session search.');
            } else {
                collection = improper;
                $scope.improper = improper;
                
                orderImporper($scope.query.order);
            }
        }
        
        function getImproperFail() {
            console.log('Session search failed.')
        }
        
        function orderImporper(order) {
            $scope.improper = $scope.improper.sort(function(a, b) {
                if (order === 'bySubmittedAt' || order === '-bySubmittedAt')
                    return new Date(b.added) - new Date(a.added);
                
                if (order === 'bySubmittedBy' || order == '-bySubmittedBy')
                    return (a.creator.name + ' ' + a.creator.surname).localeCompare(b.creator.name + ' ' + b.creator.surname);
                
                return 0;
            });

            if (order.slice(0, 1) === '-')
                $scope.improper.reverse();
        }
        
        function filterImproper() {
            $scope.improper = collection.filter(function(improper) {
                if (improper.portfolio !== null)
                  return improper.portfolio.title.slice(0, $scope.query.filter.length).toLowerCase() === $scope.query.filter.toLowerCase();
                if (improper.material !== null)
                  return $scope.getCorrectLanguageTitle(improper.material).slice(0, $scope.query.filter.length).toLowerCase() === $scope.query.filter.toLowerCase();
            });
        }
        
        function buildTable(tableId, templateUrl) {
            var url = $sce.getTrustedResourceUrl(templateUrl);
            var $container = angular.element(tableId).find('.table-container');
            
            $templateRequest(url).then(function(template) {
                $container.html($compile(template)($scope));
            });
        }
        
        $scope.getCorrectLanguageTitle = function (item) {
            if (item) {
                return getUserDefinedLanguageString(item.titles, translationService.getLanguage(), item.language);
            }
				};
        
        $scope.onReorder = function (order) {
            orderImporper(order);
        };
        
        $scope.removeFilter = function () {
            $scope.filter.show = false;
            $scope.query.filter = '';
            $scope.improper = collection;
            
            if ($scope.filter.form.$dirty) {
                $scope.filter.form.$setPristine();
            }
        };
        
        $scope.$watch('query.filter', function (newValue, oldValue) {
            if (newValue === oldValue) return;
            
            filterImproper();
        });
        
        $scope.formatDate = function(date) {
            return formatDateToDayMonthYear(date);
        }
        
        return {
            getImproperSuccess: getImproperSuccess,
            getImproperFail: getImproperFail,
            buildTable: buildTable
        }
    }]);
    
    app.controller('imporperMaterialsController', ['$scope', 'serverCallService', '$controller', '$filter', function ($scope, serverCallService, $controller, $filter) {    
        var base = $controller('baseImporperController', { $scope: $scope });
        
        serverCallService.makeGet("rest/material/getImproper", {}, base.getImproperSuccess, base.getImproperFail);

        $scope.title = $filter('translate')('DASHBOARD_IMRPOPER_MATERIALS');

        $scope.bindTable = function() {
            base.buildTable('#improper-materials-table', 'views/dashboard/templates/improperMaterialsTable.html');
        }
    }]);
    
    app.controller('imporperPortfoliosController', ['$scope', 'serverCallService', '$controller', '$filter', function ($scope, serverCallService, $controller, $filter) {    
        var base = $controller('baseImporperController', { $scope: $scope });
        
        serverCallService.makeGet("rest/portfolio/getImproper", {}, base.getImproperSuccess, base.getImproperFail);
        
        $scope.title = $filter('translate')('DASHBOARD_IMRPOPER_PORTFOLIOS');
        
        $scope.bindTable = function() {
            base.buildTable('#improper-portfolios-table', 'views/dashboard/templates/improperPortfoliosTable.html');
        }
    }]);
});
