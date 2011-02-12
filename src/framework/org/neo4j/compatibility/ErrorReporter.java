package org.neo4j.compatibility;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

public class ErrorReporter
{
    private Map<File, Report> reports = new TreeMap<File, Report>();

    public void printReport()
    {
        if (!hasErrors())
        {
            print( "There were no errors." );
            return;
        }
        print( "The following versions had errors:" );
        for ( Map.Entry<File, Report> reportEntry : reports.entrySet() )
        {
            if ( reportEntry.getValue().hasErrors() )
            {
                print( "  %s", reportEntry.getKey() );
            }
        }
    }

    public boolean hasErrors()
    {
        for ( Report report : reports.values() )
        {
            if ( report.hasErrors() ) return true;
        }
        return false;
    }

    public void print( String message, Object... params )
    {
        System.out.println( String.format( "==> " + message, params ) );
    }

    public Report versionReport( File versionDir )
    {
        Report report = new Report();
        reports.put( versionDir, report );
        return report;
    }
}
