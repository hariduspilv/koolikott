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
const EMBEDDABLE_IMAGE_FILE_EXTENSIONS = ['jpg', 'jpeg', 'png', 'gif']
const EMBEDDABLE_AUDIO_FILE_EXTENSIONS = ['mp3', 'ogg', 'wav']
const EMBEDDABLE_VIDEO_FILE_EXTENSIONS = ['mp4', 'ogv', 'webm']
const EMBEDDABLE_MEDIA_FILE_EXTENSIONS = EMBEDDABLE_IMAGE_FILE_EXTENSIONS.concat(EMBEDDABLE_AUDIO_FILE_EXTENSIONS, EMBEDDABLE_VIDEO_FILE_EXTENSIONS)
const EDITOR_ALLOWED_BLOCK_LEVEL_TAGS = ['H3', 'P', 'UL', 'LI', 'BLOCKQUOTE', 'DIV']
const EDITOR_ALLOWED_TAGS_AND_ATTRIBUTES = {
    A: ['href', 'target'],
    DIV: ['class', 'data-id', 'data-src'],
    H3: ['class', 'id'],
    P: [],
    UL: [],
    LI: [],
    BLOCKQUOTE: [],
    B: [],
    I: [],
    STRONG: [],
    EM: [],
    BR: []
}
const EDITOR_ALLOWED_TAGS = Object.keys(EDITOR_ALLOWED_TAGS_AND_ATTRIBUTES)
const EDITOR_FORBIDDEN_TAGS = ['meta', 'script', 'link', 'style', 'img', 'map', 'audio', 'video', 'track', 'applet', 'embed', 'object', 'param', 'source', 'canvas', 'noscript']

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

    for (let k in first) {
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
    if (!values || values.length === 0) return;


    let languageStringValue;

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
    if (!language) return null;
    for (let i = 0; i < values.length; i++) {
        if (values[i].language === language) {
            return values[i].text;
        }
    }
}

function formatIssueDate(issueDate) {
    if (!issueDate) return;
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
    return isNaN(date)
        ? ''
        : formatDay(date.getDate()) + "." + formatMonth(date.getMonth() + 1) + "." + date.getFullYear();
}

function arrayToInitials(array) {
    let res = ""
    for (let i = 0; i < array.length; i++) {
        res += wordToInitial(array[i]) + " ";
    }

    return res.trim();
}

function wordToInitial(name) {
    return name.charAt(0).toUpperCase() + ".";
}

function formatNameToInitials(name) {
    if (name) return arrayToInitials(name.split(" "));
}

function formatSurnameToInitialsButLast(surname) {
    if (!surname) return;

    let array = surname.split(" ")
    let res = ""
    const last = array.length - 1

    if (last > 0) res = arrayToInitials(array.slice(0, last)) + " ";

    res += array[last];
    return res;
}

function isIdCodeValid(idCode) {
    if (!idCode || idCode.length !== 11) return false;

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
    for (let x in list) {
        if (list.hasOwnProperty(x) && (list[x] === obj || list[x].__proto__ === obj.__proto__)) return true;
    }
    return false;
}

function createPortfolio(id) {
    return {
        type: ".Portfolio",
        id: id,
        title: "",
        summary: "",
        taxon: null,
        targetGroups: [],
        tags: [],
        licenseType: "",
        picture: {}
    };
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
        return decodeUTF8(material.uploadedFile.url);
    }
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
    const youtubeUrlRegex = /^(?:https?:\/\/)?(?:www\.)?(?:youtu\.be\/|youtube\.com\/(?:embed\/|v\/|watch\?v=|watch\?.+&v=))((\w|-){11})(?:\S+)?$/
    return url && url.match(youtubeUrlRegex);
}

function isSlideshareLink(url) {
    const slideshareUrlRegex = /^https?:\/\/www\.slideshare\.net\/[a-zA-Z0-9\-]+\/[a-zA-Z0-9\-]+$/
    return url && url.match(slideshareUrlRegex);
}

