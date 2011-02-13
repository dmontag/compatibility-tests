package agents;

import org.neo4j.compatibility.StoreAgent;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.index.IndexService;
import org.neo4j.index.lucene.LuceneIndexService;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.helpers.collection.IteratorUtil;

import static org.junit.Assert.assertEquals;

public class UnfinishedTransactionGraph implements StoreAgent
{
    private static final RelationshipType REL_TYPE = DynamicRelationshipType.withName( "REL" );

    public void generate( String dbPath )
    {
        GraphDatabaseService graphDb = new EmbeddedGraphDatabase( dbPath );
        IndexService indexService = new LuceneIndexService( graphDb );
        graphDb.beginTx();
        Node n = graphDb.createNode();
        graphDb.getReferenceNode().createRelationshipTo( n, REL_TYPE );

        indexService.index( n, "name", "a" );
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
