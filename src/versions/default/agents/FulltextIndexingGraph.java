package agents;

import org.neo4j.compatibility.DefaultStoreAgent;
import org.neo4j.compatibility.StoreAgent;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FulltextIndexingGraph extends DefaultStoreAgent
{
    private static final RelationshipType REL_TYPE = DynamicRelationshipType.withName( "REL" );

    public void generate( String dbPath )
    {
        GraphDatabaseService graphDb = new EmbeddedGraphDatabase( dbPath );
        Index<Node> nodeIndex = graphDb.index().forNodes( "nodes", MapUtil.stringMap( "type", "fulltext" ) );
        Index<Relationship> relationshipIndex = graphDb.index().forRelationships( "relationships", MapUtil.stringMap( "type", "fulltext" ) );
        Transaction tx = graphDb.beginTx();
        try
        {
            Node n = graphDb.createNode();
            Node n2 = graphDb.createNode();
            Relationship rel = n.createRelationshipTo( n2, REL_TYPE );

            nodeIndex.add( n, "name", "alpha bravo" );
            nodeIndex.add( n2, "name", "charlie delta" );
            relationshipIndex.add( rel, "name", "echo foxtrot" );

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
        Index<Node> nodeIndex = graphDb.index().forNodes( "nodes", MapUtil.stringMap( "type", "fulltext" ) );
        Index<Relationship> relationshipIndex = graphDb.index().forRelationships( "relationships", MapUtil.stringMap( "type", "fulltext" ) );

        Node n = nodeIndex.query( "name", "bravo" ).getSingle();
        Node n2 = nodeIndex.query( "name:char* AND name:*ta" ).getSingle();
        Relationship rel = relationshipIndex.query( "name", "ec*" ).getSingle();
        assertNotNull( rel );
        assertEquals( n, rel.getStartNode() );
        assertEquals( n2, rel.getEndNode() );

        graphDb.shutdown();
    }
}
