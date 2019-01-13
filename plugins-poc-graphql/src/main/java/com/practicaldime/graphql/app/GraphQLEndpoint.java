package com.practicaldime.graphql.app;

import javax.servlet.annotation.WebServlet;

import com.coxautodev.graphql.tools.SchemaParser;

import graphql.schema.GraphQLSchema;
import graphql.servlet.SimpleGraphQLServlet;
import com.practicaldime.graphql.config.AppContext;
import com.practicaldime.graphql.resolver.Mutation;
import com.practicaldime.graphql.resolver.Query;

@WebServlet(urlPatterns = "/graphql")
public class GraphQLEndpoint extends SimpleGraphQLServlet {

	private static final long serialVersionUID = 1L;
	private static AppContext ctx;

	public GraphQLEndpoint() {
        super(buildSchema());
    }

    private static GraphQLSchema buildSchema() {
    	ctx = new AppContext();
    	ctx.init();
    	//get bean
    	Query query = ctx.getBean(null, Query.class);
    	Mutation mutation = ctx.getBean(null, Mutation.class);
        return SchemaParser.newParser()
                .file("schema.graphqls")
                .resolvers(query, mutation)
                .build()
                .makeExecutableSchema();
    }
}
