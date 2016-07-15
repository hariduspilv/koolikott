describe('General',function() {

    var assert = require('assert');

    /* This parameter should be changed higher if your initial visual tests are failing */
    const PAUSE_TIME = 5000;

    this.timeout(99999999);

    it('Compares navmenu visually', function(done) {
        browser
            .url('https://localhost/')
            .pause(PAUSE_TIME)
            .webdrivercss('nav', {
                name: 'navmenu',
                elem: '#navmenu'
            }, function(err,res) {
                assert.ifError(err);
            })
            .call(done);
    });

    it('Compares the search bar visually', function(done){
        browser
            .url('https://localhost/')
            .pause(PAUSE_TIME)
            .click('#header-show-detailed-search-icon')
            .pause(PAUSE_TIME)
            .webdrivercss('search', {
                name: 'dialog',
                elem: '#detailed-search'
            }, function(err,res) {
                assert.ifError(err);
            })
            .call(done);
    });

});

