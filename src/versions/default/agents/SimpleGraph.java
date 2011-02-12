package agents;

import org.neo4j.compatibility.StoreAgent;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.helpers.collection.IteratorUtil;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;

public class SimpleGraph implements StoreAgent
{
    private static final RelationshipType REL_TYPE = DynamicRelationshipType.withName( "REL" );

    public void generate( String dbPath )
    {
        GraphDatabaseService graphDb = new EmbeddedGraphDatabase( dbPath );
        Transaction tx = graphDb.beginTx();
        try
        {
            Node n = graphDb.createNode();
            n.createRelationshipTo( graphDb.createNode(), REL_TYPE );
            tx.success();
        }
        finally
        {
            tx.finish();
        }
        graphDb.shutdown();
    }

    public void verify( String dbPath )
    {
        GraphDatabaseService graphDb = new EmbeddedGraphDatabase( dbPath );
        Node n = graphDb.getNodeById( 1 );
        Node n2 = n.getSingleRelationship( REL_TYPE, Direction.OUTGOING ).getEndNode();
        assertEquals( new HashSet<Node>( Arrays.asList( graphDb.getReferenceNode(), n, n2 ) ),
            new HashSet<Node>( IteratorUtil.asCollection( graphDb.getAllNodes() ) ) );
        graphDb.shutdown();
    }
}
