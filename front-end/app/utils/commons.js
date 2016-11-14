/**
 * Common constants and functions.
 *
 * @author Jordan Silva
 *
 */
var LOGIN_ORIGIN = "loginOrigin";

function log() {
    if (console && console.log) {
        console.log.apply(console, arguments);
    }
}

function isDefined(value) {
    return angular.isDefined(value) && value != null;
}

if (typeof String.prototype.startsWith === 'undefined') {
    String.prototype.startsWith = function (value, searchValue) {
        return value.indexOf(searchValue) === 0;
    };
}

if (typeof String.prototype.contains === 'undefined') {
    String.prototype.contains = function (it) {
        return this.indexOf(it) != -1;
    };
}

function isEmpty(str) {
    if (!str || str === undefined) {
        return true;
    }

    if (typeof str != 'String') {
        return false;
    }

    return str.trim().length === 0;
}

/**
 * Converts a string date into Date object. String format must be 'dd.MM.yyyy'
 * @param dateString
 * @returns {Date}
 */
function stringToDate(dateString) {
    var date = dateString.split(".");
    return new Date(date[2], date[1] - 1, date[0]);
}

/**
 * Compares two dates. If dates are in String format, it is converted to Date. String format must be 'dd.MM.yyyy'.
 * @param date1
 * @param date2
 * @return a negative integer, zero, or a positive integer as the
 *         first argument is less than, equal to, or greater than the
 *         second.
 */
function compareDate(date1, date2) {
    if (typeof date1 === 'string') {
        date1 = stringToDate(date1);
    }

    if (typeof date2 === 'string') {
        date2 = stringToDate(date2);
    }

    return date1 < date2 ? -1 : date1 > date2 ? 1 : 0;
}

/**
 * Copies the first object to the second
 */
function copyObject(first, second) {
    clearObject(second);

    for (var k in first) {
        second[k] = first[k];
    }
}

/**
 * Clear an object, deleting all its properties
 */
function clearObject(object) {
    for (prop in object) {
        if (object.hasOwnProperty(prop)) {
            delete object[prop];
        }
    }
}

/**
 * Check if complex item (element) is in an array using comparator
 */
Array.prototype.indexOfWithComparator = function (obj, comparator) {
    for (var i = 0; i < this.length; i++) {
        if (comparator(obj, this[i]) === 0) {
            return i;
        }
    }

    return -1;
};

/**
 * Gets the string in the correct language
 * @return the string in the correct language
 */
function getUserDefinedLanguageString(values, userLanguage, materialLanguage) {
    if (!values || values.length === 0) {
        return;
    }

    var languageStringValue;

    if (values.length === 1) {
        languageStringValue = values[0].text;
    } else {
        languageStringValue = getLanguageString(values, userLanguage);
        if (!languageStringValue) {
            languageStringValue = getLanguageString(values, materialLanguage);
            if (!languageStringValue) {
                languageStringValue = values[0].text;
            }
        }
    }

    return languageStringValue;
}

/**
 * Gets the text if it exists in the specified language.
 * @return the queryed text.
 */
function getLanguageString(values, language) {
    if (!language) {
        return null;
    }

    for (var i = 0; i < values.length; i++) {
        if (values[i].language === language) {
            return values[i].text;
        }
    }
}

function formatIssueDate(issueDate) {
    if (!issueDate) {
        return;
    }

    if (issueDate.day && issueDate.month && issueDate.year) {
        // full date
        return formatDay(issueDate.day) + "." + formatMonth(issueDate.month) + "." + formatYear(issueDate.year);
    } else if (issueDate.month && issueDate.year) {
        // month date
        return formatMonth(issueDate.month) + "." + formatYear(issueDate.year);
    } else if (issueDate.year) {
        // year date
        return formatYear(issueDate.year);
    }
}

