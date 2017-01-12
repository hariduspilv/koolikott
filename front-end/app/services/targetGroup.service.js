'use strict';

angular.module('koolikottApp').factory('targetGroupService', [
    '$translate',
    function ($translate) {
        const GRADE = "GRADE";
        const LEVEL = "LEVEL";
        const groups = [
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

        function getTargetGroupTranslation(value) {
            if (value) return [$translate.instant('TARGET_GROUP_' + value)];
            else return [];
        }

        function processLevel(targetGroups, groupParentMap, levelNr) {
            let result = [];

            let level = _.intersection(targetGroups, groups[levelNr].children);

            if (level.length === 3) {
                groupParentMap[levelNr] = groups[levelNr];
            } else if (level.length === 2) {
                // groupParentMap is not updated, meaning levels are not consecutive
                // If parent from previous level exists then it should be added to result
                Array.prototype.push.apply(result, mergeSchoolLevels(groupParentMap));
                clearObject(groupParentMap);

                Array.prototype.push.apply(result, mergeGroups(level));
            } else {
                Array.prototype.push.apply(result, mergeSchoolLevels(groupParentMap));
                clearObject(groupParentMap);

                Array.prototype.push.apply(result, getTargetGroupTranslation(level[0]));
            }

            return result;
        }

        /**
         * Merge groups together if possible
         * e.g 1. grade, 2.grade => 1-2. grade
         * @param level
         * @returns {[*]}
         */
        function mergeGroups(level) {
            level.sort();
            if (parseInt(level[0].slice(-1)) === parseInt(level[1].slice(-1)) - 1) {
                return [level[0].slice(-1) + ".-" + (level[1].slice(-1)) + ". " + $translate.instant(GRADE)];
            } else {
                return [level[0].slice(-1) + ", " + level[1].slice(-1) + ". " + $translate.instant(GRADE)];
            }
        }

        /**
         * parameter map: {key: level number, value: label}
         * @param map
         * @returns {Array}
         */
        function mergeSchoolLevels(map) {
            let keys = _.keys(map);
            let result = [];

            if (keys.length > 1) {
                keys = _.keys(map).map(key => parseInt(key));
                keys.sort();

                for (let i = 0; i < keys.length; i++) {
                    if (keys[i] === (keys[i + 1] - 1)) { // check if next element is consecutive
                        let start = keys[i];
                        let end;
                        for (let j = i + 1; j < keys.length; j++) { // look for the end of consecutive elements
                            if (keys[j] !== keys[j + 1] - 1) {
                                end = keys[j];
                                i = j;
                            }
                        }

                        // merge (e.g level1.. level3 => I-III level)
                        result.push("I".repeat(start) + "-" + "I".repeat(end) + " " + $translate.instant(LEVEL));
                    } else {
                        Array.prototype.push.apply(result, getTargetGroupTranslation(map[keys[i]].label));
                    }
                }

            } else if (keys.length === 1) {
                Array.prototype.push.apply(result, getTargetGroupTranslation(map[_.keys(map)[0]].label));
            }

            return result;
        }


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

                let targetGroups = selectedTargetGroup.slice();

                for (let i = 0; i < groups.length; i++) {
                    let index = targetGroups.indexOf(groups[i].label);
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
                let selectedTargetGroup = [];

                if (targetGroups) {
                    selectedTargetGroup = targetGroups;

                    for (let i = 0; i < groups.length; i++) {
                        let hasChildren = this.hasAllChildren(groups[i], selectedTargetGroup);

                        if (hasChildren) {
                            if (selectedTargetGroup.indexOf(groups[i].label) == -1) {
                                selectedTargetGroup.push(groups[i].label);
                            }
                        } else {
                            let index = selectedTargetGroup.indexOf(groups[i].label);
                            if (index != -1) {
                                selectedTargetGroup.splice(index, 1);
                            }
                        }

                    }
                }


                return selectedTargetGroup;
            },

            getMinimalGroups: function (targetGroups) {
                let list = [];

                if (targetGroups) {
                    for (let i = 0; i < groups.length; i++) {
                        let buffer = [];
                        let j;

                        for (j = 0; j < groups[i].children.length; j++) {

                            if (targetGroups.indexOf(groups[i].children[j]) != -1) {
                                buffer.push(groups[i].children[j]);
                            }
                        }

                        if (buffer.length == j) {
                            list.push(groups[i].label);
                        } else if (buffer.length >= 1) {
                            for (let x = 0; x < buffer.length; x++) {
                                list.push(buffer[x]);
                            }
                        }
                    }
                }

                return list;
            },

            getConcentratedLabelByTargetGroups: function (targetGroups) {
                if (!targetGroups) return [];
                let result = [];

                let groupParentMap = {};

                //Preschool
                let preschool = _.intersection(targetGroups, groups[0].children);
                if (preschool.length === 2) Array.prototype.push.apply(result, getTargetGroupTranslation(groups[0].label));
                else Array.prototype.push.apply(result, getTargetGroupTranslation(preschool[0]));


                //1st level
                Array.prototype.push.apply(result, processLevel(targetGroups, groupParentMap, 1));

                //2nd level
                Array.prototype.push.apply(result, processLevel(targetGroups, groupParentMap, 2));

                //3d level
                Array.prototype.push.apply(result, processLevel(targetGroups, groupParentMap, 3));

                // In case of 1-3 school level
                Array.prototype.push.apply(result, mergeSchoolLevels(groupParentMap));

                //Gymnasium
                if (targetGroups.indexOf(groups[4].label) !== -1 || targetGroups.indexOf(groups[4].children[0]) !== -1) {
                    Array.prototype.push.apply(result, getTargetGroupTranslation(groups[4].label));
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

                let list = this.getMinimalGroups(targetGroups);
                let result = [];
                for (let i = 0; i < list.length; i++) {
                    result.push($translate.instant("TARGET_GROUP_" + list[i]));
                }

                return result;
            },

            isParent: function (item) {
                for (let i = 0; i < groups.length; i++) {
                    if (item == groups[i].label) {
                        return true;
                    }
                }

                return false;
            },

            hasAllChildren: function (group, selectedTargetGroup) {
                let i = 0;
                let j = 0;
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
