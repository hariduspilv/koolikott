describe('Profile',function() {

    var assert = require('assert');

    /* This parameter should be changed higher if your initial visual tests are failing */
    const PAUSE_TIME = 5000;

    this.timeout(99999999);

    before(function() {
        browser
            .url('https://localhost/dev/login/39310174218')
    });

    it('Compares profile content visually', function (done) {
        browser
            .pause(PAUSE_TIME)
            .url('https://localhost/joonas.dev')
            .pause(PAUSE_TIME)
            .webdrivercss('profile', {
                name: 'content',
                elem: '#main-content'
            }, function(err,res) {
                assert.ifError(err);
            })
            .webdrivercss('profile', {
                name: 'sidebar',
                elem: '#sidebar'
            }, function(err,res) {
                assert.ifError(err);
            })
            .call(done);
    });

});
