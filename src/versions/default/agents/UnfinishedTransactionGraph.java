package agents;

import org.neo4j.compatibility.StoreAgent;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.index.Index;
import org.neo4j.helpers.collection.IteratorUtil;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import static org.junit.Assert.assertEquals;

public class UnfinishedTransactionGraph implements StoreAgent
{
    private static final RelationshipType REL_TYPE = DynamicRelationshipType.withName( "REL" );

    public void generate( String dbPath )
    {
        EmbeddedGraphDatabase graphDb = new EmbeddedGraphDatabase( dbPath );
        Index<Node> nodeIndex = graphDb.index().forNodes( "nodes" );
        Index<Relationship> relationshipIndex = graphDb.index().forRelationships( "relationships" );
        graphDb.beginTx();
        Node n = graphDb.createNode();
        Relationship rel = graphDb.getReferenceNode().createRelationshipTo( n, REL_TYPE );

        nodeIndex.add( n, "name", "a" );
        relationshipIndex.add( rel, "name", "a" );
    }

    public void verify( String dbPath )
    {
        EmbeddedGraphDatabase graphDb = new EmbeddedGraphDatabase( dbPath );
        assertEquals( "Reference node did not have zero relationships.",
            0, IteratorUtil.count( graphDb.getReferenceNode().getRelationships() ) );
        assertEquals( "There was more than one node.",
            1, IteratorUtil.count( graphDb.getAllNodes() ) );
        graphDb.shutdown();
    }
}
