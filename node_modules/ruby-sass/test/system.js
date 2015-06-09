/*jshint expr:true*/

var path = require('path');
var fs = require('fs');
var sass = require('..');
var chai = require('chai');
var expect = chai.expect;

var fixturesDir = path.resolve(__dirname, 'fixtures');

var fixtures = {
    simple         : path.join(fixturesDir, 'simple.scss'),
    simpleWithMap  : path.join(fixturesDir, 'simple-with-map.scss'),
    complex        : path.join(fixturesDir, 'complex.scss'),
    complexWithMap : path.join(fixturesDir, 'complex-with-map.scss'),
    broken         : path.join(fixturesDir, 'broken.scss')
};

var expected = {
    simple         : path.join(fixturesDir, 'simple.css'),
    simpleWithMap  : path.join(fixturesDir, 'simple-with-map.css'),
    simpleMap      : path.join(fixturesDir, 'simple-with-map.css.map'),
    complex        : path.join(fixturesDir, 'complex.css'),
    complexWithMap : path.join(fixturesDir, 'complex-with-map.css'),
    complexMap     : path.join(fixturesDir, 'complex-with-map.css.map')
};

for (var key in expected) {
    if (expected.hasOwnProperty(key)) {
        expected[key] = fs.readFileSync(expected[key]).toString();
    }
}

describe('ruby-sass', function () {
    it('can render simple scss', function (done) {
        sass(fixtures.simple, function (err, css) {
            expect(err).to.be.null;
            expect(css).to.equal(expected.simple);
            done();
        });
    });

    it('works with a big complex sass project', function (done) {
        this.timeout(10000); // for travis

        sass(fixtures.complex, function (err, css) {
            expect(err).to.be.null;
            expect(css).to.equal(expected.complex);
            done();
        });
    });

    it('handles sass syntax errors gracefully', function (done) {
        sass(fixtures.broken, function (err, css) {
            expect(css).to.not.exist;
            expect(err).to.be.an.instanceof(sass.SassError);
            expect(err.message).to.contain('Invalid CSS after');
            done();
        });
    });

    it('handles sass missing import errors gracefully');

    it('can render simple SCSS with source maps', function (done) {
        sass(fixtures.simpleWithMap, {sourcemap: true}, function (err, css, map) {
            expect(err).to.be.null;
            expect(css).to.equal(expected.simpleWithMap);
            expect(map).to.equal(expected.simpleMap.trim());
            done();
        });
    });

    it('can render big complex projects with source maps', function (done) {
        this.timeout(10000); // for travis

        sass(fixtures.complexWithMap, {sourcemap: true}, function (err, css, map) {
            expect(err).to.be.null;
            expect(css).to.equal(expected.complexWithMap);
            expect(map).to.equal(expected.complexMap.trim());
            done();
        });
    });
});
