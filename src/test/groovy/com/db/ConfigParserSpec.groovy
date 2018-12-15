package com.db

import static org.assertj.core.api.Assertions.assertThat
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertThat
import static org.junit.Assert.assertTrue

import javax.swing.text.html.HTMLEditorKit.Parser
import javax.xml.ws.soap.Addressing

import org.assertj.core.api.Assertions
import org.assertj.core.api.ListAssert
import org.gradle.internal.impldep.org.apache.ivy.core.module.descriptor.ExtendsDescriptor
import org.gradle.internal.impldep.org.junit.rules.ExpectedException
import org.spockframework.compiler.model.SetupBlock
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
	def "should add to list when pass a String" () {
		given:
			def list = new ArrayList<>()
			list.add("file0.bin")
		when:
			parser.parseSecondJsonLevel(list, "mock-files", "/dir/file2.js")
		then:
			Assertions.assertThat(list).containsExactly("file0.bin", "/dir/file2.js")
	}
	
	def "should throw expcetion when first level value is no a map, like {} "() {
		setup:
			def map = new HashMap<>() 
		when:
			parser.parseFirstJsonLevel(map, "module-name", "/file.js");
		then:
			IllegalArgumentException ex = thrown()
			ex.message == ConfigParser.INCORRECT_PROPERTIES + "module-name" + " It's not a map!"
	}
	def "should throw expcetion that no files specified for specifiec project in karma.conf.properties"() {
		setup:
			def map = new HashMap<>() 
			def value = new HashMap<>()
			value.put("description", "something about module");
		when:
			parser.parseFirstJsonLevel(map, "module-name", value);
		then:
			IllegalArgumentException ex = thrown()
			ex.message == ConfigParser.INCORRECT_PROPERTIES + "module-name" + " List of files is empty!"
	}
	def "should throw exception that only description, mock-files and test-files are allowed!"() {
		setup:
			def map = new HashMap<>() 
			def value = new HashMap<>()
			value.put("description", "something about module");
			value.put("other name", Arrays.asList("/file1", "file2.exe"));
		when:
			parser.parseFirstJsonLevel(map, "module-name", value);
		then:
			IllegalArgumentException ex = thrown()
			ex.message == ConfigParser.INCORRECT_PROPERTIES + "other name" + 
				" In each module you can specify only description, mock-files and test-files!"
	}
	def "should add to the list only files from mock-files list even if test-files not specified"() {
		setup:
			def map = new HashMap<>() 
			def value = new HashMap<>()
			value.put("description", "something about module");
			value.put("mock-files", Arrays.asList("/file1", "file2.exe"));
		when:
			parser.parseFirstJsonLevel(map, "module-name", value);
		then:
			map.size() == 1
			Assertions.assertThat(map.get("module-name")).containsExactly("/file1", "file2.exe")
	}
	def "should consist only one file if mock-files is a string"() {
		setup:
			def map = new HashMap<>() 
			def value = new HashMap<>()
			value.put("description", "something about module");
			value.put("mock-files",  "file2.exe" );
		when:
			parser.parseFirstJsonLevel(map, "module-name", value);
		then:
			map.size() == 1
			Assertions.assertThat(map.get("module-name")).containsExactly("file2.exe")
	}
	def "should add to the list only files from test-files list even if mock-files not specified"() {
		setup:
			def map = new HashMap<>() 
			def value = new HashMap<>()
			value.put("description", "something about module");
			value.put("test-files", Arrays.asList("/test-file1", "test-file2.exe"));
		when:
			parser.parseFirstJsonLevel(map, "module-name", value);
		then:
			map.size() == 1
			Assertions.assertThat(map.get("module-name")).containsExactly("/test-file1", "test-file2.exe")
	}
	def "should consist only one file if test-files is a string"() {
		setup:
			def map = new HashMap<>() 
			def value = new HashMap<>()
			value.put("description", "something about module");
			value.put("test-files",  "test-file2.exe" );
		when:
			parser.parseFirstJsonLevel(map, "module-name", value);
		then:
			map.size() == 1
			Assertions.assertThat(map.get("module-name")).containsExactly("test-file2.exe")
	}
	
}
