'use strict';

angular.module('koolikottApp').controller('baseTableViewController', [
    '$scope', '$location', 'translationService', 'serverCallService', '$filter', '$mdDialog', '$route', 'taxonService', 'sortService',
    function ($scope, $location, translationService, serverCallService, $filter, $mdDialog, $route, taxonService, sortService) {
        $scope.viewPath = $location.path();
        var collection = null;
        var filteredCollection = null;

        $scope.itemsCount = 0;

        $scope.filter = {
            options: {
                debounce: 500
            }
        };

        $scope.query = {
            filter: '',
            order: 'bySubmittedAt',
            limit: 10,
            page: 1
        };

        init();

        function init() {
            serverCallService.makeGet("rest/user/all", {}, successUsersCall, fail);
        }

        function successUsersCall(data) {
            if (data) $scope.users = data;
            else fail();
        }

        function fail() {
            console.log("Failed to get users list");
        }

        $scope.getTranslation = function (key) {
            return $filter('translate')(key);
        };

        $scope.editUser = function (user) {
            if (!user) return;
            var editUserScope = $scope.$new(true);
            editUserScope.user = user;

            $mdDialog.show({
                templateUrl: 'views/editUserDialog/editUser.html',
                controller: 'editUserController',
                scope: editUserScope
            }).then(function () {
                $route.reload();
            });
        };

        $scope.querySearch = function (query) {
            return query ? $scope.users.filter(createFilterFor(query)) : $scope.users;
        };

        $scope.getUsernamePlaceholder = function () {
            return $filter('translate')('USERNAME');
        };

        $scope.isView = function (path) {
            return $scope.viewPath === path
        };

        function createFilterFor(query) {
            var lowercaseQuery = angular.lowercase(query);

            return function filterFn(user) {
                return (user.username.indexOf(lowercaseQuery) === 0);
            };
        }

        $scope.getItemsSuccess = function (data, order, merge) {
            if (isEmpty(data)) {
                log('Getting data failed.');
            } else {
                if (order) {
                    $scope.query.order = order;
                }
                if (merge) {
                    data = mergeReports(data);
                }

                collection = data;
                $scope.itemsCount = data.length;

                sortService.orderItems(data, $scope.query.order);
                $scope.data = data.slice(0, $scope.query.limit);
            }
        };

        $scope.formatMaterialUpdatedDate = function (updatedDate) {
            return formatDateToDayMonthYear(updatedDate);
        };

        $scope.getLearningObjectTitle = function (learningObject) {
            if (!learningObject) return;

            if (learningObject.title) {
                return learningObject.title;
            }
            else {
                return $scope.getCorrectLanguageTitle(learningObject);
            }
        };

        $scope.getLearningObjectUrl = function (learningObject) {
            if (!learningObject) return;

            if (isPortfolio(learningObject.type)) {
                return "/portfolio?id=" + learningObject.id;
            } else {
                return "/material?id=" + learningObject.id;
            }
        };

        function filterItems() {
            filteredCollection = collection.filter(function (data) {
                if (!data) {
                    return;
                }

                var text;

                if (data.learningObject && data.learningObject.type) {
                    if (isMaterial(data.learningObject.type))
                        text = $scope.getCorrectLanguageTitle(data.learningObject);
                    if (isPortfolio(data.learningObject.type))
                        text = data.learningObject.title;
                }

                if (data.material)
                    text = $scope.getCorrectLanguageTitle(data.material);

                if (data.type === '.Material')
                    text = $scope.getCorrectLanguageTitle(data);

                if (data.type === '.Portfolio')
                    text = data.title;

                if (text) {
                    return text.toLowerCase().indexOf($scope.query.filter.toLowerCase()) !== -1;
                }
            });

            $scope.itemsCount = filteredCollection.length;
            $scope.data = filteredCollection;
        }

        $scope.getCorrectLanguageTitle = function (item) {
            if (item.titles) {
                var result = getUserDefinedLanguageString(item.titles, translationService.getLanguage(), item.language);
                if (!result) {
                    return "";
                }
                return result;
            } else {
                return item.title
            }
        };

        $scope.onReorder = function (order) {
            if (filteredCollection !== null)
                sortService.orderItems(filteredCollection, order);
            else
                sortService.orderItems(collection, order);

            $scope.data = paginate($scope.query.page, $scope.query.limit);
        };

        $scope.getTaxonTranslation = function (taxon) {
            if (taxon.level = ".TaxonDTO") {
                taxon = taxonService.getFullTaxon(taxon.id);
            }

            if (taxon.level !== '.EducationalContext') {
                return taxon.level.toUpperCase().substr(1) + "_" + taxon.name.toUpperCase();
            } else {
                return taxon.name.toUpperCase();
            }
        };

        function paginate(page, limit) {
            var skip = (page - 1) * limit;
            var take = skip + limit;

            if (filteredCollection !== null)
                return filteredCollection.slice(skip, take);

            return collection.slice(skip, take);
        }

        $scope.onPaginate = function (page, limit) {
            $scope.data = paginate(page, limit);
        };

        $scope.removeFilter = function () {
            $scope.filter.show = false;
            $scope.query.filter = '';
            $scope.itemsCount = collection.length;
            filteredCollection = null;

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
        };

        /**
         *  Merge reports so that every learning object is represented by only 1 row in the table.
         */
        function mergeReports(items) {
            var merged = [];

            for (var i = 0; i < items.length; i++) {
                var item = items[i];
                var isAlreadyReported = false;

                for (var j = 0; j < merged.length; j++) {
                    if (reportsContainSameLearningObject(merged[j], item)) {
                        isAlreadyReported = true;

                        merged[j].reportCount++;

                        // show the newest date
                        if (new Date(merged[j].added) < new Date(item.added)) {
                            merged[j].added = item.added;
                        }

                        break;
                    }
                }

                if (!isAlreadyReported) {
                    item.reportCount = 1;
                    merged.push(item);
                }
            }

            return merged;
        }

        function reportsContainSameLearningObject(report1, report2) {
            var id1, id2;

            if (report1.learningObject) {
                id1 = report1.learningObject.id;
            } else if (report1.material) {
                id1 = report1.material.id;
            }

            if (report2.learningObject) {
                id2 = report2.learningObject.id;
            } else if (report2.material) {
                id2 = report2.material.id;
            }

            return id1 === id2;
        }
    }
]);
