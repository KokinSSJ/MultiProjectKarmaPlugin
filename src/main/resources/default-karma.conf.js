module.exports = function(config) { 
function mergeFilesWithArgv(staticFiles) {

    var source = staticFiles, argv = process.argv;

    argv.forEach(function (arg) {
        var index = arg.indexOf('--file=');
		if(index !== -1) {
			var elements = arg.split('--file=');
			elements.forEach(function(element) {
			 if(element !== "") {
				 source.push(element);
			 }
			}); 
		}    
    });  
	console.log("KARMA Actual sources: ");
	console.log(source);
    return source;

}


config.set({
    "basePath": "",
    "colors": true,
    "logLevel": "INFO",
    "files": mergeFilesWithArgv([
        "jquery.js"
    ]),
    "browsers": [
        "PhantomJS" 
    ],
    "frameworks": [
        "jasmine" 
    ],
    "reporters": [
        "junit",
        "spec"
    ],
    "preprocessors": {
        
    },
    "specReporter": {
        maxLogLines: 5, 
        suppressFailed: false, 
        suppressPassed: true, 
        showSpecTiming: false,  
        failFast: false  
    },
    "exclude": [
        
    ],
    "junitReporter": {
        "outputDir": "test-dir"
    },
    "concurency": "Infinity"
}) };