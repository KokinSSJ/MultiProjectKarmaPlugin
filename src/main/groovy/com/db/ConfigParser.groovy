package com.db

class ConfigParser {
	void parseConfigProperties(File karmaConfigProperties, HashMap map) {
		def parsedJson = new groovy.json.JsonSlurper().parseText(karmaConfigProperties.text)
		parsedJson.forEach { firstLevelKey, firstLevelValue ->
			parseFirstJsonLevel(map, firstLevelKey, firstLevelValue)
		}
	}

	void parseFirstJsonLevel(HashMap map, firstLevelKey, firstLevelValue) {
		if(!isDescription(firstLevelKey)) {
			def list = new ArrayList<>();
			map.put(firstLevelKey, list);
			firstLevelValue.forEach {secondLevelKey, secondLevelValue ->
				parseSecondJsonLevel(list, firstLevelKey, secondLevelKey,secondLevelValue)
			}
			if(list.isEmpty()) {
				throw new IllegalArgumentException("You have badly specified karma.conf.properties for: " + firstLevelKey)
			}
		}
	}

	void parseSecondJsonLevel(ArrayList list,firstLevelKey, secondLevelKey, secondLevelValue) {
		if(!isDescription(firstLevelKey) && isMockOrTestFile(secondLevelKey)) {
			//potrzeba ten if? testy?
//			if(secondLevelValue != null && secondLevelValue instanceof List){
			list.addAll(secondLevelValue)
			//							}
		}
	}
	
	  boolean isDescription(String key) {
		return key.equals("description")
	}
	
	  boolean isMockOrTestFile(String name) {
		return name.equals("mock-files") || name.equals("test-files")
	}
}
