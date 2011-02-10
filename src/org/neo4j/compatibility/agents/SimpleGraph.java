package org.neo4j.compatibility.agents;

import org.neo4j.compatibility.StoreAgent;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public class SimpleGraph implements StoreAgent
{
    public void generate( String dbPath )
    {
        GraphDatabaseService graphDb = new EmbeddedGraphDatabase( dbPath );
        Transaction tx = graphDb.beginTx();
        try
        {
            graphDb.createNode();
            tx.success();
        }
        finally
        {
            tx.finish();
        }
        graphDb.shutdown();
        System.out.println( "generate" );
    }

    public void verify( String dbPath )
    {
        System.out.println( "verifying: " + dbPath );
    }
}
