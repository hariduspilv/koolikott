'use strict';

var util = require('util');

var SassError = function (message, backtrace) {
    Error.call(this);

    this.name = 'SassError';
    this.message = message;

    this.stack = this.message + backtrace.map(function (trace) {
        return '\n    at ' + trace.filename + ':' + trace.line;
    }).join('');
};

util.inherits(SassError, Error);

module.exports = SassError;
