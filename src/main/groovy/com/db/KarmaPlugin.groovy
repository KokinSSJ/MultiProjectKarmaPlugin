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
	
	private static final String NAME = "KarmaPlugin" //TODO move to build.gradle
	private static final String VERSION ="1.0.0" //TODO move to build.gradle
	
	void apply(Project project) {
		//APPLY NodePlugin to allow further usage in this plugin
		project.plugins.apply NodePlugin
		//YOU CAN SPECIFY NODE SPECIALY FOR YOUR PROJECT BY adding node{} in build.gradle file
//		NodeExtension nodeConfig = project.extensions.findByName('node') as NodeExtension
//		nodeConfig.download = true
//		nodeConfig.version = '6.12.0'
//		nodeConfig.npmVersion='3.10.10' 
//		nodeConfig.workDir = new File("${project.rootProject.projectDir}/../nodejs")
//		nodeConfig.npmWorkDir = new File("${project.rootProject.projectDir}/../npm")
//		nodeConfig.nodeModulesDir = new File("${project.rootProject.projectDir}")
//		println "NODE extension $nodeConfig.download AND $nodeConfig.version"
		//
		
		def karmaConfigProperties = project.file("/karma.conf.properties")
		if(!karmaConfigProperties.exists()) {
			return;
		}
		println "$NAME: Configuring $project.name project"
		println "$NAME: Found $karmaConfigProperties"
		
		ConfigParser parser = new ConfigParser(karmaConfigProperties);
		def map = parser.parseConfigProperties();
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