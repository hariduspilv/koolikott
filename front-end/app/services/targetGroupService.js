define([
    'angularAMD'
], function (angularAMD) {

    angularAMD.factory('targetGroupService', ['$translate',
        function ($translate) {

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

            var instance = {

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
                    if(!selectedTargetGroup) {
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

                /**
                 * Returns minimal data, it means that if a parent is found in the array,
                 * then its children will be removed
                 */
                getLabelByTargetGroupsOrAll: function (targetGroups) {
                    if (!targetGroups) {
                        return [];
                    }

                    return this.getMinimalGroups(targetGroups);
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
                        if (i != list.length - 1) {
                            result.push($translate.instant("TARGET_GROUP_" + list[i]) + ", ");
                        } else {
                            result.push($translate.instant("TARGET_GROUP_" + list[i]));
                        }
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

                    return i==j;
                }
            };

            return instance;
        }
    ]);
});
