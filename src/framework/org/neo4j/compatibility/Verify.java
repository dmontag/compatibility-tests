package org.neo4j.compatibility;

import java.io.File;
import java.util.Map;

public class Verify
{

    private AgentManager agentManager;

    public Verify( ErrorReporter errorReporter )
    {
        agentManager = new AgentManager( errorReporter );
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
        ErrorReporter errorReporter = new ErrorReporter();
        Verify verify = new Verify( errorReporter );
        verify.run();
        errorReporter.printReport();
        System.exit( errorReporter.hasErrors() ? 1 : 0 );
    }

}
