#MultiProjectKarmaPlugin

This plugin is developed version of [JasmineKarmaJS](https://github.com/KokinSSJ/JasmineKarmaJS) project which allow to simpler add karma test to the gradle multi project by adding just a plugin and config file!

See also example project [MultiProjectSample](https://github.com/KokinSSJ)

1. Add MultiProjectKarmaPlugin to your project.
2. In root project karma.conf.properties (see description bellow)
3. Add your karma test (e.g with Jasmine framework) to your subprojects
4. Run tests by "gradlew karma"


Simple by adding karma.conf.js

QA?? TODO??
	1. How to run tests in debug mode???!!! Can I run debug for specific project? ???karma-debug-projectName????
	2. How can I run tests only for specific projects? ???karma-projectName????
	
	
How to build locally? 
	gradlew clean publishToMavenLocal