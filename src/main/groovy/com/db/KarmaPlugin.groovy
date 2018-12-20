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
		addTaskToTestTaskDependency(project, 'npmInstall')
		project.allprojects.each { singleProject -> 
				applySingleProject(singleProject)
			}
			
	}
	void applySingleProject(Project project) {
		project.plugins.apply NodePlugin
		def karmaConfigProperties = project.file("/karma.conf.properties")
		if(!karmaConfigProperties.exists()) {
			return;
		}
		logger.info("$NAME: Configuring $project.name project")
		logger.info "$NAME: Found $karmaConfigProperties"
		
		ConfigParser parser = new ConfigParser(karmaConfigProperties);
		def mapModuleToTestFiles = parser.extractTestFilesForEachModule();
		def karmaConfigPath
		if(!mapModuleToTestFiles.isEmpty()) {
			karmaConfigPath = getFileOrCreateDefault(project, "karma.conf.js", project.rootProject.buildDir)
			getFileOrCreateDefault(project, "package.json", project.rootProject.projectDir)
		}

		mapModuleToTestFiles.each{ key, value ->
			def testFiles = KarmaUtils.getTestFileAsParameter(project.rootProject.projectDir, project.name, value)
			def subTaskName = "karmaSubTask-${key}"
			def karmaSubTask = project.task (subTaskName, type: NodeTask,  description: 'Executes karma tests in single run')  {
				inputs.files('/karma.conf.properties', '/karma.conf.js')
				script = project.file("$project.rootProject.projectDir/node_modules/karma/bin/karma") 
				args = ['start', karmaConfigPath, testFiles, '--single-run', '--color']
				shouldRunAfter 'npmInstall'
			}
			addTaskToTestTaskDependency(project, subTaskName)
		}
	}
	
	String getFileOrCreateDefault(Project project, String fileName, File creationDir) {
		def projectFile = project.file("$project.rootProject.projectDir/$fileName")
		if(!projectFile.exists()) {
			logger.info "$NAME: Using default $fileName"
			def defaultFile = getClass().getClassLoader().getResource("default-$fileName").getText()
			
			def karmaConfig = project.file("${creationDir.absolutePath}/${fileName}")
			def createTask = project.task("create-default-$fileName")  {
				outputs.file karmaConfig
				doLast {
					karmaConfig.parentFile.mkdirs()
					karmaConfig.text = defaultFile
				}
			}
			addTaskToTestTaskDependency(project, createTask.name)
			return karmaConfig.absolutePath
		} 
		return projectFile.absolutePath
	}
	
	void addTaskToTestTaskDependency(Project project, String taskName) {
		def testTask = project.rootProject.tasks.findByName('test')
		if (testTask) {
			println "KARMA: " + project.name + ":::" + taskName + "::" + project.tasks
			def task = project.tasks.findByName(taskName)
			testTask.dependsOn task == null ? taskName : task
		}
	}
}