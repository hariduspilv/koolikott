'use strict';

var Sass = require('./lib/sass');
var SassError = require('./lib/sass-error');

var getSingleton = (function () {
    var singleton;

    return function () {
        if (!singleton) singleton = new Sass();
        return singleton;
    };
})();

var defaults = {};

var main = function (file, options, callback) {
    getSingleton().compile(file, options, callback);
};


main.Sass = Sass;
main.SassError = SassError;
module.exports = main;
