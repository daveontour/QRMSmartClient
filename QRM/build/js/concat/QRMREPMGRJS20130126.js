/*
    http://www.JSON.org/json2.js
    2008-03-24

    Public Domain.

    NO WARRANTY EXPRESSED OR IMPLIED. USE AT YOUR OWN RISK.

    See http://www.JSON.org/js.html

    This file creates a global JSON object containing three methods: stringify,
    parse, and quote.


        JSON.stringify(value, replacer, space)
            value       any JavaScript value, usually an object or array.

            replacer    an optional parameter that determines how object
                        values are stringified for objects without a toJSON
                        method. It can be a function or an array.

            space       an optional parameter that specifies the indentation
                        of nested structures. If it is omitted, the text will
                        be packed without extra whitespace. If it is a number,
                        it will specify the number of spaces to indent at each
                        level. If it is a string (such as '\t'), it contains the
                        characters used to indent at each level.

            This method produces a JSON text from a JavaScript value.

            When an object value is found, if the object contains a toJSON
            method, its toJSON method will be called and the result will be
            stringified. A toJSON method does not serialize: it returns the
            value represented by the name/value pair that should be serialized,
            or undefined if nothing should be serialized. The toJSON method will
            be passed the key associated with the value, and this will be bound
            to the object holding the key.

            This is the toJSON method added to Dates:

                function toJSON(key) {
                    return this.getUTCFullYear()   + '-' +
                         f(this.getUTCMonth() + 1) + '-' +
                         f(this.getUTCDate())      + 'T' +
                         f(this.getUTCHours())     + ':' +
                         f(this.getUTCMinutes())   + ':' +
                         f(this.getUTCSeconds())   + 'Z';
                }

            You can provide an optional replacer method. It will be passed the
            key and value of each member, with this bound to the containing
            object. The value that is returned from your method will be
            serialized. If your method returns undefined, then the member will
            be excluded from the serialization.

            If no replacer parameter is provided, then a default replacer
            will be used:

                function replacer(key, value) {
                    return Object.hasOwnProperty.call(this, key) ?
                        value : undefined;
                }

            The default replacer is passed the key and value for each item in
            the structure. It excludes inherited members.

            If the replacer parameter is an array, then it will be used to
            select the members to be serialized. It filters the results such
            that only members with keys listed in the replacer array are
            stringified.

            Values that do not have JSON representaions, such as undefined or
            functions, will not be serialized. Such values in objects will be
            dropped; in arrays they will be replaced with null. You can use
            a replacer function to replace those with JSON values.
            JSON.stringify(undefined) returns undefined.

            The optional space parameter produces a stringification of the value
            that is filled with line breaks and indentation to make it easier to
            read.

            If the space parameter is a non-empty string, then that string will
            be used for indentation. If the space parameter is a number, then
            then indentation will be that many spaces.

            Example:

            text = JSON.stringify(['e', {pluribus: 'unum'}]);
            // text is '["e",{"pluribus":"unum"}]'


            text = JSON.stringify(['e', {pluribus: 'unum'}], null, '\t');
            // text is '[\n\t"e",\n\t{\n\t\t"pluribus": "unum"\n\t}\n]'


        JSON.parse(text, reviver)
            This method parses a JSON text to produce an object or array.
            It can throw a SyntaxError exception.

            The optional reviver parameter is a function that can filter and
            transform the results. It receives each of the keys and values,
            and its return value is used instead of the original value.
            If it returns what it received, then the structure is not modified.
            If it returns undefined then the member is deleted.

            Example:

            // Parse the text. Values that look like ISO date strings will
            // be converted to Date objects.

            myData = JSON.parse(text, function (key, value) {
                var a;
                if (typeof value === 'string') {
                    a =
/^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2}(?:\.\d*)?)Z$/.exec(value);
                    if (a) {
                        return new Date(Date.UTC(+a[1], +a[2] - 1, +a[3], +a[4],
                            +a[5], +a[6]));
                    }
                }
                return value;
            });


        JSON.quote(text)
            This method wraps a string in quotes, escaping some characters
            as needed.


    This is a reference implementation. You are free to copy, modify, or
    redistribute.

    USE YOUR OWN COPY. IT IS EXTREMELY UNWISE TO LOAD THIRD PARTY
    CODE INTO YOUR PAGES.
*/

