exports.config = {
    seleniumAddress: 'http://localhost:4444/wd/hub',

    capabilities: {
        'browserName': 'phantomjs',
        'phantomjs.cli.args': ['--ignore-ssl-errors=true']
    },

    specs: ['hw6-spec.js'],

    // useAllAngular2AppRoots: true,

    jasmineNodeOpts: {
        showColors: true
    },

    onPrepare: function() {
        jasmine.getEnv().addReporter(getReporter());
    }

};

function getReporter() {
    return {
        suiteDone: function(result) {
            console.log('Suite ' + result.description + ' done');
        }
    };
}
