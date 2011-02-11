package org.neo4j.compatibility;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class AgentManager
{
    private Map<String, StoreAgent> agents = new HashMap<String, StoreAgent>();

    public AgentManager()
    {
        loadVerifiers();
    }

    private void loadVerifiers()
    {
        for ( StoreAgent storeAgent : ServiceLoader.load( StoreAgent.class ) )
        {
            System.out.println( "Found agent: " + storeAgent.getClass().getName() );
            agents.put( storeAgent.getClass().getSimpleName(), storeAgent );
        }
        System.out.println( "Loaded all agents." );
    }

    public void generate( File versionDir )
    {
        for ( Map.Entry<String, StoreAgent> agentEntry : agents.entrySet() )
        {
            StoreAgent agent = agentEntry.getValue();
            String agentName = agentEntry.getKey();
            agent.generate( new File( versionDir, agentName ).getAbsolutePath() );
        }
    }

    public void verify( File versionDir )
    {
        for ( Map.Entry<String, StoreAgent> agentEntry : agents.entrySet() )
        {
            StoreAgent agent = agentEntry.getValue();
            String agentName = agentEntry.getKey();
            File storePath = new File( versionDir, agentName );
            if ( storePath.exists() )
            {
                try
                {
                    agent.verify( storePath.getAbsolutePath() );
                }
                catch ( Exception e )
                {
                    System.out.println( String.format( "Failed to verify version: %s with agent: %s", versionDir, agentName ) );
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
            else
            {
                System.out.println( String.format( "Version [%s] missing store for agent [%s]", versionDir, agentName ) );
            }
        }
    }
}
