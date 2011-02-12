package org.neo4j.compatibility;

public class IgnoringStoreAgent implements StoreAgent
{
    public void generate( String dbPath )
    {
        System.out.println( "Generate ignored for path: " + dbPath );
    }

    public void verify( String dbPath )
    {
        System.out.println( "Verify ignored for path: " + dbPath );
    }
}
