describe('Portfolio',function() {

    var assert = require('assert');

    /* This parameter should be changed higher if your initial visual tests are failing */
    const PAUSE_TIME = 5000;

    this.timeout(99999999);

    before(function() {
        browser
            .url('https://localhost/dev/login/39310174218')
    });

    it('Compares portfolio visually', function (done) {
        browser
            .pause(PAUSE_TIME)
            .url('https://localhost/portfolio?id=4244')
            .pause(PAUSE_TIME)
            .click('#show-comments')
            .pause(PAUSE_TIME)
            .webdrivercss('portfolio', {
                name: 'comments',
                elem: '#comment-list'
            }, function(err,res) {
                assert.ifError(err);
            })
            .webdrivercss('portfolio', {
                name: 'right-sidebar',
                elem: '#sidebar-right'
            }, function(err,res) {
                assert.ifError(err);
            })
            .webdrivercss('portfolio', {
                name: 'left-sidebar',
                elem: '#sidebar-left'
            }, function(err,res) {
                assert.ifError(err);
            })
            .call(done);
    });

    it('Compares portfolio create view visually', function(done) {
        browser
            .url('https://localhost/')
            .pause(PAUSE_TIME)
            .click('#add-portfolio')
            .pause(PAUSE_TIME)
            .webdrivercss('portfolio-create', {
                name: 'add',
                elem: '#add-portfolio-modal'
            }, function(err,res) {
                assert.ifError(err);
            })
            .call(done);
    });

});
