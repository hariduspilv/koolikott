function isItemActive(filterGroups, item) {
    let isMaterialActive = filterGroups[item.foundFrom].isMaterialActive && item.type.toLowerCase().includes('material')
    let isPortfolioActive = filterGroups[item.foundFrom].isPortfolioActive && item.type.toLowerCase().includes('portfolio')
    return isMaterialActive || isPortfolioActive
}

function singleGroupsFilter(items, filterGroups) {
    if (filterGroups['all'].isMaterialActive) return items
    return items.filter((item) => {
        return isItemActive(filterGroups, item)
    })
}

function phraseGrouping(items, filterGroups, filterGroupsExact) {
    if (filterGroups['all'].isMaterialActive && filterGroupsExact['all'].isMaterialActive) return items
    return items.filter((item) => {
        let groups = item.searchType === 'exact' ? filterGroupsExact : filterGroups
        if (groups['all'].isMaterialActive) return true
        return isItemActive(groups, item)
    })
}

function itemFilter(items, filterGroups, filterGroupsExact, isGrouped) {
    if (isGrouped === 'grouping') return singleGroupsFilter(items, filterGroups)
    if (isGrouped === 'phraseGrouping') return phraseGrouping(items, filterGroups, filterGroupsExact)
    return items
}

angular.module('koolikottApp').filter('itemGroupFilter', function () {
    return (items, filterGroups, filterGroupsExact, isGrouped) => {
        return itemFilter(items, filterGroups, filterGroupsExact, isGrouped)
    }
})

