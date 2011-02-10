package org.neo4j.compatibility;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class Verify {

    private AgentManager agentManager;

    public Verify()
    {
        agentManager = new AgentManager();
    }

    private void run()
    {
        String versionsCsv = System.getProperty( "versions.csv" );
        for ( String versionDir : versionsCsv.split( "," ) )
        {
            verifyVersion( versionDir );
        }
    }

    private void verifyVersion( String versionDir )
    {
        agentManager.verify( new File( versionDir ) );
    }

    public static void main( String[] args )
    {
        new Verify().run();
    }

}
