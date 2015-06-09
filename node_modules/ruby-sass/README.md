# node-ruby-sass

[![Build Status](https://secure.travis-ci.org/callumlocke/node-ruby-sass.png?branch=master)](http://travis-ci.org/callumlocke/node-ruby-sass)

**WORK IN PROGRESS...**

This Node module is an attempt to provide a faster binding to Ruby Sass than currently available.

Explanation: Sass in a Rails app with LiveReload seems much faster than things like [gulp-ruby-sass](https://github.com/sindresorhus/gulp-ruby-sass). I'm guessing this is because Rails runs Sass using the same Ruby process every compile, avoiding Ruby spin-up time, and benefiting from in-memory caching. This module tries to do something similar. Benchmarks to follow.


## Installation

```sh
$ npm install ruby-sass
```

## Usage

```js
var sass = require('ruby-sass');

sass('path/to/something.scss', function (err, css) {
    // got the css (or an error with a *sass* stack trace)
});
```

Repeated calls should be faster than the first one.

You can also pass options as a second argument.

### Options

* `loadPaths` (array)
* `sourcemap` (boolean)


## Alternative API

```js
var Sass = require('ruby-sass').Sass;

var sass = new Sass();

sass.compile(filename, options, callback); // same as the `sass` function in standard API.
```

### Constructor options

These are optional.

- `port` – which port to use
- `basePort` (default: `8500`) – if you don't set a `port`, portfinder will be used to find one manually, starting at this base port.


## To do

- source maps
- ~~make it so you don't have to wait for ready~~
- ~~kill ruby process when node exits or on uncaught exception~~
- ~~make a way to stop it manually~~
- write benchmarks
- see if repeat runs on the same file are actually any faster. if not, look at using Sass::Plugin (?)
- make post install script to check if required gems are installed and print instructions to install them (or just install them?)
- ~~pass in sass options per-file, not per-instance~~
- ~~simplify api~~
- test it on windows


## Licence

MIT
