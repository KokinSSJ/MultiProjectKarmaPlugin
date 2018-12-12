package com.db

import static org.assertj.core.api.Assertions.assertThat
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertThat
import static org.junit.Assert.assertTrue

import javax.swing.text.html.HTMLEditorKit.Parser

import org.assertj.core.api.Assertions
import org.assertj.core.api.ListAssert
import org.gradle.internal.impldep.org.apache.ivy.core.module.descriptor.ExtendsDescriptor
import org.gradle.internal.impldep.org.junit.rules.ExpectedException
import org.spockframework.util.Matchers

import groovy.swing.impl.DefaultAction
import spock.lang.Specification
import spock.util.matcher.HamcrestMatchers

class ConfigParserSpec extends Specification {
	
	ConfigParser parser = new ConfigParser() 

	def "should return true when description"() {
		when:
			boolean result = parser.isDescription("description") 
		then:
			assertTrue(result) 
	}
	
	def "should return false when something else then description"() {
		when:
			boolean result = parser.isDescription("name") 
		then:
			assertFalse(result) 
	}
	
	def "should return true when mock-files"() {
		when:
			boolean result = parser.isMockOrTestFile("mock-files")
		then:
			assertTrue(result);
	}
	
	def "should return true when test-files"() {
		when:
			boolean result = parser.isMockOrTestFile("test-files")
		then:
			assertTrue(result);
	}
	def "should return false when else then test-files or mock-files"() {
		when:
			boolean result = parser.isMockOrTestFile("name")
		then:
			assertFalse(result);
	}
//	def "file can have description in first and second JSON level"
	// def "should add mock-files first a then test-files"
	def "should add to list when key is test-files" () {
		given:
			def list = new ArrayList<>()
		when:
			parser.parseSecondJsonLevel(list, "test-files", Arrays.asList("/dir/file1.js", "/file2.exe"))
		then:
			Assertions.assertThat(list).containsExactly("/dir/file1.js", "/file2.exe")
	}
	
	def "should add to list when key is mock-files" () {
		given:
			def list = new ArrayList<>()
		when:
			parser.parseSecondJsonLevel(list, "mock-files", Arrays.asList("/dir/file2.js", "/file3.exe"))
		then:
			Assertions.assertThat(list).containsExactly("/dir/file2.js", "/file3.exe")
	}
	def "should not add to list when key is different than mock-files or test-files" () {
		given:
			def list = new ArrayList<>()
			list.add("file0.bin")
		when:
			parser.parseSecondJsonLevel(list, "description", Arrays.asList("/dir/file2.js", "/file3.exe"))
		then:
			Assertions.assertThat(list).containsExactly("file0.bin")
	}
	def "should add to list when value is single String" () {
		given:
			def list = new ArrayList<>()
			list.add("file0.bin")
			when:
				parser.parseSecondJsonLevel(list, "mock-files", "/dir/file2.js")
			then:
				Assertions.assertThat(list).containsExactly("file0.bin", "/dir/file2.js")
	}
	
}