function isVideoLink(url) {
    return url && ["mp4", "ogv", "webm"].includes(url.split('.').pop().toLowerCase());
}

function isAudioLink(url) {
    return url && ["mp3", "ogg", "wav"].includes(url.split('.').pop().toLowerCase());
}

function isPictureLink(url) {
    return url && ["jpg", "jpeg", "png", "gif"].includes(url.split('.').pop().toLowerCase());
}

function isEbookLink(url) {
    return url && url.split('.').pop().toLowerCase() === "epub";
}

function isPDFLink(url) {
    return url && url.split('.').pop().toLowerCase() === "pdf";
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

function isIE() {
    return (navigator.appName == 'Microsoft Internet Explorer' || !!(navigator.userAgent.match(/Trident/) ||
    navigator.userAgent.match(/rv 11/)));
}

function focusInput(elementID) {
    const $parent = angular.element(document.getElementById(elementID));
    $parent.find('input')[0].focus();
}

function isMaterial(type) {
    return type === ".Material" || type === ".ReducedMaterial" || type === ".AdminMaterial"
}

function isPortfolio(type) {
    return type === ".Portfolio" || type === ".ReducedPortfolio" || type === ".AdminPortfolio"
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

/**
 *
 * Server requests are in iso-8859-1 therefore data is also in iso-8859-1, this can be decoded
 * with decodeURIComponent(escape()); then we again encode it in java to utf-8 (actually a mistake)
 * which results in another decodeURIComponent();
 */
function decodeUTF8(string) {
    return decodeURIComponent(escape(decodeURIComponent(string)));
}

if (typeof localStorage === 'object') {
    try {
        localStorage.setItem('localStorage', 1);
        localStorage.removeItem('localStorage');
    } catch (e) {
        var tmp_storage = {};
        var p = '__unique__';  // Prefix all keys to avoid matching built-ins
        Storage.prototype.setItem = function (k, v) {
            tmp_storage[p + k] = v;
        };
        Storage.prototype.getItem = function (k) {
            return tmp_storage[p + k] === undefined ? null : tmp_storage[p + k];
        };
        Storage.prototype.removeItem = function (k) {
            delete tmp_storage[p + k];
        };
        Storage.prototype.clear = function () {
            tmp_storage = {};
        };
        alert('Your web browser does not support storing settings locally. In Safari, the most common cause of this is using "Private Browsing Mode". Some settings may not save or some features may not work properly for you.');
    }
}

/**
 * Base class for component/directive controllers.
 * It auto-assigns all injected dependendcies as instace properties.
 *
 * Usage:
 *      class MyController extends Controller {
 *          $onInit() {
 *              // this.$scope and this.$timeout are now available
 *          }
 *      }
 *      MyController.$inject = ['$scope', '$timeout']
 *
 *      angular.module('myModule').component('myComponent', {
 *          bindings: {},
 *          templateUrl: '...',
 *          controller: MyController
 *      })
 */
class Controller {
    constructor() {
        if (Array.isArray(this.constructor.$inject))
            this.constructor.$inject.forEach((name, idx) =>
                this[name] = arguments[idx]
            )
    }
    isMaterial({ type }) {
        return type === '.Material' || type === '.ReducedMaterial' || type === '.AdminMaterial'
    }
    isPortfolio({ type }) {
        return type === '.Portfolio' || type === '.ReducedPortfolio' || type === '.AdminPortfolio'
    }
    getCorrectLanguageTitle({ title, titles, language } = {}) {
        return !this.dependencyExists('translationService')
            ? ''
            : title || titles && this.getUserDefinedLanguageString(
                titles,
                this.translationService.getLanguage(),
                language
            )
    }
    getUrl(learningObject) {
        if (this.isMaterial(learningObject)) {
            return 'material?name=' + this.getCorrectLanguageTitle(learningObject)
        }
        else
            return 'portfolio?name=' + learningObject.titleForUrl + '&id=' + learningObject.id
    }
    getUserDefinedLanguageString(values, userLanguage, materialLanguage) {
        if (!values || values.length === 0)
            return

        if (values.length === 1)
            return values[0].text

        let languageStringValue = this.getLanguageString(values, userLanguage)

        if (!languageStringValue) {
            languageStringValue = this.getLanguageString(values, materialLanguage)

            if (!languageStringValue)
                languageStringValue = values[0].text
        }

        return languageStringValue
    }
    getLanguageString(values, language) {
        if (!language) return null

        for (let i = 0; i < values.length; i++)
            if (values[i].language === language) return values[i].text
    }
    formatNameToInitials(name) {
        if (name)
            return this.arrayToInitials(name.split(' '))
    }
    formatSurnameToInitialsButLast(surname) {
        if (!surname) return

        const array = surname.split(' ')
        let last = array.length - 1
        let res = ''

        if (last > 0) res = this.arrayToInitials(array.slice(0, last)) + ' '

        res += array[last]
        return res
    }
    arrayToInitials(array) {
        let res = ''

        for (let i = 0; i < array.length; i++)
            res += array[i].charAt(0).toUpperCase() + '. '

        return res.trim()
    }
    createPortfolio(id) {
        return {
            id,
            type: '.Portfolio',
            title: '',
            summary: '',
            taxon: null,
            targetGroups: [],
            tags: [],
            chapters: []
        }
    }
    /**
     * @todo Is that decodeUTF8 really necessary?
     */
    getMaterialSource(material) {
        return material && material.source || material.uploadedFile && decodeUTF8(material.uploadedFile.url)
    }
    isYoutubeLink(url) {
        // regex taken from http://stackoverflow.com/questions/2964678/jquery-youtube-url-validation-with-regex #ULTIMATE YOUTUBE REGEX
        const youtubeUrlRegex = /^(?:https?:\/\/)?(?:www\.)?(?:youtu\.be\/|youtube\.com\/(?:embed\/|v\/|watch\?v=|watch\?.+&v=))((\w|-){11})(?:\S+)?$/
        return url && youtubeUrlRegex.test(url)
    }
    isSoundcloudLink(url) {
        return url && /^https?:\/\/(soundcloud\.com)\/(.*)$/.test(url)
    }
    isSlideshareLink(url) {
        const slideshareUrlRegex = /^https?\:\/\/www\.slideshare\.net\/[a-zA-Z0-9\-]+\/[a-zA-Z0-9\-]+$/
        return url && url.match(slideshareUrlRegex)
    }
    isVideoLink(url) {
        return url && ["mp4", "ogv", "webm"].includes(url.split('.').pop().toLowerCase())
    }
    isAudioLink(url) {
        return url && ["mp3", "ogg", "wav"].includes(url.split('.').pop().toLowerCase())
    }
    isPictureLink(url) {
        return url && ["jpg", "jpeg", "png", "gif"].includes(url.split('.').pop().toLowerCase())
    }
    isEbookLink(url) {
        return url && url.split('.').pop().toLowerCase() === "epub"
    }
    isPDFLink(url) {
        return url && url.split('.').pop().toLowerCase() === "pdf"
    }
    getEmbeddedMaterialType({ source, uploadedFile, embedSource }) {
        if (!source && !uploadedFile)
            return

        const url = source || uploadedFile.url
        switch (true) {
            case this.isYoutubeLink(url): return 'YOUTUBE'
            case this.isSlideshareLink(url): return 'SLIDESHARE'
            case this.isVideoLink(url): return 'VIDEO'
            case this.isAudioLink(url): return 'AUDIO'
            case this.isPictureLink(url): return 'PICTURE'
            case this.isEbookLink(url): return 'EBOOK'
            case this.isPDFLink(url): return 'PDF'
            case !!embedSource: return 'EMBEDSOURCE'
        }
    }
    isAcceptedEmbeddableMediaLink(url) {
        if (this.isYoutubeLink(url) || this.isSoundcloudLink(url))
            return true

        const extension = url.split('.').pop()

        return !!extension && EMBEDDABLE_MEDIA_FILE_EXTENSIONS.indexOf(extension) > -1
    }
    getEmbeddedMediaType({ url }) {
        switch (true) {
            case this.isYoutubeLink(url): return 'YOUTUBE'
            case this.isSoundcloudLink(url): return 'SOUNDCLOUD'
            default:
                const extension = url.split('.').pop()
                switch (true) {
                    case !extension: return 'UNKNOWN'
                    case EMBEDDABLE_IMAGE_FILE_EXTENSIONS.indexOf(extension) > -1: return 'PICTURE'
                    case EMBEDDABLE_AUDIO_FILE_EXTENSIONS.indexOf(extension) > -1: return 'AUDIO'
                    case EMBEDDABLE_VIDEO_FILE_EXTENSIONS.indexOf(extension) > -1: return 'VIDEO'
                }
        }
    }
    isIE() {
        return (
            navigator.appName === 'Microsoft Internet Explorer' ||
            !!(navigator.userAgent.match(/Trident/) ||
            navigator.userAgent.match(/rv 11/))
        )
    }
    isIOS() {
        return /iPad|iPhone|iPod/.test(navigator.userAgent) && !window.MSStream
    }
    isTouchDevice() {
        return 'ontouchstart' in window || navigator.msMaxTouchPoints
    }
    isNVP() {
        return window.innerWidth < BREAK_XS
    }
    formatDateToDayMonthYear(dateString) {
        const date = new Date(dateString)

        return isNaN(date)
            ? ''
            : formatDay(date.getDate()) + "." + formatMonth(date.getMonth() + 1) + "." + date.getFullYear()
    }
    sprintf(str, ...replacements) {
        let idx = 0
        return str.replace(/(%s|%d)/g, (match) => {
            idx++
            return replacements[idx - 1] || match
        })
    }
    getMostRecentChangeDate(item) {
        return this.getMostRecentDateFromReviewList(item.reviewableChanges)
    }
    getMostRecentReportDate(item) {
        return this.getMostRecentDateFromReviewList(item.improperContents)
    }
    getMostRecentDateFromReviewList(reviews) {
        return reviews.reduce((mostRecentDate, { createdAt, reviewed }) => {
            const date = new Date(createdAt)
            return !reviewed && !mostRecentDate || date > mostRecentDate
                ? date
                : mostRecentDate
        }, null)
    }
    getMostRecentChangeDateFormatted(item) {
        const date = this.getMostRecentChangeDate(item)
        return isNaN(date) ? '' : this.formatDateToDayMonthYear(date.toISOString())
    }
    getMostRecentReportDateFormatted(item) {
        const date = this.getMostRecentReportDate(item)
        return isNaN(date) ? '' : this.formatDateToDayMonthYear(date.toISOString())
    }
    getChangedByLabel({ __changers }) {
        // Unknown
        if (__changers.length === 1 && __changers[0] === 'UNKNOWN')
            return this.dependencyExists('$translate')
                ? this.$translate.instant('UNKNOWN')
                : ''

        // One name
        if (__changers.length === 1)
            return __changers[0].name+' '+__changers[0].surname

        // # changers
        return this.dependencyExists('$translate')
            ? this.sprintf(
                this.$translate.instant('NUM_CHANGERS'),
                __changers.length
            )
            : ''
    }
    getReportedByLabel({ __reporters }) {
        // Unknown
        if (__reporters.length === 1 && __reporters[0] === 'UNKNOWN')
            return this.dependencyExists('$translate')
                ? this.$translate.instant('UNKNOWN')
                : ''

        // One name
        if (__reporters.length === 1)
            return __reporters[0].name+' '+__reporters[0].surname

        // # changers
        return this.dependencyExists('$translate')
            ? this.sprintf(
                this.$translate.instant('NUM_REPORTERS'),
                __reporters.length
            )
            : ''
    }
    getCommaSeparatedChangers(item) {
        return this.getCreatedByToString(item.__changers);
    }
    getCommaSeparatedReporters(item) {
        return this.getCreatedByToString(item.__reporters);
    }
    getCreatedByToString(items){
        return items.reduce((str, createdBy) => {
            if (!createdBy)
                return this.dependencyExists('$translate')
                    ? this.$translate.instant('UNKNOWN')
                    : ''

            const { name, surname } = createdBy
            return `${str}${str ? ', ' : ''}${name} ${surname}`
        }, '')
    }
    dependencyExists(depName) {
        if (typeof this[depName] === 'undefined') {
            throw new Error(`this.${depName} is undefined, please include '${depName}' in controller.$inject = [..., '${depName}']`)
            return false
        }
        return true
    }
    scrollToElement(element, duration = 200, offset = 0) {
        if (typeof element === 'string') element = document.querySelector(element)

        if (element) {
            const startTime = Date.now()
            const { pageYOffset } = window
            const destinationX = element.getBoundingClientRect().top - offset
            const easeInOutSin = (t) => (1 + Math.sin(Math.PI * t - Math.PI / 2)) / 2
            const scroll = () => {
                const progress = Math.min(1, (Date.now() - startTime) / duration)
                window.scrollTo(
                    window.pageXOffset,
                    easeInOutSin(progress) * destinationX + pageYOffset
                )
                if (progress < 1)
                    window.requestAnimationFrame(scroll)
            }
            scroll()
        }
    }
    updateChaptersStateFromEditors() {
        /**
         * call updateState() on all dopChapters which updates $scope.chapter.blocks
         * from editors' innerHTML values.
         */
        for (let chapter of document.querySelectorAll('dop-chapter, .dop-chapter, [dop-chapter]')) {
            const chapterCtrl = angular.element(chapter).controller('dopChapter')
            if (chapterCtrl && typeof chapterCtrl.updateState === 'function')
                chapterCtrl.updateState()
        }
    }
    updateChapterEditorsFromState() {
        /**
         * call updateEditors() on all dopChapters which updates editors' innerHTML values
         * from $scope.chapter.blocks.
         */
        for (let chapter of document.querySelectorAll('dop-chapter, .dop-chapter, [dop-chapter]')) {
            const chapterCtrl = angular.element(chapter).controller('dopChapter')
            if (chapterCtrl && typeof chapterCtrl.updateEditors === 'function')
                chapterCtrl.updateEditors()
        }
    }
    getSlug(str, fallback = '') {
        return str
            ? str.replace(/\s+/g, '-')
            : fallback
    }
    transformChapters(chapters) {
        return !Array.isArray(chapters) || !chapters.length || chapters[0].blocks
            ? chapters
            : chapters.reduce(
                (chapters, c, idx) => chapters
                    .concat({
                        title: c.title,
                        blocks: [{
                            narrow: false,
                            htmlContent: (c.text || '')
                                + this.transformEmbeds(c)
                                + c.subchapters.reduce(
                                    (subchapters, s, subIdx) =>
                                        subchapters
                                        + `<h3 class="subchapter">${s.title || (this.dependencyExists('$translate') ? this.$translate.instant('PORTFOLIO_ENTER_SUBCHAPTER_TITLE') : '')}</h3>`
                                        + (s.text || '')
                                        + this.transformEmbeds(s),
                                    ''
                                )
                        }]
                    }),
                []
            )
    }
    transformEmbeds(chapter) {
        return !Array.isArray(chapter.contentRows)
            ? ''
            : chapter.contentRows.reduce(
                (embeds, r) => Array.isArray(r.learningObjects) && r.learningObjects.length
                    ? embeds + `<div class="chapter-embed-card chapter-embed-card--material" data-id="${r.learningObjects[0].id}"></div>`
                    : embeds,
                ''
            )
    }
    arrayEquals(array1,array2){
        if (!array1 && !array2) return true
        if (!array1 || !array2) return false
        return array1.length === array2.length && array1.every((value, index) => value === array2[index])
    }
    equals(element1, element2){
        if (!element1 && !element2) return true
        if (!element1 || !element2) return false
        return element1 === element2;

    }
    issueDateToDate(issueDate) {
        return issueDate && (
            issueDate.day && issueDate.month && issueDate.year
                ? new Date(issueDate.year, issueDate.month - 1, issueDate.day)
                : issueDate.month && issueDate.year
                    ? new Date(issueDate.year, issueDate.month - 1, 1)
                    : issueDate.year
                        && new Date(issueDate.year, 0, 1)
        )
    }
    stripHtml(htmlString = '') {
        let tmp = document.createElement('div')
        tmp.innerHTML = htmlString

        return tmp.textContent || tmp.innerText || ''
    }
    sortTagsByUpVoteCount(tags) {
        return Array.isArray(tags)
            ? tags.sort((a, b) => {
                const diff = b.upVoteCount - a.upVoteCount;
                if (diff !== 0) return diff;
                if (a.tag < b.tag) return -1;
                if (a.tag > b.tag) return 1;
                return 0;
            })
            : tags
    }
    isString(value) {
        return typeof value === 'string' || value instanceof String
    }
    arrayToLowerCase(upperCaseArray) {
        const lowerCaseArray = []

        for (let i = 0; i < upperCaseArray.length; i++)
            if (upperCaseArray[i] && this.isString(upperCaseArray[i]))
                lowerCaseArray.push(upperCaseArray[i].toLowerCase())

        return lowerCaseArray
    }
    arrayToUpperCase(lowerCaseArray) {
        const upperCaseArray = []

        for (let i = 0; i < lowerCaseArray.length; i++)
            if (lowerCaseArray[i] && this.isString(lowerCaseArray[i]))
                upperCaseArray.push(lowerCaseArray[i].toUpperCase())

        return upperCaseArray
    }
    countSelected(filterGroup){
        let count = 0;
        Object.entries(filterGroup).forEach(([name, content]) => {
            if (content.isMaterialActive) {
                count += content.countMaterial
            }
            if (content.isPortfolioActive) {
                count += content.countPortfolio
            }
        })
        return count;
    }
    filterModel(groupName) {
        return {
            name: groupName,
            countMaterial: 0,
            countPortfolio: 0,
            isMaterialActive: false,
            isPortfolioActive: false
        }
    }
    replaceTitleContent(results, t, query, translations) {
        if (!results) {
            return t(translations.none).replace('${query}', query)
        } else if (results === 1) {
            let newTitle = t(translations.single)
            return query ? newTitle.replace('${query}', query) : newTitle.replace('${query}', '').replace(/"/g, '')
        } else if (results > 1) {
            let newTitle = t(translations.multiple)
            return query ? newTitle.replace('${count}', results).replace('${query}', query)
                : newTitle.replace('${count}', results).replace('${query}', '').replace(/"/g, '')
        }
        return '';
    }
    toTitleCase(str) {
        return str.replace(/\w\S*/g, (txt)  => txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase()
        );
    }
    disableAllGroupsForFilter(filter) {
        Object.entries(filter).forEach(([name, content]) => {
            content.isMaterialActive = false
            content.isPortfolioActive = false
        })
    }
    allIsOrWas(filterGroup, groupId) {
        const allActiveExists = filterGroup['all'].isMaterialActive;
        const currentIsAll = groupId === 'all';
        return !currentIsAll && allActiveExists || currentIsAll && !allActiveExists;
    }
}


/**
 * Convenience methods for creating angular controllers, diectives, components,
 * services, providers and factories.
 */
function _controller(name, controller) {
    return angular.module('koolikottApp').controller(name, controller)
}

function directive(name, options) {
    const factory = typeof options === 'function' || Array.isArray(options)
        ? options
        : () => options

    return angular.module('koolikottApp').directive(name, factory)
}

function component(name, options) {
    return angular.module('koolikottApp').component(name, options)
}

function service(name, controller) {
    return angular.module('koolikottApp').service(name, controller)
}

function factory(name, controller) {
    const factoryFn = Array.isArray(controller.$inject)
        ? controller.$inject.concat((...args) => new controller(...args))
        : () => new controller()

    return angular.module('koolikottApp').factory(name, factoryFn)
}
