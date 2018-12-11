package com.db

class ConfigParser {
	private parseConfigProperties(File karmaConfigProperties, HashMap map) {
		def parsedJson = new groovy.json.JsonSlurper().parseText(karmaConfigProperties.text)
		parsedJson.forEach { firstLevelKey, firstLevelValue ->
			parseFirstJsonLevel(map, firstLevelKey, firstLevelValue)
		}
	}

	private parseFirstJsonLevel(HashMap map, firstLevelKey, firstLevelValue) {
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

	private parseSecondJsonLevel(ArrayList list,firstLevelKey, secondLevelKey, secondLevelValue) {
		if(!isDescription(firstLevelKey) && isMockOrTestFile(secondLevelKey)) {
			//potrzeba ten if? testy?
//			if(secondLevelValue != null && secondLevelValue instanceof List){
			list.addAll(secondLevelValue)
			//							}
		}
	}
	
	private boolean isDescription(String key) {
		return key.equals("description")
	}
	
	private boolean isMockOrTestFile(String name) {
		return name.equals("mock-files") || name.equals("test-files")
	}
}
