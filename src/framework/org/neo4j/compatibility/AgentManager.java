package org.neo4j.compatibility;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class AgentManager
{
    private Map<String, StoreAgent> agents = new HashMap<String, StoreAgent>();
    private ErrorReporter errorReporter;

    public AgentManager( ErrorReporter errorReporter )
    {
        this.errorReporter = errorReporter;
        loadVerifiers();
    }

    private void loadVerifiers()
    {
        for ( StoreAgent storeAgent : ServiceLoader.load( StoreAgent.class ) )
        {
            if (storeAgent instanceof IgnoringStoreAgent)
            {
                System.out.println( "Ignored agent: " + storeAgent.getClass().getName() );
            }
            else
            {
                System.out.println( "Found agent: " + storeAgent.getClass().getName() );
                agents.put( storeAgent.getClass().getSimpleName(), storeAgent );
            }
        }
        System.out.println( "Loaded all agents." );
    }

    public void generate( File versionDir )
    {
        Report report = errorReporter.versionReport( versionDir );
        for ( Map.Entry<String, StoreAgent> agentEntry : agents.entrySet() )
        {
            StoreAgent agent = agentEntry.getValue();
            String agentName = agentEntry.getKey();
            try
            {
                agent.generate( new File( versionDir, agentName ).getAbsolutePath() );
            }
            catch ( Throwable e )
            {
                report.reportException( e, "Failed to generate version [%s] with agent [%s]", versionDir, agentName );
            }
        }
    }

    public void verify( File versionDir )
    {
        System.out.println( "====================================================" );
        System.out.println( "Verifying version: " + versionDir );
        System.out.println( "====================================================" );
        Report report = errorReporter.versionReport( versionDir );
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
                    report.info( "Verified version [%s] with agent [%s]", versionDir, agentName );
                }
                catch ( Throwable e )
                {
                    report.reportException( e, "Failed to verify version [%s] with agent [%s]", versionDir, agentName );
                }
            }
            else
            {
                report.info( "Version [%s] missing store for agent [%s]", versionDir, agentName );
            }
        }
    }
}
