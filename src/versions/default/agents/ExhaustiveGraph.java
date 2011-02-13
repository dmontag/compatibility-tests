package agents;

import org.neo4j.compatibility.StoreAgent;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class ExhaustiveGraph implements StoreAgent
{
    private static final RelationshipType KNOWS = DynamicRelationshipType.withName( "KNOWS" );
    private static final RelationshipType WORKS_FOR = DynamicRelationshipType.withName( "LIKES" );

    public void generate( String dbPath )
    {
        GraphDatabaseService graphDb = new EmbeddedGraphDatabase( dbPath );
        Index<Node> personIndex = graphDb.index().forNodes( "persons" );
        Transaction tx = graphDb.beginTx();
        try
        {
            Node personA = graphDb.createNode();
            Node personB = graphDb.createNode();
            Node personC = graphDb.createNode();
            Node personD = graphDb.createNode();

            personIndex.add( personA, "name", "A" );
            personIndex.add( personB, "name", "B" );
            personIndex.add( personC, "name", "C" );
            personIndex.add( personD, "name", "D" );

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
        graphDb.shutdown();
    }

    public void verify( String dbPath )
    {
        GraphDatabaseService graphDb = new EmbeddedGraphDatabase( dbPath );
        Index<Node> personIndex = graphDb.index().forNodes( "persons" );

        Node personA = personIndex.get( "name", "A" ).getSingle();
        Node personB = personIndex.get( "name", "B" ).getSingle();
        Node personC = personIndex.get( "name", "C" ).getSingle();
        Node personD = personIndex.get( "name", "D" ).getSingle();

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

        graphDb.shutdown();
    }
}
