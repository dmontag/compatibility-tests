package latest.agents;

import org.neo4j.compatibility.StoreAgent;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import static org.junit.Assert.assertEquals;

public class UnclosedIndexingGraph implements StoreAgent
{
    private static final RelationshipType REL_TYPE = DynamicRelationshipType.withName( "REL" );

    public void generate( String dbPath )
    {
        GraphDatabaseService graphDb = new EmbeddedGraphDatabase( dbPath );
        Index<Node> nodeIndex = graphDb.index().forNodes( "nodes" );
        Index<Relationship> relationshipIndex = graphDb.index().forRelationships( "relationships" );
        Transaction tx = graphDb.beginTx();
        try
        {
            Node n = graphDb.createNode();
            Node n2 = graphDb.createNode();
            Relationship rel = n.createRelationshipTo( n2, REL_TYPE );

            nodeIndex.add( n, "name", "a" );
            nodeIndex.add( n2, "name", "b" );
            relationshipIndex.add( rel, "name", "a" );

            tx.success();
        }
        finally
        {
            tx.finish();
        }
    }

    public void verify( String dbPath )
    {
        GraphDatabaseService graphDb = new EmbeddedGraphDatabase( dbPath );
        Index<Node> nodeIndex = graphDb.index().forNodes( "nodes" );
        Index<Relationship> relationshipIndex = graphDb.index().forRelationships( "relationships" );

        Node n = nodeIndex.get( "name", "a" ).getSingle();
        Node n2 = nodeIndex.get( "name", "b" ).getSingle();
        Relationship rel = relationshipIndex.get( "name", "a" ).getSingle();
        assertEquals( n, rel.getStartNode() );
        assertEquals( n2, rel.getEndNode() );

        graphDb.shutdown();
    }
}
