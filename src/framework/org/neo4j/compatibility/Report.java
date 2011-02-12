package org.neo4j.compatibility;

import java.io.File;

public class Report
{
    private boolean hasErrors;

    public void reportException( Throwable e, String message, Object... params )
    {
        hasErrors = true;
        print( message, params );
        e.printStackTrace( System.out );
    }

    public void reportError( String message, Object... params )
    {
        hasErrors = true;
        print( message, params );
    }

    private void print( String message, Object... params )
    {
        System.out.println( String.format( "==> " + message, params ) );
    }

    public boolean hasErrors()
    {
        return hasErrors;
    }

    public void info( String message, Object... params )
    {
        print( message, params );
    }
}
