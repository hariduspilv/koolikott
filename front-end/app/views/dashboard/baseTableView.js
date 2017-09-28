'use strict';

angular.module('koolikottApp').controller('baseTableViewController', [
    '$rootScope', '$scope', '$location', 'translationService', 'serverCallService', '$filter', '$mdDialog', '$route', 'taxonService', 'sortService',
    function ($rootScope, $scope, $location, translationService, serverCallService, $filter, $mdDialog, $route, taxonService, sortService) {
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

            var _init = function (titleKey, restUri, sortBy, merge) {
                $scope.titleTranslationKey = titleKey
                serverCallService.makeGet(restUri, {}, function (data) {
                    if (data)
                        $scope.getItemsSuccess(data, sortBy, merge);
                })
            }

            switch ($scope.viewPath) {
                case '/dashboard/unReviewed':
                    return _init(
                        'DASHBOARD_UNREVIEWED',
                        'rest/admin/firstReview/unReviewed',
                        'byAddedAt'
                    )
                case '/dashboard/improperMaterials':
                    return _init(
                        'DASHBOARD_IMRPOPER_MATERIALS',
                        'rest/admin/improper/material',
                        'byReportCount',
                        true
                    )
                case '/dashboard/improperPortfolios':
                    return _init(
                        'DASHBOARD_IMRPOPER_PORTFOLIOS',
                        'rest/admin/improper/portfolio',
                        'byReportCount',
                        true
                    )
                case '/dashboard/changedLearningObjects':
                    return _init(
                        'DASHBOARD_CHANGED_LEARNING_OBJECTS',
                        'rest/admin/changed',
                        'byAddedAt'
                    )
                case '/dashboard/brokenMaterials':
                    return _init(
                        'BROKEN_MATERIALS',
                        'rest/admin/brokenContent/getBroken',
                        'byReportCount',
                        true
                    )
                case '/dashboard/deletedPortfolios':
                    return _init(
                        'DASHBOARD_DELETED_MATERIALS',
                        'rest/admin/deleted/material/getDeleted',
                        'byUpdatedAt'
                    )
                case '/dashboard/deletedMaterials':
                    return _init(
                        'DASHBOARD_DELETED_PORTFOLIOS',
                        'rest/admin/deleted/portfolio/getDeleted',
                        'byUpdatedAt'
                    )
                case '/dashboard/moderators':
                    return _init(
                        'MODERATORS_TAB',
                        'rest/admin/moderator',
                        'byUsername'
                    )
                case '/dashboard/restrictedUsers':
                    return _init(
                        'RESTRICTED_USERS_TAB',
                        'rest/admin/restrictedUser',
                        'byUsername'
                    )
            }
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

        $scope.openLearningObject = function (learningObject) {
            $location.url(
                $scope.getLearningObjectUrl(learningObject)
            )
        }

        function isFilterMatch(str, query) {
            return str.toLowerCase().indexOf(query) > -1
        }

        function filterItems() {
            filteredCollection = collection.filter(function (data) {
                if (!data) {
                    return;
                }

                var query = $scope.query.filter.toLowerCase()

                if ($scope.isView('/dashboard/moderators') || $scope.isView('/dashboard/restrictedUsers')) {
                    return (
                        isFilterMatch(data.name+' '+data.surname, query) ||
                        isFilterMatch(data.name, query) ||
                        isFilterMatch(data.surname, query) ||
                        isFilterMatch(data.username, query)
                    )
                } else {
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

                    if (text)
                        return isFilterMatch(text, query)
                }
            });

            $scope.itemsCount = filteredCollection.length;
            $scope.data = paginate($scope.query.page, $scope.query.limit);
        }

        $scope.getCorrectLanguageTitle = function (item) {
            return item.titles
                ? getUserDefinedLanguageString(item.titles, translationService.getLanguage(), item.language) || ''
                : item.title
        };

        $scope.onReorder = function (order) {
            if (filteredCollection !== null)
                sortService.orderItems(filteredCollection, order);
            else
                sortService.orderItems(collection, order);

            $scope.data = paginate($scope.query.page, $scope.query.limit);
        };

        $scope.getTaxonTranslation = function (taxon) {
            if (!taxon)
                return

            if (taxon.level = ".TaxonDTO") {
                taxon = taxonService.getFullTaxon(taxon.id);
            }

            if (!taxon)
                return

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

        $scope.clearFilter = function () {
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
])
