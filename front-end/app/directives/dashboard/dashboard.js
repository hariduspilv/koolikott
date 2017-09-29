var directives = {
  dopUnreviewed: 'directives/dashboard/unreviewed.html',
  dopImproper: 'directives/dashboard/improper.html',
  dopChanged: 'directives/dashboard/changed.html',
  dopBroken: 'directives/dashboard/broken.html',
  dopDeleted: 'directives/dashboard/deleted.html',
  dopUsers: 'directives/dashboard/users.html'
}

Object.keys(directives).forEach(function (key) {
  angular.module('koolikottApp').directive(key, function () {
    return {
      templateUrl: directives[key]
    }
  })
})