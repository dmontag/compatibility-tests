package org.neo4j.compatibility;

import java.io.File;

public class Generate
{
    private AgentManager agentManager;

    public Generate( ErrorReporter errorReporter )
    {
        agentManager = new AgentManager( errorReporter );
    }

    private void run()
    {
        File versionDir = new File( System.getProperty( "version.dir" ) );
        System.out.println( "====================================================" );
        System.out.println( "Generating version: " + versionDir );
        System.out.println( "====================================================" );
        agentManager.generate( versionDir );
    }

    public static void main( String[] args )
    {
        ErrorReporter errorReporter = new ErrorReporter();
        new Generate( errorReporter ).run();
        errorReporter.printReport();
        System.exit( errorReporter.hasErrors() ? 1 : 0 );
    }

}
