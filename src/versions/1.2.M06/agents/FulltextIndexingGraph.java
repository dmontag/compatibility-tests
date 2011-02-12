package agents;

import org.neo4j.compatibility.DefaultStoreAgent;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import java.lang.Override;

import static org.junit.Assert.assertEquals;

public class FulltextIndexingGraph extends DefaultStoreAgent
{
    private static final RelationshipType REL_TYPE = DynamicRelationshipType.withName( "REL" );

    @Override
    public void generate( String dbPath )
    {
        GraphDatabaseService graphDb = new EmbeddedGraphDatabase( dbPath );
        Index<Node> nodeIndex = graphDb.index().forNodes( "nodes", MapUtil.stringMap( "provider", "lucene", "type", "fulltext" ) );
        Index<Relationship> relationshipIndex = graphDb.index().forRelationships( "relationships", MapUtil.stringMap( "provider", "lucene", "type", "fulltext" ) );
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
}
