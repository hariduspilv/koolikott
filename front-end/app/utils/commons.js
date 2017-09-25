/**
 * Common constants and functions.
 *
 * @author Jordan Silva
 *
 */
const LOGIN_ORIGIN = "loginOrigin";
const BREAK_XS = 600;
const BREAK_SM = 960;
const BREAK_LG = 1280;

function isMobile() {
    return window.innerWidth < BREAK_XS;
}

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

// https://tc39.github.io/ecma262/#sec-array.prototype.includes
if (!Array.prototype.includes) {
    Object.defineProperty(Array.prototype, 'includes', {
        value: function (searchElement, fromIndex) {

            // 1. Let O be ? ToObject(this value).
            if (this == null) {
                throw new TypeError('"this" is null or not defined');
            }

            var o = Object(this);

            // 2. Let len be ? ToLength(? Get(O, "length")).
            var len = o.length >>> 0;

            // 3. If len is 0, return false.
            if (len === 0) {
                return false;
            }

            // 4. Let n be ? ToInteger(fromIndex).
            //    (If fromIndex is undefined, this step produces the value 0.)
            var n = fromIndex | 0;

            // 5. If n ≥ 0, then
            //  a. Let k be n.
            // 6. Else n < 0,
            //  a. Let k be len + n.
            //  b. If k < 0, let k be 0.
            var k = Math.max(n >= 0 ? n : len - Math.abs(n), 0);

            // 7. Repeat, while k < len
            while (k < len) {
                // a. Let elementK be the result of ? Get(O, ! ToString(k)).
                // b. If SameValueZero(searchElement, elementK) is true, return true.
                // c. Increase k by 1.
                // NOTE: === provides the correct "SameValueZero" comparison needed here.
                if (o[k] === searchElement) {
                    return true;
                }
                k++;
            }

            // 8. Return false
            return false;
        }
    });
}

// https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/String/repeat
if (!String.prototype.repeat) {
    String.prototype.repeat = function (count) {
        'use strict';
        if (this == null) {
            throw new TypeError('can\'t convert ' + this + ' to object');
        }
        var str = '' + this;
        count = +count;
        if (count != count) {
            count = 0;
        }
        if (count < 0) {
            throw new RangeError('repeat count must be non-negative');
        }
        if (count == Infinity) {
            throw new RangeError('repeat count must be less than infinity');
        }
        count = Math.floor(count);
        if (str.length == 0 || count == 0) {
            return '';
        }
        // Ensuring count is a 31-bit integer allows us to heavily optimize the
        // main part. But anyway, most current (August 2014) browsers can't handle
        // strings 1 << 28 chars or longer, so:
        if (str.length * count >= 1 << 28) {
            throw new RangeError('repeat count must not overflow maximum string size');
        }
        var rpt = '';
        for (; ;) {
            if ((count & 1) == 1) {
                rpt += str;
            }
            count >>>= 1;
            if (count == 0) {
                break;
            }
            str += str;
        }
        // Could we try:
        // return Array(count + 1).join(this);
        return rpt;
    }
}


