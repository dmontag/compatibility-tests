package org.neo4j.compatibility;

public interface StoreAgent
{
    void generate(String dbPath);
    void verify(String dbPath);
}
