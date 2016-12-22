'use strict';

angular.module('koolikottApp').factory('targetGroupService', [
    '$translate',
    function ($translate) {
        var GRADE = "GRADE";
        var groups = [
            {
                label: 'PRESCHOOL',
                children: ['ZERO_FIVE', 'SIX_SEVEN']
            }, {
                label: 'LEVEL1',
                children: ['GRADE1', 'GRADE2', 'GRADE3']
            }, {
                label: 'LEVEL2',
                children: ['GRADE4', 'GRADE5', 'GRADE6']
            }, {
                label: 'LEVEL3',
                children: ['GRADE7', 'GRADE8', 'GRADE9']
            }, {
                label: 'LEVEL_GYMNASIUM',
                children: ['GYMNASIUM']
            }
        ];

        return {

            /**
             * Get all target groups.
             */
            getAll: function () {
                return groups;
            },

            /**
             * Removes all parents from selectedTargetGroup (because none of the parents are stored in the database)
             */
            getByLabel: function (selectedTargetGroup) {
                if (!selectedTargetGroup) {
                    return [];
                }

                var targetGroups = selectedTargetGroup.slice();

                for (var i = 0; i < groups.length; i++) {
                    var index = targetGroups.indexOf(groups[i].label);
                    if (index != -1) {
                        targetGroups.splice(index, 1);
                    }
                }

                return targetGroups;
            },

            /**
             * Adds necessary parents and returns an array that is used by the select
             */
            getLabelByTargetGroups: function (targetGroups) {
                var selectedTargetGroup = [];

                if (targetGroups) {
                    selectedTargetGroup = targetGroups;

                    for (var i = 0; i < groups.length; i++) {
                        var hasChildren = this.hasAllChildren(groups[i], selectedTargetGroup);

                        if (hasChildren) {
                            if (selectedTargetGroup.indexOf(groups[i].label) == -1) {
                                selectedTargetGroup.push(groups[i].label);
                            }
                        } else {
                            var index = selectedTargetGroup.indexOf(groups[i].label);
                            if (index != -1) {
                                selectedTargetGroup.splice(index, 1);
                            }
                        }

                    }
                }


                return selectedTargetGroup;
            },

            getMinimalGroups: function (targetGroups) {
                var list = [];

                if (targetGroups) {
                    for (var i = 0; i < groups.length; i++) {
                        var buffer = [];
                        var j;

                        for (j = 0; j < groups[i].children.length; j++) {

                            if (targetGroups.indexOf(groups[i].children[j]) != -1) {
                                buffer.push(groups[i].children[j]);
                            }
                        }

                        if (buffer.length == j) {
                            list.push(groups[i].label);
                        } else if (buffer.length >= 1) {
                            for (var x = 0; x < buffer.length; x++) {
                                list.push(buffer[x]);
                            }
                        }
                    }
                }

                return list;
            },

            getConcentratedLabelByTargetGroups: function (targetGroups) {
                if (!targetGroups) return [];
                var result = [];

                function addToResult(value) {
                    if (value) result.push($translate.instant('TARGET_GROUP_' + value));
                }

                //Preschool
                var preschool = _.intersection(targetGroups, groups[0].children);
                if (preschool.length === 2) addToResult(groups[0].label);
                else {
                    addToResult(preschool[0]);
                }
                //1st level
                var level1 = _.intersection(targetGroups, groups[1].children);
                if (level1.length === 3) addToResult(groups[1].label);
                else if (level1.length === 2) {
                    level1.sort();
                    if (parseInt(level1[0].slice(-1)) === parseInt(level1[1].slice(-1)) - 1) {
                        result.push(level1[0].slice(-1) + ".-" + (level1[1].slice(-1)) + ". " + $translate.instant(GRADE));
                    }else{
                        result.push(level1[0].slice(-1) + ", " + level1[1].slice(-1) + ". " + $translate.instant(GRADE));
                    }
                } else {
                    addToResult(level1[0]);
                }

                //2nd level
                var level2 = _.intersection(targetGroups, groups[2].children);
                if (level2.length === 3) addToResult(groups[2].label);
                else if (level2.length === 2) {
                    level2.sort();
                    if (parseInt(level2[0].slice(-1)) === parseInt(level2[1].slice(-1)) - 1) {
                        result.push(level2[0].slice(-1) + ".-" + (level2[1].slice(-1)) + ". " + $translate.instant(GRADE));
                    }else{
                        result.push(level2[0].slice(-1) + ", " + level2[1].slice(-1) + ". " + $translate.instant(GRADE));
                    }
                } else {
                    addToResult(level2[0]);
                }

                //3d level
                var level3 = _.intersection(targetGroups, groups[3].children);
                if (level3.length === 3) addToResult(groups[3].label);
                else if (level3.length === 2) {
                    level3.sort();
                    if (parseInt(level3[0].slice(-1)) === parseInt(level3[1].slice(-1)) - 1) {
                        result.push(level3[0].slice(-1) + ".-" + (level3[1].slice(-1)) + ". " + $translate.instant(GRADE));
                    }else{
                        result.push(level3[0].slice(-1) + ", " + level3[1].slice(-1) + ". " + $translate.instant(GRADE));
                    }
                } else {
                    addToResult(level3[0]);
                }

                //Gymnasium
                if (targetGroups.indexOf(groups[4].label) !== -1 || targetGroups.indexOf(groups[4].children[0]) !== -1) {
                    addToResult(groups[4].label);
                }

                return result;
            },

            /**
             * Returns an array with translated grades for select label
             */
            getSelectedText: function (targetGroups) {
                if (!targetGroups) {
                    return [];
                }

                var list = this.getMinimalGroups(targetGroups);
                var result = [];
                for (var i = 0; i < list.length; i++) {
                    result.push($translate.instant("TARGET_GROUP_" + list[i]));
                }

                return result;
            },

            isParent: function (item) {
                for (var i = 0; i < groups.length; i++) {
                    if (item == groups[i].label) {
                        return true;
                    }
                }

                return false;
            },

            hasAllChildren: function (group, selectedTargetGroup) {
                var i = 0;
                var j = 0;
                for (i; i < group.children.length; i++) {
                    if (selectedTargetGroup && selectedTargetGroup.indexOf(group.children[i]) != -1) {
                        j++;
                    }
                }

                return i == j;
            }
        };
    }
]);
