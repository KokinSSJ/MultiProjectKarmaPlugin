package com.db

class ConfigParser {
	
	File karmaConfigProperties;
	final static INCORRECT_PROPERTIES = "karma.conf.properties incorrectly specified for ";
	
	ConfigParser(File karmaConfigProperties) {
		this.karmaConfigProperties = karmaConfigProperties;
	}
	
	Map<String, List<String>> parseConfigProperties() {
		def map = new HashMap<>();
		def parsedJson = new groovy.json.JsonSlurper().parseText(karmaConfigProperties.text)
		parsedJson.forEach { firstLevelKey, firstLevelValue ->
			parseFirstJsonLevel(map, firstLevelKey, firstLevelValue)
		}
		return map;
	}

	void parseFirstJsonLevel(HashMap map, firstLevelKey, firstLevelValue) {
		if(!isDescription(firstLevelKey)  && isMap(firstLevelKey, firstLevelValue)) {
			def list = new ArrayList<>() 
			map.put(firstLevelKey, list) 
			firstLevelValue.forEach {secondLevelKey, secondLevelValue ->
				parseSecondJsonLevel(list, firstLevelKey, secondLevelKey,secondLevelValue)
			}
			if(list.isEmpty()) {
				throw new IllegalArgumentException(INCORRECT_PROPERTIES + firstLevelKey + " List of files is empty!")
			} 
		}
	}

	void parseSecondJsonLevel(ArrayList list, String firstLevelKey, String secondLevelKey, secondLevelValue) {
		if(secondLevelKey.equals("mock-files")  || secondLevelKey.equals("test-files")) {
			list.addAll(secondLevelValue)
		} else if(!isDescription(secondLevelKey)) {
			throw new IllegalArgumentException(INCORRECT_PROPERTIES + firstLevelKey + 
				" In each module you can specify only description, mock-files and test-files!")
		}
	}
	
	boolean isDescription(String key) {
		return key.equals("description")
	}
	
	boolean isMap(key, value) {
		if(value instanceof Map) {
			return true;
		}
		throw new IllegalArgumentException(INCORRECT_PROPERTIES + key + " It's not a map!")
	}
}