function issueDateToDate(issueDate) {
    if (!issueDate) {
        return;
    }

    if (issueDate.day && issueDate.month && issueDate.year) {
        // full date
        return new Date(issueDate.year, issueDate.month - 1, issueDate.day);
    } else if (issueDate.month && issueDate.year) {
        // month date
        return new Date(issueDate.year, issueDate.month - 1, 1);
    } else if (issueDate.year) {
        // year date
        return new Date(issueDate.year, 0, 1);
    }
}

function formatDay(day) {
    return day > 9 ? "" + day : "0" + day;
}

function formatMonth(month) {
    return month > 9 ? "" + month : "0" + month;
}

function formatYear(year) {
    return year < 0 ? year * -1 : year;
}

function formatDateToDayMonthYear(dateString) {
    var date = new Date(dateString);
    return formatDay(date.getDate()) + "." + formatMonth(date.getMonth() + 1) + "." + date.getFullYear();
}

function arrayToInitials(array) {
    var res = "";
    for (var i = 0; i < array.length; i++) {
        res += wordToInitial(array[i]) + " ";
    }

    return res.trim();
}

function wordToInitial(name) {
    return name.charAt(0).toUpperCase() + ".";
}

function formatNameToInitials(name) {
    if (name) {
        return arrayToInitials(name.split(" "));
    }
}

function formatSurnameToInitialsButLast(surname) {
    if (!surname) {
        return;
    }

    var array = surname.split(" ");
    var last = array.length - 1;
    var res = "";

    if (last > 0) {
        res = arrayToInitials(array.slice(0, last)) + " ";
    }

    res += array[last];
    return res;
}

function isIdCodeValid(idCode) {
    if (!idCode || idCode.length !== 11) {
        return false;
    }

    var controlCode;

    var firstWeights = [1, 2, 3, 4, 5, 6, 7, 8, 9, 1];
    var secondWeights = [3, 4, 5, 6, 7, 8, 9, 1, 2, 3];

    var firstSum = 0;
    for (i = 0; i < 10; i++) {
        firstSum += idCode.charAt(i) * firstWeights[i];
    }

    if (firstSum % 11 !== 10) {
        controlCode = firstSum % 11;
    } else {
        // Calculate second sum using second set of weights
        var secondSum = 0;
        for (i = 0; i < 10; i++) {
            secondSum += idCode.charAt(i) * secondWeights[i];
        }

        if (secondSum % 11 !== 10) {
            controlCode = secondSum % 11;
        } else {
            controlCode = 0;
        }
    }

    return idCode[10] == controlCode;
}

function containsObject(obj, list) {
    var x;
    for (x in list) {
        if (list.hasOwnProperty(x) && (list[x] === obj || list[x].__proto__ === obj.__proto__)) {
            return true;
        }
    }

    return false;
}

function createPortfolio(id) {
    var portfolio = {
        type: ".Portfolio",
        id: id,
        title: "",
        summary: "",
        taxon: null,
        targetGroups: [],
        tags: []
    };
    return portfolio;
}

function sortTags(upVoteForms) {
    if (upVoteForms) {
        return upVoteForms.sort(function (a, b) {
            return b.upVoteCount - a.upVoteCount;
        });
    }
}

function containsMaterial(materials, selectedMaterial) {
    for (var i = 0; i < materials.length; i++) {
        var material = materials[i];
        if (material.id == selectedMaterial.id) {
            return true;
        }
    }
    return false;
}

function isObjectEmpty(obj) {
    return Object.keys(obj).length === 0 && JSON.stringify(obj) === JSON.stringify({});
}

// This is a workaround for angular circular reference problem. It is not efficient.
function clone(object) {
    var json = JSOG.stringify(object);
    return JSOG.parse(json)
}

function getSource(material) {
    if (material.source && (material.source !== null && material.source !== undefined && material.source !== "")) {
        return material.source;
    }
    if (material && material.uploadedFile) {
        return material.uploadedFile.url;
    }
    return null;
}

/**
 * Check if list of objects contains element
 * @param list
 * @param field
 * @param value
 * @returns {boolean}
 */
function listContains(list, field, value) {
    return list.filter(function (e) {
            return e[field] === value;
        }).length > 0
}


