angular.module('koolikottApp')
    .filter('itemGroupFilter', function () {
        return function (items, filtersGroups, isGrouped) {
            if (!isGrouped) return items
            if (filtersGroups['GROUPS_ALL'].isMaterialActive) return items
            return items.filter((item) => {
                let isMaterialActive = filtersGroups[item.foundFrom].isMaterialActive && item.type.toLowerCase().includes('material')
                let isPortfolioActive = filtersGroups[item.foundFrom].isPortfolioActive && item.type.toLowerCase().includes('portfolio')
                return isMaterialActive || isPortfolioActive
            })
        }
    })
