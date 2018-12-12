package com.db

import groovy.json.StringEscapeUtils

class ConfigParser {
	void parseConfigProperties(File karmaConfigProperties, HashMap map) {
		def parsedJson = new groovy.json.JsonSlurper().parseText(karmaConfigProperties.text)
		parsedJson.forEach { firstLevelKey, firstLevelValue ->
			parseFirstJsonLevel(map, firstLevelKey, firstLevelValue)
		}
	}

	void parseFirstJsonLevel(HashMap map, firstLevelKey, firstLevelValue) {
		if(!isDescription(firstLevelKey)) {
			def list = new ArrayList<>() 
			map.put(firstLevelKey, list) 
			firstLevelValue.forEach {secondLevelKey, secondLevelValue ->
				parseSecondJsonLevel(list, secondLevelKey,secondLevelValue)
			}
			if(list.isEmpty()) {
				throw new IllegalArgumentException("You have badly specified karma.conf.properties for: " + firstLevelKey)
			} 
		}
	}

	void parseSecondJsonLevel(ArrayList list, String secondLevelKey, secondLevelValue) {
		if(isMockOrTestFile(secondLevelKey)) {
			list.addAll(secondLevelValue)
		}
	}
	
	boolean isDescription(String key) {
		return key.equals("description")
	}
	
	boolean isMockOrTestFile(String name) {
		return name.equals("mock-files") || name.equals("test-files")
	}
}
