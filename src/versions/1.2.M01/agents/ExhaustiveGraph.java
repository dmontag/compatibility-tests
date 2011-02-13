package agents;

import org.neo4j.compatibility.IgnoringStoreAgent;
import org.neo4j.compatibility.StoreAgent;
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class ExhaustiveGraph extends IgnoringStoreAgent
{
    private static final RelationshipType KNOWS = DynamicRelationshipType.withName( "KNOWS" );
    private static final RelationshipType WORKS_FOR = DynamicRelationshipType.withName( "LIKES" );

    public void generate( String dbPath )
    {
        GraphDatabaseService graphDb = new EmbeddedGraphDatabase( dbPath );
        IndexService indexService = new LuceneIndexService( graphDb );
        Transaction tx = graphDb.beginTx();
        try
        {
            Node personA = graphDb.createNode();
            Node personB = graphDb.createNode();
            Node personC = graphDb.createNode();
            Node personD = graphDb.createNode();

            indexService.index( personA, "name", "A" );
            indexService.index( personB, "name", "B" );
            indexService.index( personC, "name", "C" );
            indexService.index( personD, "name", "D" );

            personA.createRelationshipTo( personB, KNOWS );
            personA.createRelationshipTo( personB, WORKS_FOR );
            personA.createRelationshipTo( personC, KNOWS );
            personA.createRelationshipTo( personD, KNOWS );
            personB.createRelationshipTo( personC, WORKS_FOR );
            personC.createRelationshipTo( personD, WORKS_FOR );

            tx.success();
        }
        finally
        {
            tx.finish();
        }
        indexService.shutdown();
        graphDb.shutdown();
    }

    public void verify( String dbPath )
    {
        GraphDatabaseService graphDb = new EmbeddedGraphDatabase( dbPath );
        IndexService indexService = new LuceneIndexService( graphDb );

        Node personA = indexService.getSingleNode( "name", "A" );
        Node personB = indexService.getSingleNode( "name", "B" );
        Node personC = indexService.getSingleNode( "name", "C" );
        Node personD = indexService.getSingleNode( "name", "D" );

        Set<Node> actual = new HashSet<Node>();
        for ( Relationship rel : personA.getRelationships( KNOWS, Direction.OUTGOING ) )
        {
            actual.add( rel.getEndNode() );
        }
        assertEquals( new HashSet<Node>( Arrays.asList( personB, personC, personD ) ), actual );

        assertEquals( personA, personB.getSingleRelationship( WORKS_FOR, Direction.INCOMING ).getStartNode() );
        assertEquals( personC, personB.getSingleRelationship( WORKS_FOR, Direction.OUTGOING ).getEndNode() );

        assertEquals( personD, personC.getSingleRelationship( WORKS_FOR, Direction.OUTGOING ).getEndNode() );
        assertEquals( personC, personD.getSingleRelationship( WORKS_FOR, Direction.INCOMING ).getStartNode() );

        indexService.shutdown();
        graphDb.shutdown();
    }
}
