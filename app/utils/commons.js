/**
 * Common constants and functions.
 * 
 * @author Jordan Silva
 * 
 */

function log() {
    if (console && console.log) {
        console.log.apply(console, arguments);
    }
}

function isDefined(value) {
    return angular.isDefined(value) && value != null;
}

if (typeof String.prototype.startsWith === 'undefined') {
	String.prototype.startsWith = function(value, searchValue) {
		return value.indexOf(searchValue) === 0;
	};
}

if (typeof String.prototype.contains === 'undefined') {
	String.prototype.contains = function(it) { return this.indexOf(it) != -1; };
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
	
	for(var k in first) {
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
}