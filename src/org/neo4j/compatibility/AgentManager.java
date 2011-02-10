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
            agent.verify( new File( versionDir, agentName ).getAbsolutePath() );
        }
    }
}
