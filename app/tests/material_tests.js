describe('Material',function() {

    var assert = require('assert');

    /* This parameter should be changed higher if your initial visual tests are failing */
    const PAUSE_TIME = 5000;

    this.timeout(99999999);

    before(function() {
        browser
            .url('https://localhost/dev/login/39310174218')
    });

    it('Compares material edit view visually', function (done) {
        browser
            .pause(PAUSE_TIME)
            .url('https://localhost/portfolio?id=4244')
            .pause(PAUSE_TIME)
            .click('#material-menu')
            .pause(PAUSE_TIME)
            .webdrivercss('portfolio-edit-1', {
                name: 'action-menu',
                elem: '#material-actions'
            }, function(err,res) {
                assert.ifError(err);
            })
            .pause(PAUSE_TIME)
            .click('#material-edit')
            .pause(PAUSE_TIME)
            .webdrivercss('portfolio-edit-2', {
                name: 'right-sidebar',
                elem: '#sidebar-right'
            }, function(err,res) {
                assert.ifError(err);
            })
            .webdrivercss('portfolio-edit-2', {
                name: 'left-sidebar',
                elem: '#sidebar-left'
            }, function(err,res) {
                assert.ifError(err);
            })
            .webdrivercss('portfolio-edit-2', {
                name: 'main-content',
                elem: '#main-content'
            }, function(err,res) {
                assert.ifError(err);
            })
            .url()
            .call(done)
    });

    it('Compares material create view visually', function (done) {
        browser
            .url('https://localhost/')
            .pause(PAUSE_TIME)
            .moveToObject('#add-portfolio')
            .pause(PAUSE_TIME)
            .click("#add-material")
            .pause(PAUSE_TIME)
            .setValue('#add-material-url-input', 'https://github.com/webdriverio/wdio-angular-service')
            .setValue('#add-material-title-input', 'https://github.com/webdriverio/wdio-angular-service')
            .setValue('#add-material-description-input', 'https://github.com/webdriverio/wdio-angular-service')
            .webdrivercss('material-modal-1', {
                name: 'step-1',
                elem: '#add-material-modal'
            }, function(err,res) {
                assert.ifError(err);
            })
            .click('#add-material-next-step')
            .pause(PAUSE_TIME)
            .webdrivercss('material-modal-2', {
                name: 'step-2',
                elem: '#add-material-modal'
            }, function(err,res) {
                assert.ifError(err);
            })
            .click('#add-material-next-step')
            .pause(PAUSE_TIME)

            .webdrivercss('material-modal-3', {
                name: 'step-3',
                elem: '#add-material-modal'
            }, function(err,res) {
                assert.ifError(err);
            })
            .call(done);
    });

});
