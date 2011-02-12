package org.neo4j.compatibility;

public class DefaultStoreAgent implements StoreAgent
{
    public void generate( String dbPath )
    {
        throw new UnsupportedOperationException( "Generate not supported." );
    }

    public void verify( String dbPath )
    {
        throw new UnsupportedOperationException( "Verify not supported." );
    }
}
