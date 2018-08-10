'use strict';

var BASE_URL = 'http://localhost:8080/';

describe('Application part 7', function () {

    it = extendIt(it); fit = extendIt(fit); xit = extendIt(xit);

    beforeEach(function() {
        // goTo(BASE_URL);
        goTo('http://localhost:8080/ex4.html');
    });

    fit('hello', function () {

        input('input1').setValue('hello');

        link('button1').click();

        expectTextOnPage('hello1');

    }).deductedOnFailure(10);

    function expectTextOnPage(text) {
        var until = protractor.ExpectedConditions;
        var body = element(by.tagName('body'));
        var message = 'Could not find text: ' + text;
        browser.wait(until.textToBePresentInElement(body, text), 200, message);
    }
    
    
    it('menu links should change url', function () {

        expect(currentUrl()).toBe(getUrl('#/search'));

        link('menu-new').click();

        expect(currentUrl()).toBe(getUrl('#/new'));

        link('menu-search').click();

        expect(currentUrl()).toBe(getUrl('#/search'));

    }).deductedOnFailure(10);

    it('should insert customer with name, code and type', function () {

        link('menu-new').click();

        var sampleData = getSampleData();

        input('first-name').setValue(sampleData.firstName);
        input('last-name').setValue(sampleData.lastName);
        input('code').setValue(sampleData.code);
        select('type').setValue('customer_type.private');

        link('save-link').click();

        expect(currentUrl()).toBe(getUrl('#/search'));

        expect(element(by.tagName('body')).getText()).toContain(sampleData.firstName);
        expect(element(by.tagName('body')).getText()).toContain(sampleData.lastName);
        expect(element(by.tagName('body')).getText()).toContain(sampleData.code);

    }).deductedOnFailure(10);

    it('should have a link for deleting inserted users', function () {

        deleteAllData();

        var sampleData = getSampleData();
        insertCustomer(sampleData.firstName, sampleData.lastName, sampleData.code);
        insertCustomer('Mari', 'Kuusk', 'C1');

        link('delete-button-' + sampleData.code).click();

        expect(element(by.tagName('body')).getText()).toContain('Mari');
        expect(element(by.tagName('body')).getText()).not.toContain(sampleData.lastName);

    }).deductedOnFailure(2);

    it('should allow adding and deleting phones', function () {

        link('menu-new').click();

        link('add-phone-link').click();
        expect(select('phone-select-0').isPresent()).toBe(true);
        expect(input('phone-value-0').isPresent()).toBe(true);

        link('add-phone-link').click();
        expect(select('phone-select-1').isPresent()).toBe(true);
        expect(input('phone-value-1').isPresent()).toBe(true);
        input('phone-value-1').setValue('123');

        link('delete-phone-link-0').click();
        expect(input('phone-value-0').getValue()).toBe('123');

    }).deductedOnFailure(10);

    it('should filter by first name, last name and code', function () {

        deleteAllData();

        insertCustomer('Mari', 'Kuusk', 'C1');
        insertCustomer('Jaak', 'Varres', 'C2');
        insertCustomer('Tiina', 'Kask', 'AR2');
        insertCustomer('Mati', 'Tamm', 'C3');

        input('search-string').setValue('ar');

        expect(getFirstNamesFromList()).toEqual(['Mari', 'Jaak', 'Tiina']);

    }).deductedOnFailure(1);

    it('should show errors when submitting invalid data', function () {

        deleteAllData();

        insertCustomer('x', undefined, '*bad_code*');

        getErrorCodes().then(function (codes) {
            expect(codes.length).toBe(3);
            expect(codes).toContain('NotNull');
            expect(codes).toContain('Size');
            expect(codes).toContain('Pattern');
        });

    }).deductedOnFailure(1);


});






// Different helpers

function getFirstNamesFromList() {
    return element.all(by.tagName('td'))
        .filter(function (each) {
            return each.getAttribute('id').then(function (id) {
                return id && id.indexOf('first-name-') >= 0;
            })
        }).map(function (each) {
            return each.getText();
        });
}

function getErrorCodes() {
    return element(by.id('message-block')).all(by.tagName('li'))
        .map(function (each) {
            return each.getAttribute('data-code').then(function (code) {
                return code.replace(/\..*$/, '');
            })
        });
}

function insertCustomer(firstName, lastName, code) {
    link('menu-new').click();
    input('first-name').setValue(firstName);
    input('last-name').setValue(lastName);
    input('code').setValue(code);
    link('save-link').click();
}

function getSampleData() {
    var time = new Date().getTime();
    return {
        firstName : time,
        lastName : time + 1,
        code : time + 2
    };
}

function currentUrl() {
    return browser.getCurrentUrl();
}

function link(id) {
    return element(by.id(id));
}

function goTo(addr) {
    browser.get(addr);
}

function select(id) {
    var select = element(by.id(id));

    return {
        setValue: setValue,
        hasOptionValue: hasOptionValue,
        isPresent: isPresent
    };

    function isPresent() {
        return select.isPresent();
    }

    function hasOptionValue(value) {
        expect(select.all(by.tagName('option')).map(function (each) {
            return each.getAttribute('value').then(function (value) {
                return value.replace('.*:', '');
            });
        })).toContain(value);
    }

    function setValue(value) {
        select.element(by.css('[value*="' + value + '"]')).click();
    }
}

function input(id) {
    var input = element(by.id(id));

    return {
        getValue: getValue,
        setValue: setValue,
        isPresent : isPresent
    };

    function isPresent() {
        return input.isPresent();
    }

    function setValue(value) {
        if (value == null || value == undefined) {
            return;
        }

        input.clear().sendKeys(value);
    }

    function getValue() {
        return input.getAttribute('value');
    }
}

function getUrl(path) {
    return BASE_URL.replace(/\/$/, '') + '/' + path;
}

function deleteAllData() {
    protractor.promise.controlFlow().execute(deleteAllDataSub);
}

function deleteAllDataSub() {

    var request = require('request');

    var defer = protractor.promise.defer();
    request.delete(
        getUrl('api/customers'),
        function (error, response, body) {
            if (!error && response.statusCode == 200) {
                defer.fulfill();
            } else {
                defer.reject(error);
            }
        }
    );

    return defer.promise;
}

function extendIt(it) {
    return require('./helpers/points-reporter').extendIt(it);
}
