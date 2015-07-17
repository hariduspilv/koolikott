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

 function formatDay(day) {
        return day > 9 ? "" + day : "0" + day; 
    }
    
    function formatMonth(month) {
        return month > 9 ? "" + month : "0" + month; 
    }
    
    function formatYear(year) {
        return year < 0 ? year * -1 : year; 
    }