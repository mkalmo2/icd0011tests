'use strict';

function extendIt(oldIt) {
    return function (description, testFunction) {
        var regEx1 = /^(function )?\(([^)]+)\)/;
        var regEx2 = /^\w+\s*=>/;
        var hasArguments = testFunction.toString().match(regEx1)
            || testFunction.toString().match(regEx2);

        return {
            deductedOnFailure: hasArguments
                ? getSpecWithDone : getSpecWithoutDone
        };

        function getSpecWithDone(deduction) {
            var spec = oldIt(description, function(done) {
                spec.result.deduction = deduction;
                testFunction(done);
            });
        }

        function getSpecWithoutDone(deduction) {
            var spec = oldIt(description, function() {
                spec.result.deduction = deduction;
                testFunction();
            });
        }

    };
}

function reporter() {
    var counter = new PointCounter(10);
    var allTestsEnabled = true;

    return {
        specDone: function(result) {
            if (result.status !== 'passed') {
                counter.subtract(result.deduction);
            }
            if (result.status === 'pending' || result.status === 'disabled') {
                allTestsEnabled = false;
            }
        },

        suiteDone: function(result) {
            if (allTestsEnabled) {
                console.log('\nRESULT: ' + counter.getResult() + ' POINTS');
            }
        }
    };

    function PointCounter(maxPoints) {
        var count = maxPoints;

        this.getResult = function() {
            return Math.max(0, count);
        };

        this.subtract = function(howMany) {
            count -= howMany;
        };
    }
}

module.exports = { extendIt: extendIt, reporter: reporter() };
