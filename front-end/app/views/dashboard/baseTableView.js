define([
    'app',
    'jquery',
    'services/translationService',
    'directives/dashboard/userManagement/moderatorsTable',
    'directives/dashboard/userManagement/restrictedUsersTable',
    'directives/dashboard/deletedPortfolio/deletedPortfolio',
    'directives/dashboard/deletedMaterial/deletedMaterial',
    'directives/dashboard/improper/improperMaterial',
    'directives/dashboard/improper/improperPortfolio',
    'directives/dashboard/broken/brokenMaterial',
], function (app) {
    return ['$scope', '$location', 'translationService',
        function ($scope, $location, translationService) {
            $scope.viewPath = $location.path();
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
                limit: 10,
                page: 1
            };

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

                    orderItems(data, $scope.query.order);
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

                if (learningObject.type === ".Portfolio") {
                    return "/portfolio?id=" + learningObject.id;
                } else {
                    return "/material?materialId=" + learningObject.id;
                }
            };

            function orderItems(data, order) {
                data = data.sort(function (a, b) {
                    if (a && b) {

                        if (order === 'bySubmittedAt' || order === '-bySubmittedAt')
                            return new Date(b.added) - new Date(a.added);

                        if (order === 'byUpdatedAt' || order === '-byUpdatedAt')
                            return new Date(b.updated) - new Date(a.updated);

                        if ((order === 'bySubmittedBy' || order == '-bySubmittedBy') && a.creator && b.creator) {
                            var aName = a.creator.name + ' ' + a.creator.surname;
                            var bName = b.creator.name + ' ' + b.creator.surname;
                            if (a.reportCount > 1)
                                aName = translationService.instant('REPORTED_BY_MULTIPLE_USERS');
                            if (b.reportCount > 1)
                                bName = translationService.instant('REPORTED_BY_MULTIPLE_USERS');

                            return aName.localeCompare(bName);
                        }

                        if (order === 'byReportCount' || order === '-byReportCount')
                            return b.reportCount - a.reportCount;
                    }

                    return 0;
                });

                if (order.slice(0, 1) === '-')
                    data.reverse();
            }

            function filterItems() {
                filtredCollection = collection.filter(function (data) {
                    if (!data) {
                        return;
                    }

                    var text;

                    if (data.learningObject && data.learningObject.type) {
                        if (data.learningObject.type === '.Material')
                            text = $scope.getCorrectLanguageTitle(data.learningObject);
                        if (data.learningObject.type === '.Portfolio')
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

                $scope.itemsCount = filtredCollection.length;
                $scope.data = filtredCollection;
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
        }];
});
