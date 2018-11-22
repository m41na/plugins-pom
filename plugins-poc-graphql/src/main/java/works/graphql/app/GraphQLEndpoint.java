package works.graphql.app;

import javax.servlet.annotation.WebServlet;

import com.coxautodev.graphql.tools.SchemaParser;

import graphql.schema.GraphQLSchema;
import graphql.servlet.SimpleGraphQLServlet;
import works.graphql.config.AppContext;
import works.graphql.dao.LinkRepository;
import works.graphql.resolver.Mutation;
import works.graphql.resolver.Query;
import works.graphql.service.AccountService;

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
    	LinkRepository linkRepo = ctx.getBean(null, LinkRepository.class);
    	AccountService accountService = ctx.getBean(null, AccountService.class);
        return SchemaParser.newParser()
                .file("schema.graphqls")
                .resolvers(new Query(linkRepo, accountService), 
                		new Mutation(linkRepo, accountService))
                .build()
                .makeExecutableSchema();
    }
}
