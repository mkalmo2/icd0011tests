exports.config = {
    seleniumAddress: 'http://localhost:4444/wd/hub',

    capabilities: {
        'browserName': 'phantomjs'
    },

    specs: ['hw6-spec.js'],

    // useAllAngular2AppRoots: true,

    jasmineNodeOpts: {
        showColors: true
    }
};
