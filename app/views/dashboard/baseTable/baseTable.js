define([
    'app',
    'jquery',
    'services/translationService',
], function (app, $) {
    app.controller('baseTableController', ['$scope', '$location', '$sce', '$templateRequest', '$compile', 'translationService', function ($scope, $location, $sce, $templateRequest, $compile, translationService) {
        var collection = null;
        var filtredCollection = null;

        $scope.itemsCount = 0;

        $scope.filter = {
            options: {
                debounce: 500
            }
        };

        $scope.query = {
            filter: '',
            order: 'bySubmittedAt',
            limit: 5,
            page: 1
        };

        function getItemsSuccess(data) {
            if (isEmpty(data)) {
                log('Getting data failed.');
            } else {
                collection = data;
                $scope.itemsCount = data.length;

                orderItems(data, $scope.query.order);

                $scope.data = data.slice(0, $scope.query.limit);
            }
        }

        function getItemsFail() {
            console.log('Getting data failed.')
        }

        function orderItems(data, order) {
            data = data.sort(function (a, b) {
                if (a && b) {

                    if (order === 'bySubmittedAt' || order === '-bySubmittedAt')
                        return new Date(b.added) - new Date(a.added);

                    if (order === 'byUpdatedAt' || order === '-byUpdatedAt')
                        return new Date(b.updated) - new Date(a.updated);

                    if ((order === 'bySubmittedBy' || order == '-bySubmittedBy') && a.creator && b.creator)
                        return (a.creator.name + ' ' + a.creator.surname).localeCompare(b.creator.name + ' ' + b.creator.surname);
                }

                return 0;
            });

            if (order.slice(0, 1) === '-')
                data.reverse();
        }

        function filterItems() {
            filtredCollection = collection.filter(function (data) {
                if (data && data.portfolio != null)
                    return data.portfolio.title.slice(0, $scope.query.filter.length).toLowerCase() === $scope.query.filter.toLowerCase();
                if (data && data.material != null)
                    return $scope.getCorrectLanguageTitle(data.material).slice(0, $scope.query.filter.length).toLowerCase() === $scope.query.filter.toLowerCase();
                if (data && data.type == ".Material")
                    return $scope.getCorrectLanguageTitle(data).slice(0, $scope.query.filter.length).toLowerCase() === $scope.query.filter.toLowerCase();
                if (data && data.type == ".Portfolio")
                    return data.title.slice(0, $scope.query.filter.length).toLowerCase() === $scope.query.filter.toLowerCase();
            });

            $scope.itemsCount = filtredCollection.length;
            $scope.data = filtredCollection;
        }

        function buildTable(tableId, templateUrl) {
            var url = $sce.getTrustedResourceUrl(templateUrl);
            var $container = $(tableId).find('.table-container');

            $templateRequest(url).then(function (template) {
                $container.html($compile(template)($scope));
            });
        }

        $scope.getCorrectLanguageTitle = function (item) {
            if (item) {
                var result = getUserDefinedLanguageString(item.titles, translationService.getLanguage(), item.language);
                if (!result) {
                    return "";
                }
                return result;
            }
        };

        $scope.onReorder = function (order) {
            if (filtredCollection !== null)
                orderItems(filtredCollection, order);
            else
                orderItems(collection, order);

            $scope.data = paginate($scope.query.page, $scope.query.limit);
        };

        function paginate(page, limit) {
            var skip = (page - 1) * limit;
            var take = skip + limit;

            if (filtredCollection !== null)
                return filtredCollection.slice(skip, take);

            return collection.slice(skip, take);
        }

        $scope.onPaginate = function (page, limit) {
            $scope.data = paginate(page, limit);
        };

        $scope.removeFilter = function () {
            $scope.filter.show = false;
            $scope.query.filter = '';
            $scope.itemsCount = collection.length;
            filtredCollection = null;

            $scope.data = paginate($scope.query.page, $scope.query.limit);

            if ($scope.filter.form.$dirty) {
                $scope.filter.form.$setPristine();
            }
        };

        $scope.$watch('query.filter', function (newValue, oldValue) {
            if (newValue === oldValue) return;
            filterItems();
        });

        $scope.formatDate = function (date) {
            return formatDateToDayMonthYear(date);
        }

        return {
            getItemsSuccess: getItemsSuccess,
            getItemsFail: getItemsFail,
            buildTable: buildTable
        };
    }]);
});