function isEmpty(str) {
    if (!str || str === undefined) {
        return true;
    }

    if (typeof str != 'string') {
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
    Object.keys(object).forEach(key => delete object[key]);
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

function getSource(material) {
    if (!material) return;
    if (material.source) {
        return material.source;
    } else if (material.uploadedFile) {
        /*
         Server requests are in iso-8859-1 therefore data is also in iso-8859-1, this can be decoded
         with decodeURIComponent(escape()); then we again encode it in java to utf-8 (actually a mistake)
         which results in another decodeURIComponent();
         */
        return decodeURIComponent(escape(decodeURIComponent(material.uploadedFile.url)));
    }
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

/**
 * Move element in list from old_index to new_index
 * @param old_index
 * @param new_index
 */
Array.prototype.move = function (old_index, new_index) {
    while (old_index < 0) {
        old_index += this.length;
    }
    while (new_index < 0) {
        new_index += this.length;
    }
    if (new_index >= this.length) {
        var k = new_index - this.length;
        while ((k--) + 1) {
            this.push(undefined);
        }
    }
    this.splice(new_index, 0, this.splice(old_index, 1)[0]);
};

function isYoutubeVideo(url) {
    // regex taken from http://stackoverflow.com/questions/2964678/jquery-youtube-url-validation-with-regex #ULTIMATE YOUTUBE REGEX
    var youtubeUrlRegex = /^(?:https?:\/\/)?(?:www\.)?(?:youtu\.be\/|youtube\.com\/(?:embed\/|v\/|watch\?v=|watch\?.+&v=))((\w|-){11})(?:\S+)?$/;
    return url && url.match(youtubeUrlRegex);
}

function isSlideshareLink(url) {
    var slideshareUrlRegex = /^https?\:\/\/www\.slideshare\.net\/[a-zA-Z0-9\-]+\/[a-zA-Z0-9\-]+$/;
    return url && url.match(slideshareUrlRegex);
}

function isVideoLink(url) {
    if (!url) return;
    var extension = url.split('.').pop().toLowerCase();
    return extension == "mp4" || extension == "ogv" || extension == "webm";
}

function isAudioLink(url) {
    if (!url) return;
    var extension = url.split('.').pop().toLowerCase();
    return extension == "mp3" || extension == "ogg" || extension == "wav";
}

function isPictureLink(url) {
    if (!url) return;
    var extension = url.split('.').pop().toLowerCase();
    return extension == "jpg" || extension == "jpeg" || extension == "png" || extension == "gif";
}

function isEbookLink(url) {
    if (!url) return;
    var extension = url.split('.').pop().toLowerCase();
    return extension == "epub";
}

function isPDFLink(url) {
    if (!url) return;
    var extension = url.split('.').pop().toLowerCase();
    return extension == "pdf";
}

function matchType(type) {
    if (isYoutubeVideo(type)) {
        return 'YOUTUBE';
    } else if (isSlideshareLink(type)) {
        return 'SLIDESHARE';
    } else if (isVideoLink(type)) {
        return 'VIDEO';
    } else if (isAudioLink(type)) {
        return 'AUDIO';
    } else if (isPictureLink(type)) {
        return 'PICTURE';
    } else if (isEbookLink(type)) {
        return 'EBOOK';
    } else if (isPDFLink(type)) {
        return 'PDF';
    } else {
        return 'LINK';
    }
}

function isYoutubeVideo(url) {
    // regex taken from http://stackoverflow.com/questions/2964678/jquery-youtube-url-validation-with-regex #ULTIMATE YOUTUBE REGEX
    var youtubeUrlRegex = /^(?:https?:\/\/)?(?:www\.)?(?:youtu\.be\/|youtube\.com\/(?:embed\/|v\/|watch\?v=|watch\?.+&v=))((\w|-){11})(?:\S+)?$/;
    return url && url.match(youtubeUrlRegex);
}

function isSlideshareLink(url) {
    var slideshareUrlRegex = /^https?\:\/\/www\.slideshare\.net\/[a-zA-Z0-9\-]+\/[a-zA-Z0-9\-]+$/;
    return url && url.match(slideshareUrlRegex);
}

function isVideoLink(url) {
    if (!url) return;
    var extension = url.split('.').pop().toLowerCase();
    return extension == "mp4" || extension == "ogv" || extension == "webm";
}

function isAudioLink(url) {
    if (!url) return;
    var extension = url.split('.').pop().toLowerCase();
    return extension == "mp3" || extension == "ogg" || extension == "wav";
}

function isPictureLink(url) {
    if (!url) return;
    var extension = url.split('.').pop().toLowerCase();
    return extension == "jpg" || extension == "jpeg" || extension == "png" || extension == "gif";
}

function isEbookLink(url) {
    if (!url) return;
    var extension = url.split('.').pop().toLowerCase();
    return extension == "epub";
}

function isPDFLink(url) {
    if (!url) return;
    var extension = url.split('.').pop().toLowerCase();
    return extension == "pdf";
}

function isIE() {
    return (navigator.appName == 'Microsoft Internet Explorer' || !!(navigator.userAgent.match(/Trident/) ||
    navigator.userAgent.match(/rv 11/)));
}

function focusInput(elementID) {
    const $parent = angular.element(document.getElementById(elementID));
    $parent.find('input')[0].focus();
}

function isMaterial(type) {
    return type === ".Material" || type === ".ReducedMaterial"
}

function isPortfolio(type) {
    return type === ".Portfolio" || type === ".ReducedPortfolio"
}

function stripHtml(htmlString) {
    let tmp = document.createElement("div");
    tmp.innerHTML = htmlString;
    return tmp.textContent || tmp.innerText || "";
}

function countOccurrences(value, text) {
    let count = 0;
    let index = text.indexOf(value);
    while (index !== -1) {
        count++;
        index = text.indexOf(value, index + 1);
    }

    return count;
}

function decodeUTF8(string){
    return decodeURIComponent(escape(decodeURIComponent(string)));
}

if (typeof localStorage === 'object') {
    try {
        localStorage.setItem('localStorage', 1);
        localStorage.removeItem('localStorage');
    } catch (e) {
        var tmp_storage = {};
        var p = '__unique__';  // Prefix all keys to avoid matching built-ins
        Storage.prototype.setItem = function(k, v){
            tmp_storage[p + k] = v;
        };
        Storage.prototype.getItem = function(k){
            return tmp_storage[p + k] === undefined ? null : tmp_storage[p + k];
        };
        Storage.prototype.removeItem = function(k){
            delete tmp_storage[p + k];
        };
        Storage.prototype.clear = function(){
            tmp_storage = {};
        };
        alert('Your web browser does not support storing settings locally. In Safari, the most common cause of this is using "Private Browsing Mode". Some settings may not save or some features may not work properly for you.');
    }
}
