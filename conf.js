
exports.config = {

    allScriptsTimeout: 11000,

    capabilities: {
        browserName: 'chrome',

        chromeOptions: {
            args: [ "--headless", "--disable-gpu", "--window-size=1920,1080" ]
        }
    },

    directConnect: true,

    jasmineNodeOpts: {
        showColors: true,
        defaultTimeoutInterval: 30000
    },

    onPrepare: function() {
        jasmine.getEnv()
            .addReporter(require('./spec/helpers/points-reporter').reporter);
    }

};
