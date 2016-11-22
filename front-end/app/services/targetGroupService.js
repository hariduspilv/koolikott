define([
    'angularAMD'
], function (angularAMD) {

    angularAMD.factory('targetGroupService', [
        function () {

            var preschoolGroups = ['PRESCHOOL', 'ZERO_FIVE', 'SIX_SEVEN'];
            var level1Groups = ['LEVEL1', 'GRADE1', 'GRADE2', 'GRADE3'];
            var level2Groups = ['LEVEL2', 'GRADE4', 'GRADE5', 'GRADE6'];
            var level3Groups = ['LEVEL3', 'GRADE7', 'GRADE8', 'GRADE9'];
            var secondaryGroups = ['GYMNASIUM'];

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
                },
            ]

            function map(group) {
                return group.map(function (item, index) {
                    return {
                        name: item,
                        parent: index === 0
                    };
                });
            }

            var instance = {

                /**
                 * Get all target groups.
                 */
                getAll: function () {
                    return groups;
                },

                /**
                 * Get all target groups that can be selected under the specified educational context.
                 */
                getByEducationalContext: function (educationalContext) {
                    if (educationalContext.name === 'PRESCHOOLEDUCATION') {
                        return [groups[0]]
                    } else if (educationalContext.name === 'BASICEDUCATION') {
                        return [groups[1], groups[2], groups[3]];
                    } else if (educationalContext.name === 'SECONDARYEDUCATION') {
                        return [groups[4]];
                    }
                },

                /**
                 * Get all target groups belonging to a label (for example PRESCHOOL or LEVEL1).
                 * If selectedTargetGroup is a single target group and not a label, an array consisting
                 * of only that target group is returned.
                 */
                getByLabel: function (selectedTargetGroup) {
                    var targetGroups = selectedTargetGroup.slice();

                    for(var i = 0; i < groups.length; i++) {
                        var index = targetGroups.indexOf(groups[i].label);
                        if(index != -1) {
                            targetGroups.splice(index, 1);
                        }
                    }

                    return targetGroups;
                },

                /**
                 * Get the label that represents all the selected target groups.
                 * If targetGroups contains only one target group, that target group is returned.
                 * @param targetGroups  array of target groups
                 */
                getLabelByTargetGroups: function (targetGroups) {
                    var selectedTargetGroup = [];
                    /*if(targetGroups[0]) {
                        selectedTargetGroup = targetGroups[0].slice();
                    }*/

                    // Refactor

                    if (targetGroups) {
                        selectedTargetGroup = targetGroups;

                        for(var i = 0; i < groups.length; i++) {
                            var hasChildren = this.hasAllChildren(groups[i], selectedTargetGroup);

                            if(hasChildren) {
                                if(selectedTargetGroup.indexOf(groups[i].label) == -1) {
                                    selectedTargetGroup.push(groups[i].label);
                                }
                            } else {
                                var index = selectedTargetGroup.indexOf(groups[i].label);
                                if(index != -1) {
                                    selectedTargetGroup.splice(index, 1);
                                }
                            }

                        }
                    }



                    return selectedTargetGroup;
                },

                getMinimalGroups: function(targetGroups) {
                    //if group has all, remove children and add parent
                    var list = [];

                    if(targetGroups) {
                        for (var i = 0; i < groups.length; i++) {
                            var buffer = [];
                            var j;

                            for (j = 0; j < groups[i].children.length; j++) {

                                if(targetGroups.indexOf(groups[i].children[j]) != -1) {
                                    buffer.push(groups[i].children[j]);
                                }
                            }

                            if(buffer.length == j) {
                                list.push(groups[i].label);
                            } else if (buffer.length >= 1){
                                for (var x = 0; x < buffer.length; x++) {
                                    list.push(buffer[x]);
                                }
                            }
                        }
                    }

                    return list;
                },

                /**
                 * Get the label that represents all the selected target groups.
                 * If there is no such label, return all target groups.
                 */
                // TODO: new comment because the logic has changed
                getLabelByTargetGroupsOrAll: function (targetGroups) {
                    if (!targetGroups) {
                        return [];
                    }

                    return this.getMinimalGroups(targetGroups);
                },

                areGroupElementsInArray: function (array, list) {
                    var i = 0;
                    var j = 0;

                    for (i; i < list.length; i++) {
                        if (array.indexOf(list[i]) != -1) {
                            j++;
                        }
                    }

                    if (i == j) {
                        return true;
                    } else {
                        return false;
                    }
                },

                getGroups: function () {
                    return groups;
                },

                isParent: function (item) {
                    if (item !== null) {
                        for(var i = 0; i < groups.length; i++) {
                            if (item == groups[i].label) {
                                return true;
                            }
                        }
                    }

                    return false;
                },

                getAllChildren: function(data) {
                    for (i = 0; i < groups.length; i++) {
                        var index = data.indexOf(groups[i].label);
                        if(index != -1) {
                            data.splice(index, 1);
                        }
                    }
                    return data;
                },

                hasAllChildren: function(group, selectedTargetGroup) {
                var i = 0;
                var j = 0;
                for (i; i < group.children.length; i++) {
                    if(selectedTargetGroup.indexOf(group.children[i]) != -1) {
                        j++;
                    }
                }
                if (i == j) {
                    return true;
                } else {
                    return false;
                }
            }
            };

            return instance;
        }
    ]);
});
