package org.neo4j.compatibility;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class Verify {

    private AgentManager agentManager = new AgentManager();
    Map<String, Exception> errors = new HashMap<String, Exception>();

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
        try
        {
            agentManager.verify( new File( versionDir ) );
        }
        catch ( Exception e )
        {
            errors.put( versionDir, e );
        }
    }

    private int exit()
    {
        if (errors.size() > 0)
        {
            for ( Map.Entry<String, Exception> errorEntry : errors.entrySet() )
            {
                System.out.println( String.format( "Verification of [%s] encountered an error:", errorEntry.getKey() ) );
                errorEntry.getValue().printStackTrace( System.out );
            }
            return 1;
        }
        return 0;
    }

    public static void main( String[] args )
    {
        Verify verify = new Verify();
        verify.run();
        System.exit( verify.exit() );
    }

}
