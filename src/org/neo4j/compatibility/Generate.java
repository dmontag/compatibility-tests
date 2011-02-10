package org.neo4j.compatibility;

import java.io.File;

public class Generate
{
    private AgentManager agentManager;

    public Generate()
    {
        agentManager = new AgentManager();
    }

    private void run()
    {
        agentManager.generate( new File( System.getProperty( "version.dir" ) ) );
    }

    public static void main( String[] args )
    {
        new Generate().run();
    }

}
