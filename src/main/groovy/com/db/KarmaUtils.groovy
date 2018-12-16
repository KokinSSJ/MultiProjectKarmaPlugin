package com.db

class KarmaUtils {
	static String getTestFileAsParameter(File projectDir, String projectName, List<String> files) {
		def fileAsParam = ""
		def subProjectDir = projectDir.name == projectName ? "" : File.separator +  projectName
		files.each {
			fileAsParam+="--file=" + projectDir.absolutePath + subProjectDir + File.separator + it + " "
		}
		return fileAsParam
	}

}
