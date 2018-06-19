'use strict';

/*angular.module('koolikottApp')
    .filter('itemGroupFilter', () => {


        return function (items, filtersGroups, filterGroupsExact, isGrouped) {

            if (isGrouped === 'noGrouping') return items

            if (filtersGroups['all'].isMaterialActive) return items
            return items.filter((item) => {
                let isMaterialActive = filtersGroups[item.foundFrom].isMaterialActive && item.type.toLowerCase().includes('material')
                let isPortfolioActive = filtersGroups[item.foundFrom].isPortfolioActive && item.type.toLowerCase().includes('portfolio')
                return isMaterialActive || isPortfolioActive
            })
        }
    })*/


function singleGroupsFilter(items, filtersGroups) {
    if (filtersGroups['all'].isMaterialActive) return items
    return items.filter((item) => {
        let isMaterialActive = filtersGroups[item.foundFrom].isMaterialActive && item.type.toLowerCase().includes('material')
        let isPortfolioActive = filtersGroups[item.foundFrom].isPortfolioActive && item.type.toLowerCase().includes('portfolio')
        return isMaterialActive || isPortfolioActive
    })
}

function phraseGrouping(items, filtersGroups, filterGroupsExact) {
    if (filtersGroups['all'].isMaterialActive && filterGroupsExact['all'].isMaterialActive) return items
}

function itemFilter(items, filtersGroups, filterGroupsExact, isGrouped) {
    if (isGrouped === 'grouping') return singleGroupsFilter(items, filtersGroups)
    if (isGrouped === 'phraseGrouping') return phraseGrouping(items, filtersGroups, filterGroupsExact)
    return items
}

angular.module('koolikottApp').filter('itemGroupFilter', function () {
    return (items, filtersGroups, filterGroupsExact, isGrouped) => {
        return itemFilter(items, filtersGroups, filterGroupsExact, isGrouped)
    }
})

