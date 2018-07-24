'use strict'

function isItemActive(filterGroups, learningObject) {
    const isMaterialActive = filterGroups[learningObject.foundFrom].isMaterialActive && learningObject.type.toLowerCase().includes('material')
    const isPortfolioActive = filterGroups[learningObject.foundFrom].isPortfolioActive && learningObject.type.toLowerCase().includes('portfolio')
    return isMaterialActive || isPortfolioActive
}

function singleGroupsFilter(items, filterGroups) {
    if (filterGroups['all'].isMaterialActive) return items
    return items.filter((learningObject) => {
        return isItemActive(filterGroups, learningObject)
    })
}

function phraseGrouping(items, filterGroups, filterGroupsExact) {
    if (filterGroups['all'].isMaterialActive && filterGroupsExact['all'].isMaterialActive) return items
    return items.filter((learningObject) => {
        const groups = learningObject.searchType === 'exact' ? filterGroupsExact : filterGroups
        if (groups['all'].isMaterialActive) return true
        return isItemActive(groups, learningObject)
    })
}

function itemFilter(items, filterGroups, filterGroupsExact, groupingView) {
    if (groupingView === 'grouping') return singleGroupsFilter(items, filterGroups)
    if (groupingView === 'phraseGrouping') return phraseGrouping(items, filterGroups, filterGroupsExact)
    return items
}

angular.module('koolikottApp').filter('itemGroupFilter', function () {
    return (items, filterGroups, filterGroupsExact, groupingView) => {
        return itemFilter(items, filterGroups, filterGroupsExact, groupingView)
    }
})

