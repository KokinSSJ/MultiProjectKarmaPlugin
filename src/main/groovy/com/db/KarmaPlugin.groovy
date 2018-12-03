package com.db
import com.moowork.gradle.node.NodeExtension
import com.moowork.gradle.node.NodePlugin
import com.moowork.gradle.node.task.NodeTask

import groovy.json.StringEscapeUtils

import com.moowork.gradle.node.npm.NpmInstallTask
import com.moowork.gradle.node.npm.NpmTask
import org.codehaus.groovy.classgen.ReturnAdder
import org.gradle.api.Plugin
import org.gradle.api.Project

class KarmaPlugin implements Plugin<Project> {
	
	
	void apply(Project project) {
		println "KarmaPlugin: Hello $project"
		
		//setup node
		project.plugins.apply NodePlugin
		NodeExtension nodeConfig = project.extensions.findByName('node') as NodeExtension
		nodeConfig.download = true
		nodeConfig.version = '8.11.3'
		//
		
		
		def map = new HashMap<>();
		def jsonFile = project.file("/karma.conf.properties")
		if(!jsonFile.exists()) {
			return;
		}
		println jsonFile
		def parsedJson = new groovy.json.JsonSlurper().parseText(jsonFile.text)
		
		parsedJson.forEach { key, value -> if(!key.equals("description")) {
			// key => storyeditor
			def list = new ArrayList<>();
			map.put(key, list);
			value.forEach {key1, value2 -> if(!key1.equals("description")) {
					// key -> mock-files and test-files
					if(key1.equals("mock-files") || key1.equals("test-files")) {
						if(value2 != null && value2 instanceof List){
							list.addAll(value2)
						}
						 
						 
					}
				}
			}
			if(list.isEmpty()) {
				println key
				throw new IllegalArgumentException("You have badly specified karma.conf.properties for: " + key)
			}
		}
	}
	//def check= ""
	//map.get("storyeditor").each {
	//		check+="--file=" + it + " "
	//}
	println "MAP: "
	println map
	Integer i = 0;
	map.each{ key, value ->
		key
		def check = ""
		def absolutePath = project.file("$project.rootProject.projectDir").absolutePath
		def subDir = project.rootProject.projectDir== project.name ? "" : "/" +  project.name 
		value.each {
			//check for root project
			check+="--file=" + absolutePath + subDir + "/" + it + " "
//			check+="--file=" +   it + " "
		}

		println "CHECK:"
		println check
		def karmaSubTask = project.task ("karmaSubTask-${key}", type: NodeTask, dependsOn: 'npmInstall', description: 'Executes karma tests in single run') {
			inputs.files("/karma.conf.properties", "/karma.conf.js")
			def karmaConfigFile = project.file("$project.rootProject.projectDir/karma.conf.js")
			def karmaConfigPath = karmaConfigFile.absolutePath
			if(!karmaConfigFile.exists()) {
				println "Using default karma.conf.js"
//				karmaConfigPath = 
			} 
			 
			println "ROOT PROJECT: $project.rootProject.projectDir"
			script = project.file("$project.rootProject.projectDir/node_modules/karma/bin/karma")
			args = ['start', karmaConfigPath, "$check", '--single-run', '--color']
			
		}
		
//		project.task("karmaSubTask-${key}", type:NodeTask, dependsOn: NpmInstallTask) {
//			def karmaConfigPath = project.file("${rootProject.projectDir}/karma.conf.js").absolutePath
//			args = ['start', karmaConfigPath, "$check", '--single-run', '--color']
//		}
		project.tasks.each{ task -> println task}
		
		println "TASK NAME $karmaSubTask.name"
		String projectName = karmaSubTask.getName();
		
		println "TASK2 "
		def testTask = project.tasks.findByName('test')
				println "CONFIG 1 test: $testTask.name"
			if (testTask) {
				println "CONFIG 2 test"
				println i++
				
				println testTask
				testTask.dependsOn "karmaSubTask-${key}"
		}
	}
	
//	def testTask = project.tasks.findByName('test')
//	if (testTask) {
//		testTask.dependsOn karma
//	}
		
		
		project.afterEvaluate{
			println "KarmaPlugin: END $project"
			
		}
	}
}