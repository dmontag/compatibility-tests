package agents;

import org.neo4j.compatibility.IgnoringStoreAgent;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.index.IndexService;
import org.neo4j.index.lucene.LuceneIndexService;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import static org.junit.Assert.assertEquals;

public class UnclosedIndexingGraph extends IgnoringStoreAgent
{
    private static final RelationshipType REL_TYPE = DynamicRelationshipType.withName( "REL" );

    public void generate( String dbPath )
    {
        GraphDatabaseService graphDb = new EmbeddedGraphDatabase( dbPath );
        IndexService indexService = new LuceneIndexService( graphDb );
        Transaction tx = graphDb.beginTx();
        try
        {
            Node n = graphDb.createNode();
            Node n2 = graphDb.createNode();
            n.createRelationshipTo( n2, REL_TYPE );

            indexService.index( n, "name", "a" );
            indexService.index( n2, "name", "b" );

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
        IndexService indexService = new LuceneIndexService( graphDb );

        Node n = indexService.getSingleNode( "name", "a" );
        Node n2 = indexService.getSingleNode( "name", "b" );
        Relationship rel = n2.getSingleRelationship( REL_TYPE, Direction.INCOMING );
        assertEquals( n, rel.getStartNode() );

        indexService.shutdown();
        graphDb.shutdown();
    }
}
