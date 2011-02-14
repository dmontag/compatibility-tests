Neo4j store compatibility tests
===============================

This project aims to create a backward compatibility test suite for stores,
and it is intended for continuous use and maintenance. Its goal is to detect
problems related to store upgrades between versions.

It does this by storing a set of generated test stores created by so-called
agents. The tool will produce a bundle of test stores from a given Neo4j
version. Each agent produces a store for the bundle. An agent is a Java class
implementing StoreAgent, and it is capable of generating a store, and
verifying that a store matches what it expects.


File structure
--------------

src/
 framework/     Testing framework classes
 versions/      Version-specific tests
  default/      Default test suite
   agents/      Java package for agents
   META-INF/
    services/
     org.neo4j.compatibility.StoreAgent   References agents to be used
  1.2.M06/      Version-specific test, merged with default suite for
                compatibility of certain versions.
  1.2.M05/
    ...


Scripts
-------

./generate.sh <version>
  
  Generate new bundle of test stores for the specified Neo4j version.

./generate-all.sh

  Generate new bundles of test stores for all Neo4j versions specified in the
  script.

./verify.sh <version>

  Verify that the Neo4j version specified can verify all previously stored
  test store bundles.


Examples
--------

./generate.sh 1.2.M04

  Generates a bundle of test stores in repo/db-1.2.M04.zip.

./generate-all.sh

  Generates bundles of test stores in repo for each version in script.

./verify.sh 1.3-SNAPSHOT

  Verifies that version 1.3-SNAPSHOT can verify every bundle in repo.


New versions
------------

When a new version of Neo4j is released, the previous version needs to be 
rotated into the set of versions tested against. Follow these steps when
a new version X is released:

1. Update the test suite to support version X.

2. Generate bundles of test stores for version X:

  ./generate.sh X

3. Add version X to generate-all.sh.

4. Verify the new version against all stored version bundles:

  ./verify.sh X

Note: This only covers the overall procedure of rotating versions. Adding of
tests is a separate process, described below.


Adding new tests
----------------

As new functionality is added to Neo4j, we will be wanting to test new things.
This is done by adding new test cases, generating and verify new test stores.
When a new test is added, depending on how it is done, it will also be
included when generating bundles of test stores for older versions. Therefore
it is advantageous to regenerate all test store bundles after adding a new
test. This can be done using generate-all.sh.

The default behavior should be to add new tests to src/versions/default. 
Simply give your class a unique name (that identifies the test store), and
make your class implement StoreAgent. Finally, add your class name to
src/versions/default/META-INF/services/org.neo4j.compatibility.StoreAgent. It
will then be picked up when generating and verifying test stores. 


Managing code incompatibility
-----------------------------

Sometimes changes to new code break old code. In general it is good to preserve
working test code for each version of Neo4j. 

If a new release breaks code in src/versions/default, follow these steps:

1. Update src/version/default to be compatible with the latest changes.

2. Run a cycle of generate-all.sh -> verify.sh <new_release>.

3. If the updated agent code is now incompatible with an older version, then
   create a compatible agent in a version-specific source directory.
   Version-specific agents can opt to not implement any verification step,
   as one seldomly will be verifying against versions older than the latest
   one.


How do all the versions come together?
--------------------------------------

When a version is to be generated or verified, first it is determined whether 
a specific version directory for that version exists or not. If none is found,
then the default is used, and that is that.

If a version directory is found, existence for a file named "no-default" in
it is checked. If such a file exists, the default will not be used as the
template. Then the classes used will simply be the ones in that version
directory.

If there is no "no-default" file, then the defaults are used as a template, and
any files that exist in the version directory replace the ones in the default
set. Note that this includes the META-INF/... file. Typically there will be two
use cases:

1. The tested version's code does not compile with the default, and therefore
   requires special changes. An example is graphDb.index() vs IndexService.

2. A specific test case class (agent) should not be used for this version. This
   is easily achieved by making an empty class with the same name, that 
   inherits from IgnoringStoreAgent. It will not be picked up as a store agent.

So to sum up:

exists( version/no-default ) ? version : default + version


Code compatibility relationships
--------------------------------

Every agent can generate and verify a store. An agent is identified by its
class name. Any store generated by an agent with any version of Neo4j has to
be verifiable with the latest version of Neo4j. This means that the
verification code in the agent should stay backward compatible. If the test
needs to cover something new, then a new test should be created instead.

That being said, it is still useful to keep the versions hierarchy maintained,
so that generate-all.sh generates valid store bundles for each version of
Neo4j.

