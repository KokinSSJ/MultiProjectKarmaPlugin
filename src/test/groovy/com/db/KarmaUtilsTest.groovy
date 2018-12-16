package com.db

import org.codehaus.groovy.classgen.asm.sc.StaticCompilationMopWriter
import org.gradle.internal.impldep.org.apache.ivy.core.module.descriptor.ExtendsDescriptor

import spock.lang.Specification

class KarmaUtilsTest extends Specification  {

	def "should return empty string"() {
		given:
			File projectDir = new File("")
			String projectName = "project-name"
		when:
			def result = KarmaUtils.getTestFileAsParameter(projectDir, projectName, null)
		then:
			result == ""	
	}
	def "should return param with absolute path to file"() {
		given:
			File projectDir = new File("")
			String projectName = "project-name"
		when:
			def result = KarmaUtils.getTestFileAsParameter(projectDir, projectName, Arrays.asList("file1.js"))
		then:
			result == "--file=" + projectDir.absolutePath + File.separator + projectName + File.separator + "file1.js "
	}
	
	def "should return all params with absolute path to files"() {
		given:
			File projectDir = new File("")
			String projectName = "project-name"
		when:
			def result = KarmaUtils.getTestFileAsParameter(projectDir, projectName, Arrays.asList("file1.js", "webapp/test-dir/file2.js"))
		then:
			result == "--file=" + projectDir.absolutePath + File.separator + projectName + File.separator + "file1.js " + 
			"--file=" + projectDir.absolutePath + File.separator + projectName + File.separator + "webapp/test-dir/file2.js "
	}
	
	def "should return param list for root project"() {
		given:
			File projectDir = new File("root-project")
			String projectName = "root-project"
		when:
			def result = KarmaUtils.getTestFileAsParameter(projectDir, projectName, Arrays.asList("file1.js"))
		then:
			result == "--file=" + projectDir.absolutePath  + File.separator + "file1.js "
	}
	
	
}
