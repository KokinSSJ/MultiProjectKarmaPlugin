package com.db
import org.gradle.api.Plugin
import org.gradle.api.Project

class KarmaPlugin implements Plugin<Project> {
	
	
	void apply(Project project) {
		println "KarmaPlugin: Hello"
		
		project.afterEvaluate{
			println "KarmaPlugin: END"
		}
	}
}