'use strict'

{
const lists = {
    dopUnreviewed: 'directives/dashboard/unreviewed.html',
    dopImproper: 'directives/dashboard/improper.html',
    dopChanged: 'directives/dashboard/changed.html',
    dopBroken: 'directives/dashboard/broken.html',
    dopDeleted: 'directives/dashboard/deleted.html',
    dopUsers: 'directives/dashboard/users.html'
}
Object.keys(lists).forEach(key =>
    directive(key, {
        templateUrl: lists[key]
    })
)
}
