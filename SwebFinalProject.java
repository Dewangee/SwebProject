import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDF4J;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.BooleanQuery;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.repository.sail.SailRepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;
import org.eclipse.rdf4j.sail.shacl.ShaclSail;
import org.eclipse.rdf4j.sail.shacl.ShaclSailValidationException;
import org.eclipse.rdf4j.sail.shacl.results.ValidationReport;

public class SwebFinalProject {

    static String desktop = System.getProperty("user.home") + "/Desktop/SWEB/";

    //Reading Mapping File and dataset to create RDF model for the graph

    public static ModelBuilder makeRDFModel() throws IOException
    {
        BufferedReader br = new BufferedReader(new FileReader(desktop +"MappingFile.csv"));
        String line = "";


        ModelBuilder builder = new ModelBuilder();
        ValueFactory vf = SimpleValueFactory.getInstance();

        builder.setNamespace("", "http://www.vintagewinesellers.com/tasting/reviews/");
        builder.setNamespace("owl", "http://www.w3.org/2002/07/owl#");
        builder.setNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        builder.setNamespace("xml", "http://www.w3.org/XML/1998/namespace");
        builder.setNamespace("xsd", "http://www.w3.org/2001/XMLSchema#");
        builder.setNamespace("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        builder.setNamespace("schema", "http://schema.org/");

        while((line=br.readLine())!=null)
        {
            //System.out.println(line);
            String[] l = line.split(",");

            if (l[0].equalsIgnoreCase("Class"))
            {

                BufferedReader newbr = new BufferedReader(new FileReader(desktop+"Wine.csv"));
                String newline = newbr.readLine();

                while((newline=newbr.readLine())!=null)
                {
                    String[] newl = newline.split(",");
                    if (l[2].equals("25"))
                        builder.add(":"+l[1]+newl[0],RDF.TYPE, ":"+(l[1]));
                    else
                        builder.add(":"+newl[Integer.parseInt(l[2])],RDF.TYPE, ":"+l[1]);
                }
            }

            else if (l[0].equalsIgnoreCase("DataProperty"))
            {
                BufferedReader newbr = new BufferedReader(new FileReader(desktop+"Wine.csv"));
                String newline = newbr.readLine();

                while((newline=newbr.readLine())!=null)
                {
                    String[] newl = newline.split(",");
                    if (l[1].matches("[0-9]+"))
                    {
                        if (l[2].indexOf(":")==-1)
                            builder.add(":"+newl[Integer.parseInt(l[1])], ":"+l[2], vf.createLiteral(newl[Integer.parseInt(l[3])]));
                        else
                            builder.add(":"+newl[Integer.parseInt(l[1])], l[2], vf.createLiteral(newl[Integer.parseInt(l[3])]));
                    }
                    else
                    {
                        if (l[2].indexOf(":")==-1)
                            builder.add(":"+l[1]+newl[0], ":"+l[2], vf.createLiteral(newl[Integer.parseInt(l[3])]));
                        else
                            builder.add(":"+l[1]+newl[0], l[2], vf.createLiteral(newl[Integer.parseInt(l[3])]));
                    }
                }
            }

            else if (l[0].equalsIgnoreCase("DataPropertyInt"))
            {
                BufferedReader newbr = new BufferedReader(new FileReader(desktop+"Wine.csv"));
                String newline = newbr.readLine();

                while((newline=newbr.readLine())!=null)
                {
                    String[] newl = newline.split(",");
                    if (l[1].matches("[0-9]+"))
                    {
                        if (l[2].indexOf(":")==-1)
                            builder.add(":"+newl[Integer.parseInt(l[1])], ":"+l[2], vf.createLiteral(Integer.parseInt(newl[Integer.parseInt(l[3])])));
                        else
                            builder.add(":"+newl[Integer.parseInt(l[1])], l[2], vf.createLiteral(Integer.parseInt(newl[Integer.parseInt(l[3])])));
                    }
                    else
                    {
                        if (l[2].indexOf(":")==-1)
                            builder.add(":"+l[1]+newl[0], ":"+l[2], vf.createLiteral(Integer.parseInt(newl[Integer.parseInt(l[3])])));
                        else
                            builder.add(":"+l[1]+newl[0], l[2], vf.createLiteral(Integer.parseInt(newl[Integer.parseInt(l[3])])));
                    }
                }
            }



            else
            {
                BufferedReader newbr = new BufferedReader(new FileReader(desktop+"Wine.csv"));
                String newline = newbr.readLine();

                while((newline=newbr.readLine())!=null)
                {
                    String[] newl = newline.split(",");
                    if (l[1].matches("[0-9]+") && l[3].matches("[0-9]+"))
                    {
                        if (l[2].indexOf(":")==-1)
                            builder.add(":"+newl[Integer.parseInt(l[1])], ":"+l[2], ":"+newl[Integer.parseInt(l[3])]);
                        else
                            builder.add(":"+newl[Integer.parseInt(l[1])], l[2], ":"+newl[Integer.parseInt(l[3])]);
                    }
                    else if (l[1].matches("[0-9]+"))
                    {
                        if (l[2].indexOf(":")==-1)
                            builder.add(":"+newl[Integer.parseInt(l[1])], ":"+l[2], ":"+l[3]+newl[0]);
                        else
                            builder.add(":"+newl[Integer.parseInt(l[1])], l[2], ":"+l[3]+newl[0]);
                    }
                    else if (l[3].matches("[0-9]+"))
                    {
                        if (l[2].indexOf(":")==-1)
                            builder.add(":"+l[1]+newl[0], ":"+l[2], ":"+newl[Integer.parseInt(l[3])]);
                        else
                            builder.add(":"+l[1]+newl[0], l[2], ":"+newl[Integer.parseInt(l[3])]);
                    }
                    else
                    {
                        if (l[2].indexOf(":")==-1)
                            builder.add(":"+l[1]+newl[0], ":"+l[2], ":"+l[3]+newl[0]);
                        else
                            builder.add(":"+l[1]+newl[0], l[2], ":"+l[3]+newl[0]);
                    }
                }
            }

        }
        return builder;
    }

    //Reading the input file and forming a SPARQL query

    public static String queryBuilder(String filename) throws IOException
    {
        BufferedReader buffer = new BufferedReader(new FileReader(filename));
        String queryline = "";

        String query ="";
        while((queryline=buffer.readLine())!=null)
        {
            query+= queryline;
        }
        return query;
    }

    //Running the Count Query to display number of triples formed

    public static void countQuery(RepositoryConnection conn)
    {
        String countquery = "SELECT (COUNT(*) AS ?triples)  WHERE { ?s ?p ?o }";

        TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, countquery);
        try
        {
            TupleQueryResult result = tupleQuery.evaluate();
            Long count = Long.parseLong(result.next().getValue("triples").stringValue());
            System.out.println("Count "+count);
            result.close();
        }
        finally
        {

        }
    }

