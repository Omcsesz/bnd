import java.util.jar.*;

println 'Tests for bnd-maven-plugin'
println " basedir: ${basedir}"
println " localRepositoryPath: ${localRepositoryPath}"
println " mavenVersion: ${mavenVersion}"

// Check the bundles exist!
File api_bundle = new File(basedir, 'test-api-bundle/target/test-api-bundle-0.0.1.jar')
assert api_bundle.isFile()
File impl_bundle = new File(basedir, 'test-impl-bundle/target/test-impl-bundle-0.0.1-SNAPSHOT.jar')
assert impl_bundle.isFile()
File wrapper_bundle = new File(basedir, 'test-wrapper-bundle/target/test-wrapper-bundle-0.0.1-BUILD-SNAPSHOT.jar')
assert wrapper_bundle.isFile()

// Load manifests
JarFile api_jar = new JarFile(api_bundle)
Attributes api_manifest = api_jar.getManifest().getMainAttributes()
JarFile impl_jar = new JarFile(impl_bundle)
Attributes impl_manifest = impl_jar.getManifest().getMainAttributes()
JarFile wrapper_jar = new JarFile(wrapper_bundle)
Attributes wrapper_manifest = wrapper_jar.getManifest().getMainAttributes()

// Basic manifest check
assert api_manifest.getValue('Bundle-SymbolicName') == 'test-api-bundle'
assert impl_manifest.getValue('Bundle-SymbolicName') == 'test-impl-bundle'
assert wrapper_manifest.getValue('Bundle-SymbolicName') == 'test-wrapper-bundle'
assert api_manifest.getValue('Bundle-Version') == '0.0.1'
assert impl_manifest.getValue('Bundle-Version') == '0.0.1.SNAPSHOT'
assert wrapper_manifest.getValue('Bundle-Version') != '0.0.1.BUILD-SNAPSHOT'
assert wrapper_manifest.getValue('Bundle-Version') =~ /^0\.0\.1\.BUILD-/

// Check inheritance of properties in bnd.bnd from the parent project
assert api_manifest.getValue('X-ParentProjectProperty') == 'it worked'
assert impl_manifest.getValue('X-ParentProjectProperty') == 'it worked'
assert wrapper_manifest.getValue('X-ParentProjectProperty') == 'overridden'

// Check -include of bnd files
assert api_manifest.getValue('X-IncludedParentProjectProperty') == 'Included via -include in parent bnd.bnd file'
assert impl_manifest.getValue('X-IncludedParentProjectProperty') == 'Included via -include in parent bnd.bnd file'
assert wrapper_manifest.getValue('X-IncludedParentProjectProperty') == 'Included via -include in parent bnd.bnd file'
assert wrapper_manifest.getValue('X-IncludedProperty') == 'Included via -include in project bnd.bnd file'
assert impl_manifest.getValue('X-IncludedProjectProperty') == 'Included via -include in project bnd.bnd file'

// Check POM properties
assert impl_manifest.getValue('Project-Build-OutputDirectory') == new File(basedir, 'test-impl-bundle/target/classes').absolutePath
assert impl_manifest.getValue('Project-Build-SourceEncoding') == 'UTF-8'
assert impl_manifest.getValue('Project-GroupId-ArtifactId') == 'biz.aQute.bnd-test:test-impl-bundle'
assert impl_manifest.getValue('Project-NoSuchProperty') == '${project.nosuchproperty}'
assert impl_manifest.getValue('Settings-LocalRepository') == localRepositoryPath.absolutePath
assert impl_manifest.getValue('Settings-InteractiveMode') == 'false'
assert impl_manifest.getValue('SomeVar') == 'value'
assert impl_manifest.getValue('SomeParentVar') == 'parentValue'

// Check bnd properties
assert api_manifest.getValue('Project-Name') == 'test-api-bundle'
assert impl_manifest.getValue('Project-Name') == 'test-impl-bundle'
assert wrapper_manifest.getValue('Project-Name') == 'test-wrapper-bundle'
assert api_manifest.getValue('Project-Dir') == new File(basedir, 'test-api-bundle').absolutePath
assert impl_manifest.getValue('Project-Dir') == new File(basedir, 'test-impl-bundle').absolutePath
assert wrapper_manifest.getValue('Project-Dir') == new File(basedir, 'test-wrapper-bundle').absolutePath
assert api_manifest.getValue('Project-Output') == new File(basedir, 'test-api-bundle/target').absolutePath
assert impl_manifest.getValue('Project-Output') == new File(basedir, 'test-impl-bundle/target').absolutePath
assert wrapper_manifest.getValue('Project-Output') == new File(basedir, 'test-wrapper-bundle/target').absolutePath
assert api_manifest.getValue('Project-Buildpath')
assert impl_manifest.getValue('Project-Buildpath')
assert wrapper_manifest.getValue('Project-Buildpath')
assert api_manifest.getValue('Project-Sourcepath')
assert impl_manifest.getValue('Project-Sourcepath')
assert !wrapper_manifest.getValue('Project-Sourcepath')

// Check contents
assert api_jar.getEntry('org/example/api/') != null
assert api_jar.getEntry('org/example/api/aresource.txt') != null
assert api_jar.getInputStream(api_jar.getEntry('org/example/api/aresource.txt')).text =~ /This is a resource/
assert api_jar.getEntry('org/example/types/') != null
assert api_jar.getEntry('OSGI-OPT/src/') != null
assert impl_jar.getEntry('org/example/impl/') != null
assert impl_jar.getEntry('OSGI-INF/org.example.impl.ExampleComponent.xml') != null
assert impl_jar.getEntry('OSGI-INF/metatype/org.example.impl.Config.xml') != null
assert wrapper_jar.getEntry('org/example/api/') != null
assert wrapper_jar.getEntry('org/example/types/') != null