if (!this.JSON) {

// Create a JSON object only if one does not already exist. We create the
// object in a closure to avoid global variables.

    JSON = function () {

        function f(n) {    // Format integers to have at least two digits.
            return n < 10 ? '0' + n : n;
        }

        Date.prototype.toJSON = function () {

// Eventually, this method will be based on the date.toISOString method.

            return this.getUTCFullYear()   + '-' +
                 f(this.getUTCMonth() + 1) + '-' +
                 f(this.getUTCDate())      + 'T' +
                 f(this.getUTCHours())     + ':' +
                 f(this.getUTCMinutes())   + ':' +
                 f(this.getUTCSeconds())   + 'Z';
        };


        var escapeable = /["\\\x00-\x1f\x7f-\x9f]/g,
            gap,
            indent,
            meta = {    // table of character substitutions
                '\b': '\\b',
                '\t': '\\t',
                '\n': '\\n',
                '\f': '\\f',
                '\r': '\\r',
                '"' : '\\"',
                '\\': '\\\\'
            },
            rep;


        function quote(string) {

// If the string contains no control characters, no quote characters, and no
// backslash characters, then we can safely slap some quotes around it.
// Otherwise we must also replace the offending characters with safe escape
// sequences.

            return escapeable.test(string) ?
                '"' + string.replace(escapeable, function (a) {
                    var c = meta[a];
                    if (typeof c === 'string') {
                        return c;
                    }
                    c = a.charCodeAt();
                    return '\\u00' + Math.floor(c / 16).toString(16) +
                                               (c % 16).toString(16);
                }) + '"' :
                '"' + string + '"';
        }


        function str(key, holder) {

// Produce a string from holder[key].

            var i,          // The loop counter.
                k,          // The member key.
                v,          // The member value.
                length,
                mind = gap,
                partial,
                value = holder[key];

// If the value has a toJSON method, call it to obtain a replacement value.

            if (value && typeof value === 'object' &&
                    typeof value.toJSON === 'function') {
                value = value.toJSON(key);
            }

// If we were called with a replacer function, then call the replacer to
// obtain a replacement value.

            if (typeof rep === 'function') {
                value = rep.call(holder, key, value);
            }

// What happens next depends on the value's type.

            switch (typeof value) {
            case 'string':
                return quote(value);

            case 'number':

// JSON numbers must be finite. Encode non-finite numbers as null.

                return isFinite(value) ? String(value) : 'null';

            case 'boolean':
            case 'null':

// If the value is a boolean or null, convert it to a string. Note:
// typeof null does not produce 'null'. The case is included here in
// the remote chance that this gets fixed someday.

                return String(value);

// If the type is 'object', we might be dealing with an object or an array or
// null.

            case 'object':

// Due to a specification blunder in ECMAScript, typeof null is 'object',
// so watch out for that case.

                if (!value) {
                    return 'null';
                }

// Make an array to hold the partial results of stringifying this object value.

                gap += indent;
                partial = [];

// If the object has a dontEnum length property, we'll treat it as an array.

                if (typeof value.length === 'number' &&
                        !(value.propertyIsEnumerable('length'))) {

// The object is an array. Stringify every element. Use null as a placeholder
// for non-JSON values.

                    length = value.length;
                    for (i = 0; i < length; i += 1) {
                        partial[i] = str(i, value) || 'null';
                    }

// Join all of the elements together, separated with commas, and wrap them in
// brackets.

                    v = partial.length === 0 ? '[]' :
                        gap ? '[\n' + gap + partial.join(',\n' + gap) +
                                  '\n' + mind + ']' :
                              '[' + partial.join(',') + ']';
                    gap = mind;
                    return v;
                }

// If the replacer is an array, use it to select the members to be stringified.

                if (typeof rep === 'object') {
                    length = rep.length;
                    for (i = 0; i < length; i += 1) {
                        k = rep[i];
                        if (typeof k === 'string') {
                            v = str(k, value, rep);
                            if (v) {
                                partial.push(quote(k) + (gap ? ': ' : ':') + v);
                            }
                        }
                    }
                } else {

// Otherwise, iterate through all of the keys in the object.

                    for (k in value) {
                        v = str(k, value, rep);
                        if (v) {
                            partial.push(quote(k) + (gap ? ': ' : ':') + v);
                        }
                    }
                }

// Join all of the member texts together, separated with commas,
// and wrap them in braces.

                v = partial.length === 0 ? '{}' :
                    gap ? '{\n' + gap + partial.join(',\n' + gap) +
                              '\n' + mind + '}' :
                          '{' + partial.join(',') + '}';
                gap = mind;
                return v;
            }
        }


// Return the JSON object containing the stringify, parse, and quote methods.

        return {
            stringify: function (value, replacer, space) {

// The stringify method takes a value and an optional replacer, and an optional
// space parameter, and returns a JSON text. The replacer can be a function
// that can replace values, or an array of strings that will select the keys.
// A default replacer method can be provided. Use of the space parameter can
// produce text that is more easily readable.

                var i;
                gap = '';
                indent = '';
                if (space) {

// If the space parameter is a number, make an indent string containing that
// many spaces.

                    if (typeof space === 'number') {
                        for (i = 0; i < space; i += 1) {
                            indent += ' ';
                        }

// If the space parameter is a string, it will be used as the indent string.

                    } else if (typeof space === 'string') {
                        indent = space;
                    }
                }

// If there is no replacer parameter, use the default replacer.

                if (!replacer) {
                    rep = function (key, value) {
                        if (!Object.hasOwnProperty.call(this, key)) {
                            return undefined;
                        }
                        return value;
                    };

// The replacer can be a function or an array. Otherwise, throw an error.

                } else if (typeof replacer === 'function' ||
                        (typeof replacer === 'object' &&
                         typeof replacer.length === 'number')) {
                    rep = replacer;
                } else {
                    throw new Error('JSON.stringify');
                }

// Make a fake root object containing our value under the key of ''.
// Return the result of stringifying the value.

                return str('', {'': value});
            },


            parse: function (text, reviver) {

// The parse method takes a text and an optional reviver function, and returns
// a JavaScript value if the text is a valid JSON text.

                var j;

                function walk(holder, key) {

// The walk method is used to recursively walk the resulting structure so
// that modifications can be made.

                    var k, v, value = holder[key];
                    if (value && typeof value === 'object') {
                        for (k in value) {
                            if (Object.hasOwnProperty.call(value, k)) {
                                v = walk(value, k);
                                if (v !== undefined) {
                                    value[k] = v;
                                } else {
                                    delete value[k];
                                }
                            }
                        }
                    }
                    return reviver.call(holder, key, value);
                }


// Parsing happens in three stages. In the first stage, we run the text against
// regular expressions that look for non-JSON patterns. We are especially
// concerned with '()' and 'new' because they can cause invocation, and '='
// because it can cause mutation. But just to be safe, we want to reject all
// unexpected forms.

// We split the first stage into 4 regexp operations in order to work around
// crippling inefficiencies in IE's and Safari's regexp engines. First we
// replace all backslash pairs with '@' (a non-JSON character). Second, we
// replace all simple value tokens with ']' characters. Third, we delete all
// open brackets that follow a colon or comma or that begin the text. Finally,
// we look to see that the remaining characters are only whitespace or ']' or
// ',' or ':' or '{' or '}'. If that is so, then the text is safe for eval.

                if (/^[\],:{}\s]*$/.test(text.replace(/\\["\\\/bfnrtu]/g, '@').
replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g, ']').
replace(/(?:^|:|,)(?:\s*\[)+/g, ''))) {

// In the second stage we use the eval function to compile the text into a
// JavaScript structure. The '{' operator is subject to a syntactic ambiguity
// in JavaScript: it can begin a block or an object literal. We wrap the text
// in parens to eliminate the ambiguity.

                    j = eval('(' + text + ')');

// In the optional third stage, we recursively walk the new structure, passing
// each name/value pair to a reviver function for possible transformation.

                    return typeof reviver === 'function' ?
                        walk({'': j}, '') : j;
                }

// If the text is not JSON parseable, then a SyntaxError is thrown.

                throw new SyntaxError('JSON.parse');
            },

            quote: quote
        };
    }();
}
var RankPane,AnalysisPane,RiskTablePane,RelMatrixPane,RelationshipPane,ConcurrentPane,CalPane,DashBoard,OverviewPane,uMat,uMatRef,descendantsCurrent = false,pageRiskData = 1,pageProbImpact = 2,pageMitigation = 4,pageResponse = 8,pageConsequence = 16,pageControl = 32,pageObjective = 64,pageAttachment = 128,pageComment = 256,pageAudit = 512,viewExplorer = 1024,viewDashboard = 2048,viewCalendar = 4096,viewMatrix = 8192,viewRanking = 16384,viewSumaryRisk = 32768,viewAnalysis = 65536,viewReview = 131072,viewIncident = 262144,viewOverview = 524288,viewBackground = 1048576,currentEditorMask = 0,defaultViewChoice = 0,selectedCellID,selectedCellClassName,projectTitleStr,tMat,tMatRef,descend = false,projectID,sessionID,userID,userName,loggedOnUser, project,tempIndex = -100,projects,matrices = new Array(),allMatrices = new Array(),allCategories = new Array(),allUsers = new Array(),allObjectives = new Array(),projectMetrics = new Array(),quantTypesValueMap = new Object(),currentMatrix,riskOwners = new Array(),riskManagers = new Array(),primCats = new Array(),secCats = new Array(),riskOwnersIDs = new Array(),riskManagersIDs = new Array(),checkDescendants,currentTab,currentTabNum = 0,currentPaneNum = 0,navigatorInit = false,relMatrixRisks,currentReview,currentRiskObj,currentPane,currentRiskData,managerValueMap,catValueMap,ownerValueMap,riskEditor,_qrmNewRisk = -1,_qrmNewSummaryRisk = -2,//ctrlColor = '#C6DEFF',ctrlColor = '#FFFFFF',viewState = "Regular",currentTabNum,currentTabPane,currentTabID,currentTab,projObjectives,projCats,projType,dirtyEditor = false,RiskController = new Object(),singlePhase = false,zeroProbLimit = 0.00001,projectMap = new Map(),riskProjectEditMode = "risk",reportParamMap = new Map(),mgrAdded = false,explorerSetProject = true,projectValueMap,projectReportValueMap,homePageReports,reportReadyAction = 0,distributionTypeValueMap = {"au.com.quaysystems.qrm.util.probability.NormalDistribution" : "Normal","au.com.quaysystems.qrm.util.probability.TruncNormalDistribution" : "Truncated Normal","au.com.quaysystems.qrm.util.probability.TriangularDistribution" : "Triangular","au.com.quaysystems.qrm.util.probability.TriGenDistribution" : "Truncated Triangular","au.com.quaysystems.qrm.util.probability.SimpleDistribution" : "Simple","au.com.quaysystems.qrm.util.probability.UniformDistribution" : "Uniform","au.com.quaysystems.qrm.util.probability.DiscreteDistribution" : "Discrete","au.com.quaysystems.qrm.util.probability.NullDistribution" : "No Distribution Defined"};distributionTypeValueMapMap = new Map();distributionTypeValueMapMap.put("au.com.quaysystems.qrm.util.probability.NormalDistribution" , "Normal");distributionTypeValueMapMap.put("au.com.quaysystems.qrm.util.probability.TruncNormalDistribution" , "Truncated Normal");distributionTypeValueMapMap.put("au.com.quaysystems.qrm.util.probability.TriangularDistribution" , "Triangular");distributionTypeValueMapMap.put("au.com.quaysystems.qrm.util.probability.TriGenDistribution" , "Truncated Triangular");distributionTypeValueMapMap.put("au.com.quaysystems.qrm.util.probability.SimpleDistribution" , "Simple");distributionTypeValueMapMap.put("au.com.quaysystems.qrm.util.probability.UniformDistribution" , "Uniform");distributionTypeValueMapMap.put("au.com.quaysystems.qrm.util.probability.DiscreteDistribution" , "Discrete");distributionTypeValueMapMap.put("au.com.quaysystems.qrm.util.probability.NullDistribution" , "No Distribution Defined");var BrowserDetect = {		init: function () {			this.browser = this.searchString(this.dataBrowser) || "An unknown browser";			this.version = this.searchVersion(navigator.userAgent)				|| this.searchVersion(navigator.appVersion)				|| "an unknown version";			this.OS = this.searchString(this.dataOS) || "an unknown OS";		},		searchString: function (data) {			for (var i=0;i<data.length;i++)	{				var dataString = data[i].string;				var dataProp = data[i].prop;				this.versionSearchString = data[i].versionSearch || data[i].identity;				if (dataString) {					if (dataString.indexOf(data[i].subString) != -1)						return data[i].identity;				}				else if (dataProp)					return data[i].identity;			}		},		searchVersion: function (dataString) {			var index = dataString.indexOf(this.versionSearchString);			if (index == -1) return;			return parseFloat(dataString.substring(index+this.versionSearchString.length+1));		},		dataBrowser: [			{				string: navigator.userAgent,				subString: "Chrome",				identity: "Chrome"			},			{ 	string: navigator.userAgent,				subString: "OmniWeb",				versionSearch: "OmniWeb/",				identity: "OmniWeb"			},			{				string: navigator.vendor,				subString: "Apple",				identity: "Safari",				versionSearch: "Version"			},			{				prop: window.opera,				identity: "Opera",				versionSearch: "Version"			},			{				string: navigator.vendor,				subString: "iCab",				identity: "iCab"			},			{				string: navigator.vendor,				subString: "KDE",				identity: "Konqueror"			},			{				string: navigator.userAgent,				subString: "Firefox",				identity: "Firefox"			},			{				string: navigator.vendor,				subString: "Camino",				identity: "Camino"			},			{		// for newer Netscapes (6+)				string: navigator.userAgent,				subString: "Netscape",				identity: "Netscape"			},			{				string: navigator.userAgent,				subString: "MSIE",				identity: "Explorer",				versionSearch: "MSIE"			},			{				string: navigator.userAgent,				subString: "Gecko",				identity: "Mozilla",				versionSearch: "rv"			},			{ 		// for older Netscapes (4-)				string: navigator.userAgent,				subString: "Mozilla",				identity: "Netscape",				versionSearch: "Mozilla"			}		],		dataOS : [			{				string: navigator.platform,				subString: "Win",				identity: "Windows"			},			{				string: navigator.platform,				subString: "Mac",				identity: "Mac"			},			{				   string: navigator.userAgent,				   subString: "iPhone",				   identity: "iPhone/iPod"		    },			{				string: navigator.platform,				subString: "Linux",				identity: "Linux"			}		]	};	BrowserDetect.init();function getCookie(c_name) {	if (document.cookie.length > 0) {		c_start = document.cookie.indexOf(c_name + "=");		if (c_start != -1) {						c_start = c_start + c_name.length + 1;			c_end = document.cookie.indexOf(";", c_start);						if (c_end == -1) {				c_end = document.cookie.length;			}			return unescape(document.cookie.substring(c_start, c_end));		}	}	return "";}function roundDoubleStr(value, precision){		if (value == 0){		return "0.00%";	}	str = value.toString();		catStr = "";	var i = 0;		while (catStr.length < str.indexOf('.') + precision+1) catStr += str.charAt(i++);			return catStr+"%";}function dateParser(key, value) {	var a;	if (typeof value === 'string') {				a = /^(\d{4})-(\d{2})-(\d{2}) (\d{2}):(\d{2}):(\d{2})/.exec(value);		if (a) {			var d = new Date();			d.setFullYear(+a[1], +a[2] - 1, +a[3]);			d.setHours(+a[4], +a[5], +a[6], 0);			return d;		}		a = /^(\d{4})-(\d{2})-(\d{2})/.exec(value);		if (a) {			return new Date(Date.UTC(+a[1], +a[2] - 1, +a[3]));		}	}	return value;}function dateParserDebug(key, value) {	var a;	if (typeof value === 'string') {		a = /^(\d{4})-(\d{2})-(\d{2}) (\d{2}):(\d{2}):(\d{2})/.exec(value);		if (a) {			return  new Date(+a[1], +a[2] - 1, +a[3], +a[4], +a[5], +a[6], 0);		}		a = /^(\d{4})-(\d{2})-(\d{2})/.exec(value);		if (a) {			return new Date(Date.UTC(+a[1], +a[2] - 1, +a[3]));		}	}	return value;}function simplifyDate(sdParam){		var sd = sdParam;	try {		if (sd == null) {			sd = new Date();		}		var year = sd.getFullYear();		var month = sd.getMonth()+1;		var day = sd.getDate();				var mStr="";		if (month< 10){			mStr=mStr+"0"+month;		} else {		    mStr = mStr+month;		}				var dStr = "";		if (day< 10){			dStr=dStr+"0"+day;		} else {		    dStr = dStr+day;		}					return year+"-"+mStr+"-"+dStr;	} catch (e){		return sd;	}}function Map(){    	// members    	this.keyArray = new Array(); // Keys    	this.valArray = new Array(); // Values            	this.put = function  (key, val ){var elementIndex = this.findIt( key );        if( elementIndex == (-1) )    {        this.keyArray.push( key );        this.valArray.push( val );    }    else    {        this.valArray[ elementIndex ] = val;    }};	this.get = function ( key ){var result = null;    var elementIndex = this.findIt( key );    if( elementIndex != (-1) )    {           result = this.valArray[ elementIndex ];    }          return result;};	this.remove = function ( key ){   var elementIndex = this.findIt( key );    if( elementIndex != (-1) )    {        this.keyArray = this.keyArray.removeAt(elementIndex);        this.valArray = this.valArray.removeAt(elementIndex);    }          return ;};	this.size = function (){ return (this.keyArray.length);  };	this.clear = function () {while (this.keyArray.length > 0) { this.keyArray.pop(); this.valArray.pop(); }};	this.keySet = function (){ return (this.keyArray);};	this.valSet = function (){return (this.valArray);   };	this.showMe = function (){var result = "";        for( var i = 0; i < this.keyArray.length; i++ )    {        result += "Key: " + this.keyArray[ i ] + "\tValues: " + this.valArray[ i ] + "\n";    }    return result;};	this.findIt = function ( key ){    var result = (-1);    for( var i = 0; i < this.keyArray.length; i++ )    {        if( this.keyArray[ i ] == key )        {            result = i;            break;        }    }    return result;};	this.removeAt = function ( index ){  var part1 = this.slice( 0, index);  var part2 = this.slice( index+1 );  return( part1.concat( part2 ) );};}//Datasourcesfunction createDataSources(){isc.RestDataSource.create({    ID: "contextReportsDS",    dataFormat: "xml",    fields: [{        name: "id",        title: "id"    }, {        name: "reportName",        title: "Report Name",        align: "left"    }],    recordXPath: "//response/data/record",    transformRequest: function(dsRequest){        var params = {            NOCACHE: Math.random()        };        return isc.addProperties({}, dsRequest.data, params);    },    fetchDataURL: "./getContextReports"});isc.RestDataSource.create({    ID: "registerReportsDS",    dataFormat: "xml",    fields: [{        name: "id",        title: "id"    }, {        name: "reportName",        title: "Report Name",        align: "left"    }],    recordXPath: "//response/data/record",    transformRequest: function(dsRequest){        var params = {            NOCACHE: Math.random()        };        return isc.addProperties({}, dsRequest.data, params);    },    fetchDataURL: "./getRegisterReports"});isc.RestDataSource.create({    ID: "riskReportsDS",    dataFormat: "xml",    fields: [{        name: "id",        title: "id"    }, {        name: "reportName",        title: "Report Name",        align: "left"    }],    recordXPath: "//response/data/record",    transformRequest: function(dsRequest){        var params = {            NOCACHE: Math.random()        };        return isc.addProperties({}, dsRequest.data, params);    },    fetchDataURL: "./getRiskReports"});isc.RestDataSource.create({    ID: "analToolDS",    fields: [{        name: "title",        title: "Analysis Tool",        type: "string"    }, {        name: "clazz",        type: "string"    }, {        name: "param1",        type: "string"    }, {        name: "b3D",        type: "boolean"    }, {        name: "bTol",        type: "boolean"    }, {        name: "bNumElem",        type: "boolean"    }, {        name: "bReverse",        type: "boolean"    }],    fetchDataURL: "./getAnalToolsFetch"});isc.RestDataSource.create({    ID: "userTemplateDS",    titleField:"templateName",    fields: [{        name: "templateName",        type: "string"    }, {        name: "template",        type: "string"    }],    fetchDataURL: "./getUserImportTemplate"});// Datasource to supply the pick list isc.DataSource.create({    ID: "riskSelectGridDS",    fields: [{        name: "riskProjectCode",        title: "Risk Code",        primaryKey:true    }, {        name: "title",        title: "Risk Title"    }],    dataFormat: "json",    clientOnly: true,    testData: "[{}]",    transformResponse: function(dsResponse, dsRequest, data){        dsResponse.data = relMatrixRisks;        dsResponse.totalRows = relMatrixRisks.length;        dsResponse.startRow = 0;        dsResponse.endRow = relMatrixRisks.length - 1;    }});isc.RestDataSource.create({    ID: "reviewSelectGridDS",    fields: [{        name: "title",        title: "Review Title",        type:"text"    },{    	name:"scheduledDate",    	title:"Scheduled Date",    	type:"date",    	width:150    },{    	name:"actualDate",    	title:"Actual Date",    	type:"date"    },{    	name:"reviewID",    	hidden:true,    	primaryKey:true    }, {    	name:"reviewComplete",	    	title:"Complete",     	type:"boolean"    }],   fetchDataURL: "./getExistingReviews"});isc.RestDataSource.create({    ID: "incidentSelectGridDS",    fields: [{        name: "title",        title: "Incident Title"    },{    	name:"incidentDate",    	title:"Incident Date"    },{    	name:"incidentID",    	hidden:true,    	primaryKey:true    }],   fetchDataURL: "./getExistingIncidents"});}var dateEnd = new Date();dateEnd.setFullYear(dateEnd.getFullYear()+10,12,31);isc.DateItem.addProperties({"endDate": dateEnd});RiskController.riskID = 0;RiskController.dirty = false;RiskController.allowEditorChanges = true;RiskController.secAllowEdit = false;RiskController.currentRisk = null;Date.setShortDisplayFormat("toEuropeanShortDate");Date.setNormalDisplayFormat("toLocaleString");isc.setAutoDraw(false);Array.prototype.clear = function(){	this.length = 0;};isc.RPCManager.addClassProperties({    handleTransportError : function (transactionNum, status, httpResponseCode, httpResponseText) {           if (httpResponseCode == 401 || httpResponseCode == 405){               if (httpResponseText.indexOf("LOGGEDOUT") != -1){            	   isc.say("Logged out of Quay Risk Manager", function(){window.open(httpResponseText.split("*")[1], "_self");});               } else {            	   isc.say("Your session has expired or is invalid. You will need to login to Quay Risk Manager.", function(){window.open("./login.jsp", "_self");});               }           } else {        	   isc.say("An error occured processing your request. The server may be offline. You will need to login to Quay Risk Manager again.", function(){window.open("./login.jsp", "_self");});           }    }});function constructRepMgrApp(){
	try {

		isc.setAutoDraw(false);
		
		var topPane = isc.HLayout.create({
			backgroundColor: ctrlColor,
			layoutTopMargin: 2,
			membersMargin:4,
			height: 25,
			width: "100%",
			members: [isc.Label.create({
				contents:"<span class='qrmMajorTitle'>Quay Risk Manager Repository Manager<span>",
				height:25,
				width:"100%"
			}),
			isc.Label.create({
				height:25,
				ID:"qrmHeaderUserName",
				width:100
			}),
			isc.Label.create({
				contents:"<span style='cursor:pointer;font-size:10;color:blue'>Sign Out<span>",
				click:function(){
				isc.RPCManager.sendRequest({
					prompt:"Logging Out",
					evalResult: true,
					params: {
					"OPERATION": "logout"
				},
				actionURL: "./QRMServer"
				});				
				},
				height:25,
				width:60
			})],
			layoutBottomMargin: 2,
			autoDraw: false
		});

		ServerPane = defineServerControlPane();
		RepPane = defineRepositoryPane();
		SchedJobPane = defineScheduledJobPane();
		ComplJobPane = defineCompletedJobPane();
		
		TabSet.create({
			 ID:"RepMgrTabSet",
			 paneContainerProperties:{backgroundColor : ctrlColor},
			 backgroundColor : ctrlColor,
			 tabBarPosition : "top",
		     tabs: [
		         {title:"Server Control", pane: ServerPane},
		         {title:"Repositories", pane:RepPane},
		         {title:"Scheduled Jobs", pane: SchedJobPane},
		         {title:"Completed Jobs", pane:ComplJobPane}
		     ]
		 });
	
		PortalPane = isc.VLayout.create({
			backgroundColor: ctrlColor,
			membersMargin: 2,
			layoutMargin: 2,
			layoutTopMargin: 2,
			height: "100%",
			width: "100%",
			members: [topPane,
			          RepMgrTabSet],
			layoutBottomMargin: 2,
			layoutRightMargin: 2,
			layoutLeftMargin: 2,
			autoDraw: true
		});

	} 
	catch (e) {
		alert(e.message);
	}
}
function defineRepositoryPane(){
	isc.VLayout.create({
		membersMargin:5,
		width:"100%",
		height:"100%",
		autoDraw:true,
		backgroundColor: ctrlColor,
		ID:"RepositoryPane"
	});
	
	isc.ListGrid.create({
		ID:"RepGrid",
		width:"100%",
		height:"100%",
		alternateRecordStyles : true,
		fields : [{name:"rep", showTitle:true, title:"Repository",type : "select",width:200}, 
		          {	  name : "repmgr",
		        	  width : "200",
		        	  title : "Repository Risk Manager",
		        	  required : true,
		        	  type : "select",
		        	  allowEmptyValue : false,
		        	  optionDataSource: "allUsersDS", 
		        	  displayField:"name",
		        	  valueField : "stakeholderID"
		          }, {
		        	  name : "active",
		        	  width : "70",
		        	  title : "Active",
		        	  required : true,
		        	  type : "boolean"
		          }, {
		        	  name : "autoAddUsers",
		        	  width : "120",
		        	  title : "Auto Add Users",
		        	  required : true,
		        	  type : "boolean"
		          }, {
		        	  name : "sessionlimit",
		        	  title : "Session Limit",
		        	  width : "80",
		        	  required : true,
		        	  align:"center",
		        	  editorType: "spinner", 
		        	  min: -1, max: 100, step: 1, width: 95
		          }, {
		        	  name : "sessions",
		        	  width : "80",
		        	  align:"center",
		        	  title : "# Sessions",
		        	  required : true,
		        	  type:"text"
		          }, {
		        	  name : "userlimit",
		        	  title : "User Limit",
		        	  width : "50",
		        	  align:"center",
		        	  required : true,
		        	  editorType: "spinner", 
		        	  min: -1, max: 100, step: 1, width: 95
		            }, {
		        	  name : "url",
		        	  width : "400",
		        	  title : "Access URL",
		        	  required : true,
		        	  type : "text"
		            }],
				getBaseStyle: function(record, rowNum, colNum){
						if (colNum == 5 ) {
							if (record.sessionlimit == -1){
								return "rankItem1";
							}
							if (record.sessions == record.sessionlimit ){
								return "rankItem4";
							}
							if (record.sessions >= record.sessionlimit-3){
								return "rankItem3";
							}
							return "rankItem1";
						} else {
							return this.Super("getBaseStyle", arguments);
						}
				},
				recordDoubleClick: function(){			
					editRepository();
					var record = this.getSelectedRecord();
					if (record === null) {
							return
					}
					repositoryForm.setData(record);
				}
	});
	
	var repButtons = isc.HLayout.create( {
		height : 30,
		membersMargin : 5,
		layoutMargin : 5,
		backgroundColor: ctrlColor,
		members : [isc.Label.create({width:"100%"}),
		           isc.IButton.create( {
		        	   title : "Refresh",
		        	   width:140,
		        	   autoDraw : false,
		        	   click : function(form, item, value) {
		        		   getRepositories();
		        	   }
		           }), isc.IButton.create( {
		        	   title : "Edit Repository",
		        	   width:140,
		        	   autoDraw : false,
		        	   click : function(form, item, value) {
							editRepository();
							var record = RepGrid.getSelectedRecord();
							if (record === null) {
									return
							}
							repositoryForm.setData(record);
		        	   }
		           }), isc.IButton.create( {
		        	   title : "Populate With Test Data",
		        	   width:145,
		        	   autoDraw : false,
		        	   click : function(form, item, value) {
		        		   seedRepository();
		        	   }
		           }), isc.IButton.create( {
		        	   title : "New Repository",
		        	   width:140,
		        	   autoDraw : false,
		        	   click : function(form, item, value) {
		        		   newRepository();
		        	   }
		           }), isc.IButton.create( {
		        	   title : "Delete Reposiotry",
		        	   width:140,
		        	   autoDraw : false,
		        	   click : function(form, item, value) {
		        			isc.RPCManager.sendRequest( {
		        				evalResult : false,
		        				params : {
				  				"repURL":RepGrid.getSelectedRecord().url, 
		        				"NONCACHE":Math.random()
		        			},
		        			callback : function(rpcResponse, dataJS, rpcRequest) {
		        				data = JSON.parse(dataJS,dateParser);
		        				reps = data[0];
		        				orgUsers = data[1];
		        				repMap.clear();
		        				for (var i = 0; i < reps.length; i++){
		        					repMap.put(reps[i].repID, reps[i]);
		        				}
		        				getRepositoryEditForm();
		        				RepGrid.setData(reps);
		        			},
		        			actionURL : "./deleteRepository"
		        			});
						}
		        	   }
		           )]
	});

	RepositoryPane.addMember(RepGrid);
	RepositoryPane.addMember(repButtons);
	
	return RepositoryPane;
}
function defineServerControlPane(){
	isc.VLayout.create({
		membersMargin:5,
		width:"100%",
		height:"100%",
		autoDraw:true,
		backgroundColor: ctrlColor,
		ID:"ServerControlPane"
	});
	
	isc.HLayout.create({
		membersMargin:5,
		width:"100%",
		height:"100%",
		autoDraw:true,
		backgroundColor: ctrlColor,
		ID:"mainH"
	});

	ServerControlPane.addMember(mainH);

	
	isc.VLayout.create({
		membersMargin:5,
		width:350,
		height:"100%",
		autoDraw:true,
		backgroundColor: ctrlColor,
		ID:"leftV"
	});
	
	mainH.addMember(leftV);

	isc.VLayout.create({
		membersMargin:5,
		width:650,
		height:"100%",
		autoDraw:true,
		backgroundColor: ctrlColor,
		ID:"leftV2"
	});

	mainH.addMember(leftV2);
	
	isc.HLayout.create({
		membersMargin:5,
		width:800,
		height:50,
		autoDraw:true,
		backgroundColor: ctrlColor,
		ID:"leftV3"
	});

//	mainH.addMember(leftV3);
	mainH.addMember(getSessionPane());

	isc.ListGrid.create({
		ID:"QueueGrid",
		width:"100%",
		height:150,
		alternateRecordStyles : true,
		fields : [
		          {name:"method", showTitle:true, title:"Queue",type : "text",width:"100%"}, 
		          {name:"count", showTitle:true, title:"Length",type : "text",width:100, align:"center"}]
	});	
	isc.ListGrid.create({
		ID:"TransactionGrid",
		width:"100%",
		height:"100%",
		alternateRecordStyles : true,
		sortFieldNum: 1,
		sortDirection: "descending",
		fields : [
		          {name:"method", showTitle:true, title:"Method",type : "text",width:"100%"}, 
		          {name:"count", showTitle:true, title:"Count",type : "text",width:100, align:"center"}]
	});
	
	isc.ListGrid.create({
		ID:"PeopleGrid",
		width:"100%",
		height:"100%",
		dataSource:allUsersDS,
		autoFetch:true,
		alternateRecordStyles : true,
		fields : [
		          {name:"name", showTitle:true, title:"Name",type : "text",width:150}, 
		          {name:"email", showTitle:true, title:"Email",type : "text",width:"100%"},
		          {name:"allowLogon", showTitle:true, title:"Allow Logon",type : "boolean",width:100}]
	});
	
	var pplButtons = isc.HLayout.create( {
		height : 30,
		membersMargin : 5,
		layoutMargin : 5,
		backgroundColor: ctrlColor,
		members : [isc.Label.create({width:"100%"}),
		           isc.IButton.create( {
		        	   title : "Refresh",
		        	   width:130,
		        	   autoDraw : false,
		        	   click : function(form, item, value) {
		        		   PeopleGrid.fetchData({nocache:Math.random()});		        	   }
		           }),isc.IButton.create( {
		        	   title : "Delete",
		        	   width:130,
		        	   autoDraw : false,
		        	   click : function(form, item, value) {
		        		   deleteUser();
		        	   }
		           }), isc.IButton.create( {
		        	   title : "Disable Logon",
		        	   width:130,
		        	   autoDraw : false,
		        	   click : function(form, item, value) {
		        		   disableLogon();
		        	   }
		           }), isc.IButton.create( {
		        	   title : "Enable Logon",
		        	   width:130,
		        	   autoDraw : false,
		        	   click : function(form, item, value) {
		        		   enableLogon();
		        	   }
		           })]
	});


	
	
	var buttons = isc.HLayout.create( {
		height : 30,
		membersMargin : 5,
		layoutMargin : 5,
		backgroundColor: ctrlColor,
		members : [isc.Label.create({width:"100%"}),
		           isc.IButton.create( {
		        	   title : "Refresh",
		        	   width:140,
		        	   autoDraw : false,
		        	   click : function(form, item, value) {
		        		   refreshServerControl();
		        	   }
		           })]
	});

	leftV.addMember(isc.Label.create({height:25,contents:"<b>Work Queues</b>"}));
	leftV.addMember(QueueGrid);
	leftV.addMember(isc.Label.create({height:25,contents:"<b>Server Functions</b>"}));
	leftV.addMember(TransactionGrid);

	leftV2.addMember(isc.Label.create({height:25,contents:"<b>Stakeholders</b>"}));
	leftV2.addMember(PeopleGrid);
	leftV2.addMember(pplButtons);

	leftV3.addMember(isc.DynamicForm.create({
		numCols:4,
		width:600,
		titleWidth:200,
		padding:10,
	    fields: [
	        {name: "message",type: "text", width:300, title:"Broadcast Message", required: true},
	        {type:"button", startRow:false, endRow:true, name:"Send", align:"right",click:"sendBroadcastMessage(this.form.getItem('message').getValue())"}
	        ]
	}));
	
	ServerControlPane.addMember(leftV3);
	ServerControlPane.addMember(buttons);
	
	return ServerControlPane;
}
function defineScheduledJobPane(){

	var schedReportTable = isc.ListGrid.create( {
		autoDraw : false,
		ID:"schedJobTable",
		selectionType: "single",
		headerHeight: 40,
		modalEditing: true,
		alternateRecordStyles : true,
		height : "100%",
		width:"100%",
		canEdit:false,
		emptyMessage : "No Scheduled Reports",
		updateJobScheduleTable:function(){
			isc.RPCManager.showPrompt = false;
			prompt:"Retrieving Scheduled Jobs",
			isc.RPCManager.sendRequest( {
				evalResult : true,
				params : {
					"OPERATION" : "getUserScheduledJobs"
				},
				callback : function(rpcResponse, jobs, rpcRequest) {

					try {
						var d = new Date();
						var len=jobs.length;
						for(var i=0; i<len; i++) {
							var record = jobs[i];
							var timeBits = record.timeStr.split(":");
							var hour = parseInt(timeBits[0],10);
							var min = parseInt(timeBits[1],10);
							d.setUTCHours(hour);
							d.setUTCMinutes(min);
							d.setUTCSeconds(0);					
							record.timeStr = d.toLocaleTimeString();
						}
						schedJobTable.setData(jobs);
					} catch(e){
						alert(e.message);
					}
				},
				actionURL : "./QRMServer"
			});
		},
		recordDoubleClick: function(){	
			getScheduleReportWindow().show();
			scheduleReportForm.setValues(schedJobTable.getSelectedRecord());
			DescendantForm2.setValues(schedJobTable.getSelectedRecord());
			QRMNavigator2.selectRecord(schedJobTable.getSelectedRecord());
			var index = -1;
			try {
			for (var i = 0; QRMNavigator2.getRecord(i).projectID != schedJobTable.getSelectedRecord().projectID &&  i < 1000; i++ ){
				index++;
			}
			} catch (e){
				// can happen if 
			}
			QRMNavigator2.selectSingleRecord(index+1);
			
		},
		getBaseStyle: function(record, rowNum, colNum){
			if (colNum == 1 && (record.reportID == -50000 || record.reportID == -50001)) {
				return "qrmRiskExportEntry";
			}
			return this.Super("getBaseStyle", arguments);
		},
		fields : [{
			name:"description", 
			title:"Description",
			type: "text",
			width:150,
			align:"left"
		},{
			name : "reportID",
			title : "Report Type",
			type:"select",
			width : 170,
			align : "center"
		},{
			name : "projectID",
			title : "Project",
			type:"select",
			width : 130,
			align : "center"
		},{
			name : "descendants",
			title : "Desc",
			type:"boolean",
			width : 50,
			align : "center"
		},{ 
			name:"Mon",
			type:"boolean",
			width:40
		},{ 
			name:"Tue",
			type:"boolean",
			width:40
		},{ 
			name:"Wed",
			type:"boolean",
			width:40
		},{ 
			name:"Thu",
			type:"boolean",
			width:40
		},{ 
			name:"Fri",
			type:"boolean",
			width:40
		},{ 
			name:"Sat",
			type:"boolean",
			width:40
		},{ 
			name:"Sun",
			type:"boolean",
			width:40
		},{ 
			title:"Time",
			name:"timeStr",
			type:"text",
			width:80,
			align:"center"
		},{ 
			title:"Email",
			name:"email",
			type:"boolean",
			width:50,
			align:"center"
		},{
			name:"database", 
			title:"Organisation",
			type: "text",
			width:150,
			align:"center"
		},{
			name:"databaseUser", 
			title:"Database URL",
			type: "text",
			width:"100%",
			align:"left"
		}],
		headerSpans: [{
			fields: ["Mon","Tue", "Wed", "Thu","Fri", "Sat", "Sun"], 
			title: "Days to Run"
		}]

	});
	
	var buttons = isc.HLayout.create( {
		height : 30,
		membersMargin : 5,
		layoutMargin : 5,
		backgroundColor: ctrlColor,
		members : [isc.Label.create({width:"100%"}),
		           isc.IButton.create( {
		        	   title : "Refresh",
		        	   width:140,
		        	   autoDraw : false,
		        	   click : function(form, item, value) {
		        		   getScheduledJobs();
		        	   }
		           }), isc.IButton.create( {
		        	   title : "Delete Job",
		        	   width:140,
		        	   autoDraw : false,
		        	   click : function(form, item, value) {
		        		   deleteJob();
		        	   }
		           })]
	});
	
	var pane = isc.VLayout.create({
		backgroundColor: ctrlColor,
		membersMargin: 2,
		layoutMargin: 2,
		layoutTopMargin: 2,
		height: "100%",
		width: "100%",
		members: [schedReportTable, buttons],
		layoutBottomMargin: 2,
		layoutRightMargin: 2,
		layoutLeftMargin: 2,
		autoDraw: true
	});
	
	return pane;
}
function defineCompletedJobPane(){

	isc.ListGrid.create( {
		ID:"completedJobTable",
		autoDraw : false,
		alternateRecordStyles : true,
		height : "100%",
		emptyMessage : "No Report or Analysis Jobs Found",
		fields : [{
			name:"jobID", 
			title:"Job ID",
			type: "text",
			width:80,
			align:"center"
		},{
			name : "collected",
			title : "Viewed",
			type:"boolean",
			width : "70",
			align : "center"
		},{
			name : "jobDescription",
			title : "Description",
			width : 200,
			align : "left"
		}, {
			name : "queuedDate",
			title : "Queued Date",
			width : 200,
			align : "center", type:"time",
			formatCellValue: function (value) {
				if (value) {
					return value.toDateString ()+"  "+value.toLocaleTimeString ();
				}
			}
		}, {
			name : "executedDate",
			title : "Completed",
			width : 200,
			align : "center", type:"date",
			formatCellValue: function (value) {
				if (value) {
					return value.toDateString ()+"  "+value.toLocaleTimeString ();
				} else {
					return "Pending";
				}
			}
		},{
			name : "jobJdbcURL",
			title : "Database URL",
			width : 400,
			align : "left"
		}]});	
	
	var buttons = isc.HLayout.create( {
		height : 30,
		backgroundColor: ctrlColor,
		membersMargin : 5,
		layoutMargin : 5,
		backgroundColor: ctrlColor,
		members : [isc.Label.create({width:"100%"}),
		           isc.IButton.create( {
		        	   title : "Refresh",
		        	   width:140,
		        	   autoDraw : false,
		        	   click : function(form, item, value) {
		        		   getCompletedJobResult();
		        	   }
		           }), isc.IButton.create( {
		        	   title : "Delete Two Week Old",
		        	   width:140,
		        	   autoDraw : false,
		        	   click : function(form, item, value) {
		        		   deleteJobResult(14);
		        	   }
		           }), isc.IButton.create( {
		        	   title : "Delete Two Day Old",
		        	   width:140,
		        	   autoDraw : false,
		        	   click : function(form, item, value) {
		        		   deleteJobResult(2);
		        	   }
		           }), isc.IButton.create( {
		        	   title : "Delete Job",
		        	   width:140,
		        	   autoDraw : false,
		        	   click : function(form, item, value) {
		        			isc.RPCManager.sendRequest( {
		        				evalResult : false,
		        				params : {
		        				"OPERATION" : "deleteCompletedJob",
		        				"jobID": completedJobTable.getSelectedRecord().jobID,
		        				"url":completedJobTable.getSelectedRecord().jobJdbcURL,
		        				"NONCACHE":Math.random()
		        			},
		        			callback : function(rpcResponse, dataJS, rpcRequest) {
		        				try {
		        					completedJobTable.setData(JSON.parse(dataJS, dateParserDebug));
		        				} catch (e){alert (e.message);}
		        			},
		        			actionURL : "./sessionControl"
		        			});
		        	   }
		           })]
	});
	
	var pane = isc.VLayout.create({
		backgroundColor: ctrlColor,
		membersMargin: 2,
		layoutMargin: 2,
		layoutTopMargin: 2,
		height: "100%",
		width: "100%",
		members: [completedJobTable, buttons],
		layoutBottomMargin: 2,
		layoutRightMargin: 2,
		layoutLeftMargin: 2,
		autoDraw: true
	});
	
	return pane;
}

function disableLogon(){
	isc.RPCManager.sendRequest( {
		evalResult : false,
		params : {
		"stakeholderID":PeopleGrid.getSelectedRecord().stakeholderID,
		"NONCACHE":Math.random()
	},
	callback : function(rpcResponse, dataJS, rpcRequest) {
		refreshServerControl();
		PeopleGrid.fetchData({nocache:Math.random()});
	},
	actionURL : "./disableLogon"
	});	
}
function enableLogon(){
	isc.RPCManager.sendRequest( {
		evalResult : false,
		params : {
		"stakeholderID":PeopleGrid.getSelectedRecord().stakeholderID,
		"NONCACHE":Math.random()
	},
	callback : function(rpcResponse, dataJS, rpcRequest) {
		refreshServerControl();
		PeopleGrid.fetchData({nocache:Math.random()});
	},
	actionURL : "./enableLogon"
	});	
}
function getSessionPane(){
	
	isc.ListGrid.create({
		ID:"SessionGrid",
		width:"100%",
		height:"100%",
		timeFormatter:"toLong24HourTime",
		alternateRecordStyles : true,
		fields : [{
					 name:"name", 
					 title:"User Name",
					 type : "text",
					 width:200
					},{
					  name : "orgName",
		        	  width : "100",
		        	  title : "Organisation"
		          }, {
		        	  name : "dbUser",
		        	  width : "100",
		        	  title : "Database User",
		        	  type : "text"
		          }, {
		        	  name : "sessionID",
		        	  width : "220",
		        	  title : "Session ID",
		        	  type : "text"
		          }, {
		        	  name : "lastAccess",
		        	  width : "180",
		        	  title : "Last Access",
		        	  type : "time"
		          }, {
		        	  name : "numTransactions",
		        	  width : "70",
		        	  title : "Transactions",
		        	  type : "text",
		        	  align:"center"
		          }, {
		        	  name : "sessionEnabled",
		        	  width : "120",
		        	  title : "Session Enabled",
		        	  required : true,
		        	  type : "boolean"
		          }, {
		        	  name : "remoteHost",
		        	  width : "220",
		        	  title : "Remote Host",
		        	  type : "text"
		          }, {
		        	  name : "remoteAddr",
		        	  width : "220",
		        	  title : "Remote Address",
		        	  type : "text"
		          }]
	});
	
	var sessionButtons = isc.HLayout.create( {
		height : 30,
		membersMargin : 5,
		layoutMargin : 5,
		backgroundColor: ctrlColor,
		members : [isc.Label.create({width:"100%"}),
		           isc.IButton.create( {
		        	   title : "Refresh",
		        	   width:140,
		        	   autoDraw : false,
		        	   click : function(form, item, value) {
		        		   getSessions();
		        	   }
		           }), isc.IButton.create( {
		        	   title : "Disable Session",
		        	   width:140,
		        	   autoDraw : false,
		        	   click : function(form, item, value) {
		        		   disableSession();
		        	   }
		           }), isc.IButton.create( {
		        	   title : "Clear Defunct Sessions",
		        	   width:140,
		        	   autoDraw : false,
		        	   click : function(form, item, value) {
		        		   clearDefunct();
		        	   }
		           })]
	});
	
	var pane = isc.VLayout.create({
		backgroundColor: ctrlColor,
		membersMargin: 2,
		layoutMargin: 2,
		layoutTopMargin: 2,
		height: "100%",
		width: "100%",
		members: [isc.Label.create({height:25,contents:"<b>Active Sessions</b>"}),SessionGrid, sessionButtons],
		layoutBottomMargin: 2,
		layoutRightMargin: 2,
		layoutLeftMargin: 2,
		autoDraw: true
	});
	
	return pane;
}
function getRepositories(){
	isc.RPCManager.sendRequest( {
		evalResult : false,
		params : {
		"NONCACHE":Math.random()
	},
	callback : function(rpcResponse, dataJS, rpcRequest) {
		data = JSON.parse(dataJS,dateParser);
		reps = data[0];
		orgUsers = data[1];
		repMap.clear();
		for (var i = 0; i < reps.length; i++){
			repMap.put(reps[i].repID, reps[i]);
		}
		getRepositoryEditForm();
		RepGrid.setData(reps);
	},
	actionURL : "./getAllRepositories"
	});
}
function getSessions(){
	isc.RPCManager.sendRequest( {
		evalResult : false,
		params : {
		"OPERATION" : "getAllSessions",
		"NONCACHE":Math.random()
	},
	callback : function(rpcResponse, dataJS, rpcRequest) {
		SessionGrid.setData(JSON.parse(dataJS,dateParserTime2));
	},
	actionURL : "./sessionControl"
	});
}
function getScheduledJobs(){
	isc.RPCManager.sendRequest( {
		evalResult : false,
		params : {
		"OPERATION" : "getAllScheduledJobs",
		"NONCACHE":Math.random()
	},
	callback : function(rpcResponse, dataJS, rpcRequest) {
		try {
		schedJobTable.setData(JSON.parse(dataJS));
		} catch (e){alert (e.message);}
	},
	actionURL : "./sessionControl"
	});
}
function deleteJob(){
	isc.RPCManager.sendRequest( {
		evalResult : false,
		params : {
		"OPERATION" : "deleteJob",
		"jobID":schedJobTable.getSelectedRecord().internalID,
		"repository":schedJobTable.getSelectedRecord().repository,
		"NONCACHE":Math.random()
	},
	callback : function(rpcResponse, dataJS, rpcRequest) {
		try {
		schedJobTable.setData(JSON.parse(dataJS));
		} catch (e){alert (e.message);}
	},
	actionURL : "./sessionControl"
	});
}
function disableSession(){
	isc.RPCManager.sendRequest( {
		evalResult : false,
		params : {
		"OPERATION" : "disableSession",
		"sessionID" : SessionGrid.getSelectedRecord().sessionID,
		"NONCACHE":Math.random()
	},
	callback : function(rpcResponse, dataJS, rpcRequest) {
		SessionGrid.setData(JSON.parse(dataJS));
	},
	actionURL : "./sessionControl"
	});
}
function clearDefunct(){
	isc.RPCManager.sendRequest( {
		evalResult : false,
		params : {
		"OPERATION" : "clearDefunct",
		"NONCACHE":Math.random()
	},
	callback : function(rpcResponse, dataJS, rpcRequest) {
		SessionGrid.setData(JSON.parse(dataJS));
	},
	actionURL : "./sessionControl"
	});
}
function getRepositoryEditForm(){
	
	isc.DynamicForm.create( {
		ID : "repositoryForm",
		numCols : 2,
		autoDraw : false,
		titleWidth : 250,
		padding:10,
		hiliteRequiredFields : true,
		width : "100%",
		fields : [{
		        	type:"header", defaultValue:"Edit Repository"
		         },{
		        	 name:"rep", 
		        	 showTitle:true, 
		        	 editorType:"text", 
		        	 title:"Repository",
		        	 width:240, 
		        	 required : true
  	        	},{
		        	  name : "repmgr",
		        	  width : 320,
		        	  title : "Repository Risk Manager",
		        	  required : true,
		        	  type : "comboBox",
		        	  allowEmptyValue : false,
		        	  startRow : true,
		        	  optionDataSource: "allUsersDS", 
		        	  pickListWidth : 320,
		        	  displayField:"name",
		        	  valueField : "stakeholderID"
		          },{
		        	  name : "url",
		        	  width : "600",
		        	  title : "Access URL",
		        	  required : true,
		        	  type : "text",
		        	  allowEmptyValue : false,
		        	  startRow : true
		          },{
		        	  name : "active",
		        	  width : "200",
		        	  title : "Active",
		        	  required : true,
		        	  type : "boolean",
		        	  allowEmptyValue : false,
		        	  startRow : true
		          },{
		        	  name : "autoAddUsers",
		        	  width : "200",
		        	  title : "Auto Add Users",
		        	  required : true,
		        	  type : "boolean",
		        	  allowEmptyValue : false,
		        	  startRow : true
		          },{
		        	  name : "sessionlimit",
		        	  width : "200",
		        	  title : "Session Limit",
		        	  required : true,
		        	  editorType: "spinner", 
		        	  min: -1, max: 100, step: 1, width: 70,
		        	  startRow : true
		          },{
		        	  name : "userlimit",
		        	  width : "200",
		        	  title : "User Limit",
		        	  required : true,
		        	  editorType: "spinner", 
		        	  min: -1, max: 100, step: 1, width: 70,
		        	  startRow : true
		          },{
		        	  name : "repLogonMessage",
		        	  width : "600",
		        	  height:"150",
		        	  title : "Logon Message",
		        	  required : true,
		        	  type : "textArea",
		        	  allowEmptyValue : true,
		        	  startRow : true
		          },{
		        	  name: "validateBtn", 
		        	  title: "Save Changes", 
		        	  type: "button", 
		        	  colSpan:2, 
		        	  endRow:true, 
		        	  align:"right",
		        	  click:function(){
		        	  	isc.RPCManager.sendRequest({
		        	  		actionURL: "./updateRep",
		        	  		evalResult: true,
		        	  		params: {
		        	  			"DATA": JSON.stringify(repositoryForm.getValues())
		  					},
		  					callback: function(rpcResponse, data2, rpcRequest){
		  						alert(data2);
		  						modalEditRepWindow.hide();
		  						getRepositories();
			  				}
						});
		          	}
		       
		          }]
	});
	
	return repositoryForm;

}
function editRepository(){

		isc.Window.create({
			ID: "modalEditRepWindow",
			title: "Edit Repository",
			width:900,
			height:500,
			autoCenter: true,
	    	isModal: true,
	    	showModalMask: true,
	    	showMinimizeButton:false,
	    	autoDraw: false,
	    	closeClick : function () { this.Super("closeClick", arguments);},
	    	items: [getRepositoryEditForm()]
		});
	modalEditRepWindow.show();
}
function seedRepository(){

	if (typeof (modalSeedRepWindow) == "undefined"){
		isc.Window.create({
			ID: "modalSeedRepWindow",
			title: "Seed Reoository",
			width:500,
			height:320,
			autoCenter: true,
	    	isModal: true,
	    	showModalMask: true,
	    	showMinimizeButton:false,
	    	autoDraw: false,
	    	closeClick : function () { this.Super("closeClick", arguments);},
	    	items: [getSeedForm()]
		});
	}
	modalSeedRepWindow.show();
}
function getSeedForm(){
	isc.DynamicForm.create( {
		ID : "seedForm",
		numCols : 2,
		autoDraw : false,
		titleWidth : 250,
		padding:10,
		width : "100%",
		fields : [{
        	type:"header", defaultValue:"Seed Repository"
        	}, { 
 			   name: "createProjectStructure", title: "Create Project Structure", type:"boolean", defaultValue:true
        	}, { 
  			   name: "createTestUsers", title: "Create Test Users", type:"boolean", defaultValue:true
	        },{
			   name: "numRisks", title: "Number of Risks", editorType: "spinner", defaultValue: 0, min: 0, max: 1000, step: 10
	        },{
			   name: "maxNumControlsPerRisk", title: "Max Number of Controls", editorType: "spinner", defaultValue: 5,  min: 0, max: 20, step: 1
			},{
			   name: "maxNumberObjectivesPerRisk", title: "Max Number of Objectives", editorType: "spinner", defaultValue: 5, min: 0, max: 20, step: 1
		   },{
			  name: "maxNumberMitigationStepsPerRisk", title: "Max Number of Mitigation", editorType: "spinner", defaultValue: 10, min: 0, max: 20, step: 1
			},{
			   name: "maxNumberCommentsPerRisk", title: "Max Number of Comments", editorType: "spinner", defaultValue: 5, min: 0, max: 20, step: 1
		  },{
			   name: "maxNumberConsequences", title: "Max Number of Consequences", editorType: "spinner", defaultValue: 5, min: 0, max: 20, step: 1
          },{
				_constructor: "RowSpacerItem"
		  },{
        	  name: "validateBtn", 
        	  title: "Seed Repository", 
        	  type: "button", 
        	  colSpan:2, 
        	  endRow:true, 
        	  align:"right",
        	  click:function(){
		  		isc.RPCManager.sendRequest({
		  			actionURL: "./normaliseProject",
		  			showPrompt:true,
		  			prompt:"Seeding Repository with Data...Please Standby",
		  			evalResult: true,
		  			params: {
		  				"repURL":RepGrid.getSelectedRecord().url, 
		  				"repDBUser":"",
		  				"createProjectStructure":seedForm.getItem("createProjectStructure").getValue(),
		  				"createTestUsers":seedForm.getItem("createTestUsers").getValue(),
		  				"maxNumControlsPerRisk":seedForm.getItem("maxNumControlsPerRisk").getValue(),
		  				"maxNumberObjectivesPerRisk":seedForm.getItem("maxNumberObjectivesPerRisk").getValue(),
		  				"maxNumberMitigationStepsPerRisk":seedForm.getItem("maxNumberMitigationStepsPerRisk").getValue(),
		  				"maxNumberCommentsPerRisk":seedForm.getItem("maxNumberCommentsPerRisk").getValue(),
		  				"numRisks":seedForm.getItem("numRisks").getValue(),
		  				"maxNumberConsequences":seedForm.getItem("maxNumberConsequences").getValue()
		  		},
		  		callback: function(rpcResponse, list, rpcRequest){
		  			alert("Population of Data Complete");
		  			modalSeedRepWindow.hide();
		  		}
		  		});
          	}
          }
		]
	});
	
	return seedForm;

}
function newRepository(){
	isc.DynamicForm.create({
		width:550,
		titleWidth:180,
		padding:10,
	    ID: "addRepositoryForm",
	    fields: [
	        {type:"header", defaultValue:"Create New Repository"},
	          {name:"repTitle", showTitle:true, editorType:"text", title:"Repository",width:240, required : true},
	          {name:"reporgcode", showTitle:true, editorType:"text", title:"Organisation Code",width:240, required : true},
            {name:"RowSpacerItem0", _constructor:"RowSpacerItem"},
	        	{
	        	  name : "repmgr",
	        	  width : 320,
	        	  title : "Repository Risk Manager",
	        	  type : "comboBox",
	        	  allowEmptyValue : false,
	        	  optionDataSource: "allUsersDS", 
	        	  pickListWidth : 320,
	        	  displayField:"compoundName",
	        	  valueField : "stakeholderID"
	        	},
	        	 {_constructor:"StaticTextItem", defaultValue:"or", title:""},
	        	{
		        	  name : "repname",
		        	  width : 320,
		        	  title : "New Risk Manager Name",
		        	  type : "text",
		        	  allowEmptyValue : false,
		        	  startRow : true
	        	}, {
		        	  name : "repemail",
		        	  width : 320,
		        	  title : "New Risk Manager Email",
		        	  type : "text",
		        	  allowEmptyValue : false,
		        	  startRow : true
	        	}, {
		        	  name : "reppass",
		        	  width : 320,
		        	  title : "New Risk Manager Password",
		        	  type : "text",
		        	  allowEmptyValue : false,
		        	  startRow : true
		       },
		       {name:"RowSpacerItem0", _constructor:"RowSpacerItem"},

	          {name: "validateBtn", title: "Submit", type: "button", align:"right", colSpan:2, 
	        	click: function(){
	        	  try{
	        		if (addRepositoryForm.validate()){
	        			isc.RPCManager.sendRequest({
	        				prompt:"Adding Repository",
	        				evalResult: true,
	        				params: {
	        				"OPERATION": "addRepository",
	        				"DATA":JSON.stringify(addRepositoryForm.getValues())
	        			},
	        			callback: function(rpcResponse, data2, rpcRequest){
	        				alert(data2);
	        				getRepositories();
	     	        		modalAddRepWindow.hide();
	        			},
	        			actionURL: "./addRepository"
	        			});
	        		} else {
	        			alert("Not Valid for some reason");
	        		}
	            } catch (e){alert(e.message);}
	          }
	        }
	        ]
	});
	
	isc.Window.create({
	    ID: "modalAddRepWindow",
	    title: "New Repository",
	    autoSize:true,
	    autoCenter: true,
	    isModal: true,
	    showModalMask: true,
	    showMinimizeButton:false,
	    autoDraw: false,
	    closeClick : function () { this.Super("closeClick", arguments);},
	    items: [addRepositoryForm]
	});

	modalAddRepWindow.show();

}
function sendBroadcastMessage(value){
	
	isc.RPCManager.sendRequest({
		actionURL: './sessionControl',
		evalResult: true,
		params: {"OPERATION": "sendBroadcastMessage","DATA": value}
	});
	broadcastWindow.hide();
}
function dateParser(key, value) {
	var a;
	if (typeof value === 'string') {
		a = /^(\d{4})-(\d{2})-(\d{2})/.exec(value);
		if (a) {
			return new Date(Date.UTC(+a[1], +a[2] - 1, +a[3]));
		}
	}
	return value;
}
function dateParserTime2(key, value) {
	var a;
	if (typeof value === 'string') {
		a = /^(\d{4})-(\d{2})-(\d{2}) (\d{2}):(\d{2}):(\d{2}).(\d{3})/.exec(value);
		if (a) {
			return new Date(a[1], +a[2] - 1, +a[3], a[4],a[5],a[6],a[7]);
		}
	}
	return value;
}
function dateParserDebug(key, value) {
	var a;
	if (typeof value === 'string') {
		a = /^(\d{4})-(\d{2})-(\d{2}) (\d{2}):(\d{2}):(\d{2})/.exec(value);
		if (a) {
			return  new Date(+a[1], +a[2] - 1, +a[3], +a[4], +a[5], +a[6], 0);
		}

		a = /^(\d{4})-(\d{2})-(\d{2})/.exec(value);
		if (a) {
			return new Date(Date.UTC(+a[1], +a[2] - 1, +a[3]));
		}
	}
	return value;
}
function deleteJobResult(interval){
	isc.RPCManager.sendRequest( {
		evalResult : false,
		params : {
		"OPERATION" : "deleteJobResult",
		"DAYINTERVAL":interval,
		"NONCACHE":Math.random()
	},
	callback : function(rpcResponse, dataJS, rpcRequest) {
		try {
			completedJobTable.setData(JSON.parse(dataJS, dateParserDebug));
		} catch (e){alert (e.message);}
	},
	actionURL : "./sessionControl"
	});
}
function getCompletedJobResult(){
	isc.RPCManager.sendRequest( {
		evalResult : false,
		params : {
		"OPERATION" : "getCompletedJobResult",
		"NONCACHE":Math.random()
	},
	callback : function(rpcResponse, dataJS, rpcRequest) {
		try {
			completedJobTable.setData(JSON.parse(dataJS, dateParserDebug));
		} catch (e){alert (e.message);}
	},
	actionURL : "./sessionControl"
	});
}
function getWorkQueueLengths(){
	isc.RPCManager.sendRequest( {
		evalResult : false,
		params : {
		"NONCACHE":Math.random()
	},
	callback : function(rpcResponse, dataJS, rpcRequest) {
		var queueLengths = JSON.parse(dataJS);
		workQueueHTML="<table border=\"1\"><tr><td style=\"width:250px\" align=\"left\"><b>Queue</b></td><td style=\"width:70px\" align=\"center\"><b>Length</b></td></tr>";
		workQueueHTML=workQueueHTML+"<tr><td align=\"left\">Email Queue</td><td align=\"center\">"+queueLengths[0]+"</td></tr>";
		workQueueHTML=workQueueHTML+"<tr><td align=\"left\">Report Queue</td><td align=\"center\">"+queueLengths[1]+"</td></tr>";
		workQueueHTML=workQueueHTML+"<tr><td align=\"left\">Monte Carlo Queue</td><td align=\"center\">"+queueLengths[2]+"</td></tr>";
		workQueueHTML=workQueueHTML+"</table>";
		workQueuesPane.markForRedraw();
	},
	actionURL : "./getWorkQueueLengths"
	});	
}
function init(){
		isc.RestDataSource.create({
		    ID: "allUsersDS",
		    fields: [{
		        name: "name",
		        title: "Name"
		    },{
		    	name:"email",
		    	title:"Email"
		    },{
		    	name:"compoundName",
		    	title:"Compound Name"
		    },{
		    	name:"allowLogon",
		    	title:"Allow Logon",
		    	type:"boolean"
		    }
		],
		   fetchDataURL: "./getAllUsersDS"
		});
		repMap = new Map();
		constructRepMgrApp();
		getRepositories();
		refresh();
		PeopleGrid.fetchData({nocache:Math.random()});
		try {
		//	setInterval(refresh, 10000);
		} catch (e){
			alert (e.message);
		}
}
function refreshServerControl(){
	
	isc.RPCManager.sendRequest( {
		evalResult : false,
		params : {
		"NONCACHE":Math.random()
	},
	callback : function(rpcResponse, dataJS, rpcRequest) {
		try {
			QueueGrid.setData(JSON.parse(dataJS));
		} catch (e){alert (e.message);}
	},
	actionURL : "./getWorkQueueLengths"
	});	
	isc.RPCManager.sendRequest( {
		evalResult : false,
		params : {
		"OPERATION" : "getTransactionUsage",
		"NONCACHE":Math.random()
	},
	callback : function(rpcResponse, dataJS, rpcRequest) {
		try {
			TransactionGrid.setData(JSON.parse(dataJS));
		} catch (e){alert (e.message);}
	},
	actionURL : "./sessionControl"
	});	
	
	getSessions();
	
	
}
function refresh(){
	getSessions();
	getScheduledJobs();
	getCompletedJobResult();
	refreshServerControl();
}

// Patch tofix HTMLFlow Problem in SmarClient 8.0

if (window.isc != null) {
	if (isc.version.startsWith("SC_SNAPSHOT-2011-01-05/")) {
		if (isc.HTMLFlow != null) {
			isc.HTMLFlow.addProperties({modifyContent:function () {
				//
			}});
		}
	}
}

// End of Patch
var workQueueHTML;
init();
