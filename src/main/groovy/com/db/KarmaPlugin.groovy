package com.db
import com.moowork.gradle.node.NodeExtension
import com.moowork.gradle.node.NodePlugin
import com.moowork.gradle.node.task.NodeTask

import groovy.json.StringEscapeUtils

import com.moowork.gradle.node.npm.NpmInstallTask
import com.moowork.gradle.node.npm.NpmTask

import java.nio.file.Files
import java.nio.file.Paths

import org.codehaus.groovy.classgen.ReturnAdder
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.slf4j.LoggerFactory

class KarmaPlugin implements Plugin<Project> {
	
	private static final String NAME = "KarmaPlugin" //TODO move to build.gradle
	private static final String VERSION ="1.0.0" //TODO move to build.gradle
	def logger = LoggerFactory.getLogger(KarmaPlugin.class)
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
		logger.info("$NAME: Configuring $project.name project") 
		logger.info "$NAME: Found $karmaConfigProperties"
		
		ConfigParser parser = new ConfigParser(karmaConfigProperties);
		def map = parser.parseConfigProperties();
		if(!map.isEmpty()) {
			def karmaConfigFile = project.file("$project.rootProject.projectDir/karma.conf.js")
			if(!karmaConfigFile.exists()) {
				logger.info "$NAME: Using default karma.conf.js"
				def karmD = getClass().getClassLoader().getResource("karma-default.conf.js").getText()
				
				project.task('createKarmaConfig')  {
					def KARMA_CONFIG = project.file("${project.rootProject.buildDir.absolutePath}/karma.conf.js")
					outputs.file KARMA_CONFIG
					doLast {
						KARMA_CONFIG.parentFile.mkdirs()
						KARMA_CONFIG.text = karmD
					}
				}
				def testTask = project.tasks.findByName('test')
				if (testTask) {
					testTask.dependsOn 'createKarmaConfig'
				}
			}
		}

		Integer i = 0;
		map.each{ key, value ->
			def testFiles = KarmaUtils.getTestFileAsParameter(project.rootProject.projectDir, project.name, value)
	
			def karmaSubTask = project.task ("karmaSubTask-${key}", type: NodeTask, dependsOn: 'npmInstall', description: 'Executes karma tests in single run')  {
				//inputs.files('/karma.conf.properties', '/karma.conf.js')
				def karmaConfigPath = project.rootProject.buildDir.absolutePath +File.separator + 'karma.conf.js'
				script = project.file("$project.rootProject.projectDir/node_modules/karma/bin/karma")
				args = ['start', karmaConfigPath, testFiles, '--single-run', '--color']
			}
			
			println "TASK NAME $karmaSubTask.name"
			String projectName = karmaSubTask.getName();
			
			def testTask = project.tasks.findByName('test')
				if (testTask) {
					testTask.dependsOn "karmaSubTask-${key}"
			}
		}
		
		project.afterEvaluate{
			println "$NAME END $project"
			
		}
	}
	
}