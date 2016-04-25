package com.iu.datasemantics;


import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

@WebServlet("/searchservlet")
public class SearchServlet extends HttpServlet {

    static final String inputFileName = "restaurant.rdf";
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {

        String cuisine = request.getParameter("cuisine");

        int rating = Integer.parseInt(request.getParameter("rating"));

        System.out.printf(cuisine);

        Model model = ModelFactory.createDefaultModel();

        //Read the file
        model.read(new RDFReader().read(inputFileName), "");

        //Fetching the resturants based on Cuisine and rating selected on the search web page
        String queryString = "PREFIX ontlgy: <http://www.semanticweb.org/restaurant.owl#>\n" +
                "PREFIX rdfsyn: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "\n" +
                "SELECT ?RestaurantUrl ?MenuUrl ?Address ?Rating\n" +
                "where {\n" +
                "\t?x <https://schema.org/Restaurant#servesCuisine> <https://schema.org/" + cuisine + "> .\n" +
                "\t?x <https://schema.org/url> ?RestaurantUrl .\n" +
                "\t?x <https://schema.org/menu> ?MenuUrl .\n" +
                "\t?x <https://schema.org/address> ?Address .\n" +
                "\t?x <https://schema.org/hasRating> ?Rating .\n" +
                "}";

        OutputStream outputStream = response.getOutputStream();

        runQuery(queryString, model, outputStream);


    }

    protected void runQuery(String queryString, Model model, OutputStream outputStream) {

        Query query = QueryFactory.create(queryString);

        // Execute the query and obtain results
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();

        // Output query results
        ResultSetFormatter.out(outputStream, results, query);

        // Important - free up resources used running the query
        qe.close();
    }
}