    //Running each of the SPARQL queries to get the dataset

    public static TupleQueryResult runSPARQLQueries(RepositoryConnection conn, String query) throws IOException
    {
        TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, query);

        try
        {
            TupleQueryResult result = tupleQuery.evaluate();
            return result;

        }
        finally
        {

        }
    }

    //Running each of the SPARQL queries to get the dataset for boolean results

    public static Boolean runSPARQLQueriesBool(RepositoryConnection conn, String query) throws IOException
    {

        BooleanQuery bool = conn.prepareBooleanQuery(QueryLanguage.SPARQL, query);

        try
        {
            Boolean result = bool.evaluate()|true;
            return result;

        }
        finally
        {

        }
    }
    //Writing each SPARQL query result to a file

    public static void OutputToFile(TupleQueryResult result, String filename) throws IOException
    {
        BufferedWriter writer = new BufferedWriter(new FileWriter(desktop+"SPARQLQueries/Output/"+ filename));
        while (result.hasNext())
        {
            List<String> bindingNames = result.getBindingNames();
            BindingSet solution = result.next();

            for (int i=0; i<bindingNames.size(); i++)
            {
                String name = bindingNames.get(i);
                writer.write(name +" - "+ solution.getValue(name)+"\n");
            }
            writer.newLine();

        }
        writer.close();
    }

    //SHACL validation writing output to file

    public static void Shacl(String filename, Model m) throws RDFParseException, RepositoryException, IOException
    {
        SailRepository sailRepository = new SailRepository(new ShaclSail(new MemoryStore()));
        sailRepository.init();

        try {

            SailRepositoryConnection connection = sailRepository.getConnection();
            connection.begin();

            String q = queryBuilder(desktop+"/SHACL/Input/"+filename);
            System.out.println(q);

            Reader shaclRules = new StringReader(q);
            connection.add(shaclRules, "", RDFFormat.TURTLE, RDF4J.SHACL_SHAPE_GRAPH);

            connection.commit();
            connection.begin();


            connection.add(m);

//            StringReader invalidSampleData = new StringReader(
//                    String.join("\n", "",
//                            "@prefix : <http://www.vintagewinesellers.com/tasting/reviews/> . ",
//                            "@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .",
//
//                            ":PaulGregutt a :Taster ;",
//                            "  :hasTwitterHandle \"30\"^^xsd:string  ."
//
//                    ));
//            connection.add(invalidSampleData, "", RDFFormat.TURTLE);

            //countQuery(connection);

            try {
                connection.commit();
                System.out.println(1);
            }
            catch (RepositoryException exception)
            {
                Throwable cause = exception.getCause();
                if (cause instanceof ShaclSailValidationException)
                {
                    FileWriter f = new FileWriter(desktop+"/SHACL/Output/"+filename, false);
                    ValidationReport validationReport = ((ShaclSailValidationException) cause).getValidationReport();
                    Model validationReportModel = ((ShaclSailValidationException) cause).validationReportAsModel();
                    Rio.write(validationReportModel, f, RDFFormat.TURTLE);
                }
                throw exception;
            }
        }
        finally
        {

        }

    }

    public static void main(String[] args) throws IOException,RepositoryException, MalformedQueryException, QueryEvaluationException {


        ModelBuilder builder = makeRDFModel();
        Model m = builder.build();

        //Writing the Triples in the RDF Format to a file

        FileWriter f = new FileWriter(desktop+"RDFXMLfile", false);
        Rio.write(m, f, RDFFormat.RDFXML);

        FileWriter f1 = new FileWriter(desktop+"RDFTurtlefile", false);
        Rio.write(m, f1, RDFFormat.TURTLE);

        //Creating a native store for triples

        Repository db = new SailRepository(new NativeStore(new File(desktop+"NativeStore/")));
        db.initialize();


        //Creating a connection to the native store to run SPARQL queries

        try
        {
            RepositoryConnection conn = db.getConnection();
            conn.add(m);

            //SPARQL query to count number of triples

            countQuery(conn);

            //Reading all query files within the folder

            String foldername = desktop+"SPARQLQueries/Input/";

			for (int k=1; k<=10; k++)
			{
			    String file = k+".txt";
				String query = queryBuilder(foldername+file);

                try
                {
                    TupleQueryResult result = runSPARQLQueries(conn, query);
                    OutputToFile(result, file);
                }
                catch (Exception e)
                {
                    Boolean result = runSPARQLQueriesBool(conn, query);
                    BufferedWriter writer = new BufferedWriter(new FileWriter(desktop+"SPARQLQueries/Output/"+ file));

                    writer.write(String.valueOf(result));
                    writer.close();

                }

			}

            Shacl("1.txt",m);
            Shacl("2.txt",m);

        }
        finally
        {
            db.shutDown();
        }

    }


}
