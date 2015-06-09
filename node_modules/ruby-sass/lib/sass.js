'use strict';

var path = require('path');
var spawn = require('win-spawn');
var portfinder = require('portfinder');
var util = require('util');
var dnode = require('dnode');
var async = require('async');
var SassError = require('./sass-error');
var EventEmitter = require('events').EventEmitter;
// var fs = require('graceful-fs');

var defaults = {
    port: null,
    basePort: 8500
};

var Sass = function (options) {
    EventEmitter.call(this);

    // process options
    if (!options) {
        options = defaults;
    }
    else {
        for (var key in defaults) {
            if (defaults.hasOwnProperty(key) && options[key] == null) {
                options[key] = defaults[key];
            }
        }
    }

    this.ready = false;


    // do preparation then resolve deferred
    var self = this;
    async.waterfall([
        // choose a port
        function (done) {
            if (typeof options.port === 'number') {
                done(null, options.port);
            }
            else {
                portfinder.basePort = options.basePort;
                portfinder.getPort(done);
            }
        },

        // start ruby
        function (port, done) {
            // start ruby process (telling it our chosen port, plus sass options)
            var args = [ path.resolve(__dirname, '..', 'server.rb'), port ];
            var ruby = spawn('ruby', args);
            ruby.stdout.setEncoding('utf8');
            ruby.stdin.setEncoding('utf8');

            // listen for the line "ready"
            ruby.stdout.on('data', function (data) {
                var output = data.toString().trim();

                // console.log('STDOUT', output);

                if (data.toString().trim() === 'ready') done(null, port);
                else done(new Error('Unexpected output from Ruby app: ' + output));
            });

            ruby.stderr.on('data', function (data) {
                console.log('STDERR', data.toString());
            });

            ruby.on('error', done);

            self.stop = function () {
                ruby.kill();
            };

            process.on('exit', function () {
                ruby.kill();
            });
        },

        // establish dnode connection to the ruby process
        function (port, done) {
            var d = dnode.connect(port);

            d.on('remote', function (remote) {
                self.remote = remote;

                self.ready = true;
                self.emit('ready');
            });

            d.on('error', function (err) {
                throw err;
            });
            d.on('fail', function (err) {
                throw err;
            });
        }
    ]);
};

util.inherits(Sass, EventEmitter);


Sass.prototype.compile = function (filename, options, callback) {
    var self = this;
    var go = function () {
        if (callback == null) {
            callback = options;
            options = {};
        }

        options.filename = filename;
        if (!options.cwd) options.cwd = process.cwd();

        if (options.loadPaths && !Array.isArray(options.loadPaths))
            options.loadPaths = [options.loadPaths];

        self.remote.f(options, function (response) {
            var err = null;
            if (response.error) {
                // console.log('response error', response);
                err = new SassError(response.error, response.sass_backtrace);
                return callback(err);
            }

            callback(err, response.css, response.sourcemap || null);
        });
    };

    if (this.ready) go();
    else this.on('ready', go);
};


module.exports = Sass;
